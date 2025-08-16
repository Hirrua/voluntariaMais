package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.email.EmailRequest;
import com.svg.voluntariado.domain.dto.subscription.SubscriptionResponse;
import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;
import com.svg.voluntariado.exceptions.*;
import com.svg.voluntariado.projection.SubscriptionProjection;
import com.svg.voluntariado.repositories.ActivityRepository;
import com.svg.voluntariado.repositories.SubscriptionRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @Autowired
    public SubscriptionService(UserRepository userRepository,
                               ActivityRepository activityRepository,
                               SubscriptionRepository subscriptionRepository,
                               EmailService emailService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Long create(Long idAtividade, Long idUser) throws ActivityNotFoundException, UserNotFoundException {
        try {
            var activity = activityRepository.findById(idAtividade).orElseThrow(ActivityNotFoundException::new);
            var user = userRepository.findByIdIfProfileExists(idUser)
                    .orElseThrow(() -> new ProfileNotFoundException("Usuário não encontrado ou não possui perfil de voluntário."));

            if (activity.getVagasPreenchidasAtividade() >= activity.getVagasTotais()) {
                throw new IllegalArgumentException("Não há mais vagas para esta atividade.");
            }

            var newSubscription = subscriptionRepository.save(new InscricaoEntity(user, activity));
            subscriptionRepository.save(newSubscription);

            String confirmationUrl = "https://prime-free-tiger.ngrok-free.app/api/inscricao/confirmar?token=" + newSubscription.getTokenConfirmacao();
            Map<String, Object> emailVar = new HashMap<>();
            emailVar.put("userName", user.getNome());
            emailVar.put("activityName", activity.getNomeAtividade());
            emailVar.put("confirmationUrl", confirmationUrl);

            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Confirme sua inscrição - " + activity.getNomeAtividade(),
                    "send-email.html",
                    emailVar
            );

            return newSubscription.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new FilledSubscriptionException();
        }
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getByActivity(Long idAtividade) throws UserNotFoundException, SubscriptionNotFoundException {

        List<SubscriptionProjection> projections = subscriptionRepository.findSubscriptionFlatten(idAtividade);
        if (projections.isEmpty()) {
            throw new SubscriptionNotFoundException();
        }

        return projections.stream()
                .map(p -> new SubscriptionResponse(
                        p.getIdInscricao(),
                        p.getDataInscricao(),
                        p.getStatus(),
                        new InfoUserSubscription(
                                p.getUsuarioId(),
                                p.getUsuarioNome(),
                                p.getUsuarioEmail()
                        )
                )
        ).toList();
    }

    @Transactional
    public void subscriptionConfirm(String token) {
        var subscription = subscriptionRepository.findByTokenConfirmacao(token)
                .orElseThrow(() -> new TokenNotFoundException("Token de confirmação inválido."));

        if (subscription.getDataExpiracaoToken().isBefore(OffsetDateTime.now())) {
            throw new ExpiredTokenException("Token de confirmação expirado.");
        }

        if (StatusInscricaoEnum.CONFIRMADA.equals(subscription.getStatus())) {
            throw new SubscriptionConfirmedException("Esta inscrição já foi confirmada.");
        }

        if (StatusInscricaoEnum.CONCLUIDA_PARTICIPACAO.equals(subscription.getStatus())) {
            throw new SubscriptionConfirmedException("Você já concluiu essa atividade.");
        }

        var activity = subscription.getAtividade();
        if (activity.getVagasPreenchidasAtividade() >= activity.getVagasTotais()) {
            throw new FilledSubscriptionException("Desculpe, as vagas para esta atividade acabaram de se esgotar.");
        }

        subscription.setStatus(StatusInscricaoEnum.CONFIRMADA);
        subscription.setTokenConfirmacao(null);
        subscription.setDataExpiracaoToken(null);

        activity.setVagasPreenchidasAtividade(activity.getVagasPreenchidasAtividade() + 1);
        activityRepository.save(activity);

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void subscriptionRevoke(Long idSubscription, Long idUser) {
        var subEntity = subscriptionRepository.findById(idSubscription)
                .orElseThrow(SubscriptionNotFoundException::new);

        if (!subEntity.getUsuario().getId().equals(idUser)) {
            throw new UserUnauthorizedException();
        }

        switch (subEntity.getStatus()) {

            case CONFIRMADA:
                subEntity.setStatus(StatusInscricaoEnum.CANCELADA_PELO_VOLUNTARIO);
                var activity = subEntity.getAtividade();
                activity.setVagasPreenchidasAtividade(activity.getVagasPreenchidasAtividade() - 1);
                activity.setVagasTotais(activity.getVagasTotais() + 1);
                subscriptionRepository.save(subEntity);
                break;

            case PENDENTE:
                subEntity.setStatus(StatusInscricaoEnum.CANCELADA_PELO_VOLUNTARIO);
                subscriptionRepository.save(subEntity);
                break;

            case CONCLUIDA_PARTICIPACAO:
                throw new SubscriptionConfirmedException("Você já concluiu essa atividade e não pode cancelar a inscrição.");

            case CANCELADA_PELO_VOLUNTARIO:
                throw new SubscriptionConfirmedException("Inscrição já foi cancelada");

            case RECUSADA_PELA_ONG:
                throw new IllegalStateException("Esta inscrição já se encontra em um estado cancelado ou recusado.");

            default:
                throw new IllegalStateException("O status atual da inscrição não permite o cancelamento.");
        }
    }
}

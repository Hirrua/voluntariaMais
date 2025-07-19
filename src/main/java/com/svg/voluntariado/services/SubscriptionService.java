package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.email.EmailRequest;
import com.svg.voluntariado.domain.dto.subscription.SubscriptionResponse;
import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.exceptions.ActivityNotFoundException;
import com.svg.voluntariado.exceptions.ProfileNotFoundException;
import com.svg.voluntariado.exceptions.SubscriptionNotFoundException;
import com.svg.voluntariado.exceptions.UserNotFoundException;
import com.svg.voluntariado.projection.SubscriptionProjection;
import com.svg.voluntariado.repositories.ActivityRepository;
import com.svg.voluntariado.repositories.SubscriptionRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        var activity = activityRepository.findById(idAtividade).orElseThrow(ActivityNotFoundException::new);
        var user = userRepository.findByIdIfProfileExists(idUser)
                .orElseThrow(() -> new ProfileNotFoundException("Usuário não encontrado ou não possui perfil de voluntário."));

        if (activity.getVagasDisponiveisAtividade() <= activity.getVagasPreenchidasAtividade()) {
            throw new IllegalArgumentException();
        }

        var newSubscription = subscriptionRepository.save(new InscricaoEntity(user, activity));
        activity.setVagasPreenchidasAtividade(activity.getVagasPreenchidasAtividade() + 1);
        activity.setVagasDisponiveisAtividade(activity.getVagasDisponiveisAtividade() - 1);
        activityRepository.save(activity);

        emailService.sendEmail(new EmailRequest(user.getEmail(),
                "Confirme sua inscrição - " + activity.getNomeAtividade(),
                "Olá " + user.getNome() + "Por favor, confirme a sua participação!"));
        return newSubscription.getId();
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
                                p.getUsuarioNome(),
                                p.getUsuarioEmail()
                        )
                )
        ).toList();
    }
}

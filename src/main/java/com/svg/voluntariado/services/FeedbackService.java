package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.feedback.InsertFeedbackRequest;
import com.svg.voluntariado.domain.dto.feedback.InsertFeedbackResponse;
import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.entities.FeedbackEntity;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;
import com.svg.voluntariado.exceptions.InvalidScoreException;
import com.svg.voluntariado.repositories.FeedbackRepository;
import com.svg.voluntariado.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, SubscriptionRepository subscriptionRepository) {
        this.feedbackRepository = feedbackRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public InsertFeedbackResponse create(InsertFeedbackRequest insertFeedbackRequest, Long idSubscription, Long idUser) throws AccessDeniedException {
        var projection = subscriptionRepository.findOneSubscriptionFlatten(idSubscription)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));

        if (insertFeedbackRequest.nota() > 5 || insertFeedbackRequest.nota() < 0) {
            throw new InvalidScoreException();
        }

        if (!projection.getUsuarioId().equals(idUser)) {
            throw new AccessDeniedException("Você não está matriculado nessa atividade");
        }

        if (!projection.getStatus().equals(StatusInscricaoEnum.CONCLUIDA_PARTICIPACAO)) {
            throw new IllegalArgumentException("Atividade ainda não foi concluída");
        }

        var subscriptionReference = subscriptionRepository.getReferenceById(idSubscription);

        var feedEntity = new FeedbackEntity(
                insertFeedbackRequest.nota(),
                insertFeedbackRequest.comentario(),
                subscriptionReference
        );

        var saved = feedbackRepository.save(feedEntity);

        var userInfo = new InfoUserSubscription(
                projection.getUsuarioId(),
                projection.getUsuarioNome(),
                projection.getUsuarioEmail()
        );

        return new InsertFeedbackResponse(
                saved.getId(),
                saved.getNota(),
                saved.getDataFeedback(),
                userInfo
        );
    }
}

package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.feedback.InsertFeedbackRequest;
import com.svg.voluntariado.domain.dto.feedback.InsertFeedbackResponse;
import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.entities.FeedbackEntity;
import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;
import com.svg.voluntariado.exceptions.InvalidScoreException;
import com.svg.voluntariado.exceptions.UserUnauthorizedException;
import com.svg.voluntariado.mapper.FeedbackMapper;
import com.svg.voluntariado.projection.SubscriptionProjection;
import com.svg.voluntariado.repositories.FeedbackRepository;
import com.svg.voluntariado.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FeedbackMapper feedbackMapper;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, SubscriptionRepository subscriptionRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.feedbackMapper = feedbackMapper;
    }

    @Transactional
    public InsertFeedbackResponse create(InsertFeedbackRequest insertFeedbackRequest, Long idSubscription, Long idUser) {
        var projection = subscriptionRepository.findOneSubscriptionFlatten(idSubscription)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));

        if (insertFeedbackRequest.nota() > 5 || insertFeedbackRequest.nota() < 0) {
            throw new InvalidScoreException();
        }

        if (!projection.getUsuarioId().equals(idUser)) {
            throw new UserUnauthorizedException ("Você não está matriculado nessa atividade");
        }

        if (!projection.getStatus().equals(StatusInscricaoEnum.CONCLUIDA_PARTICIPACAO)) {
            throw new IllegalArgumentException("Atividade ainda não foi concluída");
        }

        var subscriptionReference = subscriptionRepository.getReferenceById(idSubscription);
        var feedEntity = toFeedbackEntity(insertFeedbackRequest, subscriptionReference);
        var saved = feedbackRepository.save(feedEntity);
        var userInfo = toInfoUserSubscription(projection);

        return toInsertFeedbackResponse(saved, userInfo);
    }

    @Transactional
    public void update(InsertFeedbackRequest update, Long idFeedback, Long idUser) {
        var feed = feedbackRepository.getReferenceById(idFeedback);
        var projection = subscriptionRepository.findOneSubscriptionFlatten(feed.getInscricao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));

        if (!projection.getUsuarioId().equals(idUser)) {
            throw new UserUnauthorizedException("Você não está matriculado nessa atividade");
        }

        if (update.nota() > 5 || update.nota() < 0) {
            throw new InvalidScoreException();
        }

        var feedEntity = feedbackMapper.toFeedbackEntity(update, feed);
        feedbackRepository.save(feedEntity);
    }

    @Transactional
    public void delete(Long idFeedback, Long idUser) {
        var feed = feedbackRepository.getReferenceById(idFeedback);
        var projection = subscriptionRepository.findOneSubscriptionFlatten(feed.getInscricao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));

        if (!projection.getUsuarioId().equals(idUser)) {
            throw new UserUnauthorizedException("Você não está matriculado nessa atividade");
        }

        feedbackRepository.delete(feed);
    }

    public FeedbackEntity toFeedbackEntity(InsertFeedbackRequest insertFeedbackRequest, InscricaoEntity subscriptionReference) {
        return new FeedbackEntity(
                insertFeedbackRequest.nota(),
                insertFeedbackRequest.comentario(),
                subscriptionReference
        );
    }

    public InfoUserSubscription toInfoUserSubscription(SubscriptionProjection projection) {
        return new InfoUserSubscription(
                projection.getUsuarioId(),
                projection.getUsuarioNome(),
                projection.getUsuarioEmail()
        );
    }

    public InsertFeedbackResponse toInsertFeedbackResponse(FeedbackEntity saved, InfoUserSubscription userInfo) {
        return new InsertFeedbackResponse(
                saved.getId(),
                saved.getNota(),
                saved.getDataFeedback(),
                userInfo
        );
    }
}

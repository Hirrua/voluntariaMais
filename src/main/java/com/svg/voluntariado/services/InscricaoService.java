package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.atividade.InfoActivitySubscription;
import com.svg.voluntariado.domain.dto.inscricao.InscricaoResponse;
import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.exceptions.ActivityNotFoundException;
import com.svg.voluntariado.exceptions.ProfileNotFoundException;
import com.svg.voluntariado.exceptions.SubscriptionNotFoundException;
import com.svg.voluntariado.exceptions.UserNotFoundException;
import com.svg.voluntariado.repositories.ActivityRepository;
import com.svg.voluntariado.repositories.SubscriptionRepository;
import com.svg.voluntariado.repositories.VolunteerProfileRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    private final UserRepository userRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final ActivityRepository activityRepository;
    private final SubscriptionRepository subscriptionRepository;

    public InscricaoService(UserRepository userRepository,
                            VolunteerProfileRepository volunteerProfileRepository,
                            ActivityRepository activityRepository,
                            SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.volunteerProfileRepository = volunteerProfileRepository;
    }

    @Transactional
    public Long create(Long idAtividade, Long idUser) throws ActivityNotFoundException, UserNotFoundException {
        var activity = activityRepository.findById(idAtividade).orElseThrow(ActivityNotFoundException::new);
        var user = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

        var profile = volunteerProfileRepository.findById(idUser);
        if (profile.isEmpty()) {
            throw  new ProfileNotFoundException();
        }

        if (activity.getVagasDisponiveisAtividade() <= activity.getVagasPreenchidasAtividade()) {
            throw new IllegalArgumentException();
        }

        var newSubscription = subscriptionRepository.save(new InscricaoEntity(user, activity));
        activity.setVagasPreenchidasAtividade(activity.getVagasPreenchidasAtividade() + 1);
        activity.setVagasDisponiveisAtividade(activity.getVagasDisponiveisAtividade() - 1);
        activityRepository.save(activity);
        return newSubscription.getId();
    }

    @Transactional(readOnly = true)
    public InscricaoResponse get(Long id) throws ActivityNotFoundException, UserNotFoundException, SubscriptionNotFoundException {
        var subscription = subscriptionRepository.findById(id).orElseThrow(SubscriptionNotFoundException::new);

        var user = userRepository.findById(subscription.getUsuario().getId()).orElseThrow(UserNotFoundException::new);
        InfoUserSubscription infoUserSubscription = new InfoUserSubscription(user.getId(), user.getNome(), user.getEmail());

        var activity = activityRepository.findById(subscription.getAtividade().getId()).orElseThrow(ActivityNotFoundException::new);
        InfoActivitySubscription infoActivitySubscription = new InfoActivitySubscription(activity.getId(), activity.getNomeAtividade());

        return new InscricaoResponse(
                subscription.getId(),
                subscription.getDataInscricao(),
                subscription.getStatus(),
                infoUserSubscription,
                infoActivitySubscription
        );
    }
}

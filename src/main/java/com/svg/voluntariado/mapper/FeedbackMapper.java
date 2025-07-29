package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.feedback.InsertFeedbackRequest;
import com.svg.voluntariado.domain.entities.FeedbackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FeedbackMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inscricao", ignore = true)
    @Mapping(target = "dataFeedback", ignore = true)
    FeedbackEntity toFeedbackEntity(InsertFeedbackRequest request);
}

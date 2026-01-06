package com.svg.voluntariado.domain.dto.project;

import com.svg.voluntariado.domain.dto.activity.SimpleInfoActivityResponse;
import com.svg.voluntariado.domain.dto.ong.OngContextResponse;

import java.util.List;

public record OngProjectAndActivityInfo(
        SimpleInfoProjectResponse simpleInfoProjectResponse,
        OngContextResponse ongContextResponse,
        List<SimpleInfoActivityResponse> simpleInfoActivityResponse
) {
}

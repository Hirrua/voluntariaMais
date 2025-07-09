package com.svg.voluntariado.domain.dto.atividade;

import com.svg.voluntariado.domain.dto.ong.OngContextoResponse;
import com.svg.voluntariado.domain.dto.projeto.ProjetoContextoResponse;
import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.OffsetDateTime;

public record InfoAtividadeResponse(
        Long id,
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicio,
        OffsetDateTime dataHoraFim,
        String localAtividade,
        Integer vagasDisponiveisAtividade,
        Integer vagasPreenchidasAtividade,
        OffsetDateTime dataCriacao,
        ProjetoContextoResponse projeto,
        OngContextoResponse ong
) {
}

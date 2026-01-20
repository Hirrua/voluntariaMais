package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.ong.*;
import com.svg.voluntariado.domain.dto.project.SimpleInfoProjectResponse;
import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.entities.RoleEntity;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;
import com.svg.voluntariado.exceptions.ExpiredTokenException;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.TokenNotFoundException;
import com.svg.voluntariado.exceptions.UserUnauthorizedException;
import com.svg.voluntariado.mapper.OngMapper;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.RoleRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final OngMapper ongMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final StorageService storageService;

    private static final long APPROVAL_EXPIRY_DAYS = 2L;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.ong.approval.base-url:}")
    private String approvalBaseUrl;

    public OngService(OngRepository ongRepository, OngMapper ongMapper, UserRepository userRepository,
                      RoleRepository roleRepository, EmailService emailService, StorageService storageService) {
        this.userRepository = userRepository;
        this.ongRepository = ongRepository;
        this.ongMapper = ongMapper;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.storageService = storageService;
    }

    @Transactional
    public Long create(CreateOngRequest createOngRequest, Long requesterId, boolean isAdminPlataforma) {
        if (!isAdminPlataforma && !createOngRequest.idUsuarioResponsavel().equals(requesterId)) {
            throw new UserUnauthorizedException();
        }

        UsuarioEntity responsibleUser = userRepository.findById(createOngRequest.idUsuarioResponsavel())
                .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado com ID: " + createOngRequest.idUsuarioResponsavel())
        );

        var ongEntity = ongMapper.toOngEntity(createOngRequest);
        ongEntity.setUsuarioResponsavel(responsibleUser);
        if (isAdminPlataforma) {
            approveOng(ongEntity, responsibleUser);
        } else {
            prepareApprovalRequest(ongEntity);
        }
        var newOng = ongRepository.save(ongEntity);

        if (isAdminPlataforma) {
            userRepository.save(responsibleUser);
        } else {
            sendApprovalEmail(newOng, responsibleUser);
        }

        return newOng.getId();
    }

    public InfoOngResponse get(Long id) {
        var ong = ongRepository.findByIdAndStatus(id, StatusAprovacaoOngEnum.APROVADA)
                .orElseThrow(OngNotFoundException::new);

        return ongMapper.toInfoOngResponse(ong);
    }

    @Transactional(readOnly = true)
    public InfoOngAndProjectResponse getForAdmin(Long adminId) {
        OngEntity ong = getOngByAdminId(adminId);
        return withPublicUrl(ongMapper.toInfoOngAndProjectResponse(ong));
    }

    public InfoOngAndProjectResponse findOngAndProjects(Long idOng, Long requesterId, boolean isAdminOng, boolean isAdminPlataforma) {
        var ongEntity = ongRepository.findById(idOng)
                .orElseThrow(OngNotFoundException::new);

        var response = ongMapper.toInfoOngAndProjectResponse(ongEntity);
        boolean canViewProjects = StatusAprovacaoOngEnum.APROVADA.equals(ongEntity.getStatus())
                || isAdminPlataforma
                || (isAdminOng
                    && requesterId != null
                    && ongEntity.getUsuarioResponsavel() != null
                    && requesterId.equals(ongEntity.getUsuarioResponsavel().getId()));

        if (!canViewProjects) {
            response = new InfoOngAndProjectResponse(
                    response.nomeOng(),
                    response.descricao(),
                    response.emailContatoOng(),
                    response.telefoneOng(),
                    response.website(),
                    response.logoUrl(),
                    response.dataFundacao(),
                    response.status(),
                    response.dataCriacaoRegistro(),
                    response.endereco(),
                    List.of()
            );
        }

        return withPublicUrl(response);
    }

    public List<ListOngResponse> findAllOng(int page, int itens) {
        Page<OngEntity> ongEntities = ongRepository.findAllByStatus(StatusAprovacaoOngEnum.APROVADA, PageRequest.of(page, itens));
        if (ongEntities.isEmpty()) {
            throw new OngNotFoundException("Nenhuma ong foi criada até o momento");
        }
        return ongMapper.toListOngResponse(ongEntities);
    }

    @Transactional
    public InfoOngResponse update(Long idOng, Long idAdmin, UpdateInfoOngRequest updateInfoOngRequest) {
        var ong = ongRepository.findById(idOng)
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Acesso negado.");
        }

        var ongEntity = ongMapper.toOngEntity(updateInfoOngRequest, ong);
        var saved = ongRepository.save(ongEntity);
        return ongMapper.toInfoOngResponse(saved);
    }

    public void delete(Long idOng, Long idAdmin) {
        var ong = ongRepository.findById(idOng)
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Acesso negado.");
        }

        ongRepository.delete(ong);
    }

    @Transactional
    public InfoOngAndProjectResponse updateForAdmin(Long adminId, UpdateInfoOngRequest updateInfoOngRequest) {
        var ong = getOngByAdminId(adminId);
        var ongEntity = ongMapper.toOngEntity(updateInfoOngRequest, ong);
        var saved = ongRepository.save(ongEntity);
        return withPublicUrl(ongMapper.toInfoOngAndProjectResponse(saved));
    }

    private InfoOngAndProjectResponse withPublicUrl(InfoOngAndProjectResponse response) {
        List<SimpleInfoProjectResponse> projects = response.projectResponse().stream()
                .map(this::withPublicUrl)
                .toList();

        return new InfoOngAndProjectResponse(
                response.nomeOng(),
                response.descricao(),
                response.emailContatoOng(),
                response.telefoneOng(),
                response.website(),
                storageService.buildPublicUrl(response.logoUrl()),
                response.dataFundacao(),
                response.status(),
                response.dataCriacaoRegistro(),
                response.endereco(),
                projects
        );
    }

    private OngEntity getOngByAdminId(Long adminId) {
        return ongRepository.findByUsuarioResponsavelId(adminId)
                .orElseThrow(OngNotFoundException::new);
    }

    private SimpleInfoProjectResponse withPublicUrl(SimpleInfoProjectResponse response) {
        return new SimpleInfoProjectResponse(
                response.id(),
                response.nome(),
                response.objetivo(),
                response.publicoAlvo(),
                storageService.buildPublicUrl(response.urlImagemDestaque())
        );
    }

    @Transactional
    public void approveByToken(String token) {
        var ong = ongRepository.findByTokenAprovacao(token)
                .orElseThrow(() -> new TokenNotFoundException("Token de aprovação inválido."));

        if (ong.getDataExpiracaoAprovacao() == null || ong.getDataExpiracaoAprovacao().isBefore(OffsetDateTime.now())) {
            throw new ExpiredTokenException("Token de aprovação expirado.");
        }

        if (!StatusAprovacaoOngEnum.PENDENTE.equals(ong.getStatus())) {
            throw new TokenNotFoundException("Solicitação já analisada.");
        }

        UsuarioEntity responsibleUser = ong.getUsuarioResponsavel();
        approveOng(ong, responsibleUser);
        ongRepository.save(ong);
        userRepository.save(responsibleUser);
    }

    @Transactional
    public void rejectByToken(String token) {
        var ong = ongRepository.findByTokenAprovacao(token)
                .orElseThrow(() -> new TokenNotFoundException("Token de aprovação inválido."));

        if (ong.getDataExpiracaoAprovacao() == null || ong.getDataExpiracaoAprovacao().isBefore(OffsetDateTime.now())) {
            throw new ExpiredTokenException("Token de aprovação expirado.");
        }

        if (!StatusAprovacaoOngEnum.PENDENTE.equals(ong.getStatus())) {
            throw new TokenNotFoundException("Solicitação já analisada.");
        }

        ong.setStatus(StatusAprovacaoOngEnum.REJEITADA);
        ong.setDataAprovacaoRejeicao(OffsetDateTime.now());
        ong.setTokenAprovacao(null);
        ong.setDataExpiracaoAprovacao(null);
        ongRepository.save(ong);
    }

    private void prepareApprovalRequest(OngEntity ongEntity) {
        ongEntity.setStatus(StatusAprovacaoOngEnum.PENDENTE);
        ongEntity.setTokenAprovacao(UUID.randomUUID().toString());
        ongEntity.setDataExpiracaoAprovacao(OffsetDateTime.now().plusDays(APPROVAL_EXPIRY_DAYS));
    }

    private void approveOng(OngEntity ongEntity, UsuarioEntity responsibleUser) {
        ongEntity.setStatus(StatusAprovacaoOngEnum.APROVADA);
        ongEntity.setDataAprovacaoRejeicao(OffsetDateTime.now());
        ongEntity.setTokenAprovacao(null);
        ongEntity.setDataExpiracaoAprovacao(null);
        ensureAdminOngRole(responsibleUser);
    }

    private void ensureAdminOngRole(UsuarioEntity user) {
        RoleEntity adminOngRole = roleRepository.findByNome("ROLE_ADMIN_ONG");
        if (adminOngRole == null) {
            throw new IllegalStateException("Role ROLE_ADMIN_ONG não encontrada.");
        }
        if (user.getRoles().stream().noneMatch(role -> role.getNome().equals(adminOngRole.getNome()))) {
            user.getRoles().add(adminOngRole);
        }
    }

    private void sendApprovalEmail(OngEntity ongEntity, UsuarioEntity responsibleUser) {
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalStateException("Email do superadmin não configurado.");
        }

        String approvalUrl = buildApprovalUrl(ongEntity.getTokenAprovacao(), true);
        String rejectionUrl = buildApprovalUrl(ongEntity.getTokenAprovacao(), false);

        Map<String, Object> emailVar = new HashMap<>();
        emailVar.put("ongName", ongEntity.getNomeOng());
        emailVar.put("cnpj", ongEntity.getCnpj());
        emailVar.put("responsavelNome", responsibleUser.getNome() + " " + responsibleUser.getSobrenome());
        emailVar.put("responsavelEmail", responsibleUser.getEmail());
        emailVar.put("responsavelId", responsibleUser.getId());
        emailVar.put("approvalUrl", approvalUrl);
        emailVar.put("rejectionUrl", rejectionUrl);

        emailService.sendHtmlEmail(
                adminEmail,
                "Aprovação de ONG - " + ongEntity.getNomeOng(),
                "ong-approval-request.html",
                emailVar
        );
    }

    private String buildApprovalUrl(String token, boolean approve) {
        String baseUrl = normalizeBaseUrl(approvalBaseUrl);
        String path = approve ? "/api/ong/aprovacao" : "/api/ong/rejeicao";
        return baseUrl + path + "?token=" + token;
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }

        String trimmed = baseUrl.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }

        return trimmed;
    }
}

-- Seed de dados para ambiente local/desenvolvimento.
-- Senha padrao dos usuarios confirmados: Senha@123
-- Hash BCrypt (compativel com Spring Security): $2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC

INSERT INTO tb_roles (nome) VALUES
('ROLE_VOLUNTARIO'),
('ROLE_ADMIN_ONG'),
('ROLE_ADMIN_PLATAFORMA')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO tb_usuarios (
    nome,
    sobrenome,
    email,
    senha,
    cpf,
    logradouro_usuario,
    bairro_usuario,
    complemento_usuario,
    cidade_usuario,
    estado_usuario,
    cep_usuario,
    ativo,
    status_usuario,
    token_confirmacao,
    data_expiracao_token,
    data_cadastro
) VALUES
('Admin', 'Plataforma', 'admin.plataforma@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000001', 'Rua das Palmeiras', 'Centro', 'Sala 101', 'Sao Paulo', 'SP', '01001000', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '90 days'),
('Ana', 'Ferreira', 'ana.ong@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000002', 'Rua Augusta', 'Consolacao', 'Apto 12', 'Sao Paulo', 'SP', '01305000', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '70 days'),
('Bruno', 'Matos', 'bruno.ong@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000003', 'Rua da Bahia', 'Savassi', 'Casa', 'Belo Horizonte', 'MG', '30160010', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '65 days'),
('Carla', 'Nascimento', 'carla.responsavel@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000004', 'Rua do Porto', 'Pina', 'Bloco B', 'Recife', 'PE', '51011000', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '50 days'),
('Diego', 'Lima', 'diego.voluntario@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000005', 'Rua das Flores', 'Centro', 'Casa 2', 'Curitiba', 'PR', '80010000', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '45 days'),
('Elisa', 'Souza', 'elisa.voluntaria@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000006', 'Rua Santos Dumont', 'Aldeota', 'Apto 403', 'Fortaleza', 'CE', '60150010', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '43 days'),
('Fernanda', 'Oliveira', 'fernanda.voluntaria@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000007', 'Rua do Sol', 'Boa Vista', 'Apto 08', 'Recife', 'PE', '50070000', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '40 days'),
('Gustavo', 'Ribeiro', 'gustavo.voluntario@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000008', 'Av. Beira Mar', 'Meireles', 'Apto 701', 'Fortaleza', 'CE', '60165120', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '38 days'),
('Renata', 'Alves', 'renata.responsavel@voluntaria.local', '$2y$10$A1sGzPeW8mf7FaSLmNn1FeJ31z3GOOw9YYz84zjW4YuOE53MPQCwC', '10000000010', 'Av. Atlantica', 'Copacabana', 'Sala 9', 'Rio de Janeiro', 'RJ', '22021001', TRUE, 'CONFIRMADO', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '30 days')
ON CONFLICT (email) DO NOTHING;

INSERT INTO tb_usuario_roles (id_usuario, id_role)
SELECT u.id_usuario, r.id
FROM tb_usuarios u
JOIN tb_roles r ON r.nome = 'ROLE_VOLUNTARIO'
WHERE u.email IN (
    'ana.ong@voluntaria.local',
    'bruno.ong@voluntaria.local',
    'carla.responsavel@voluntaria.local',
    'diego.voluntario@voluntaria.local',
    'elisa.voluntaria@voluntaria.local',
    'fernanda.voluntaria@voluntaria.local',
    'gustavo.voluntario@voluntaria.local',
    'joao.pendente@voluntaria.local',
    'renata.responsavel@voluntaria.local'
)
ON CONFLICT (id_usuario, id_role) DO NOTHING;

INSERT INTO tb_usuario_roles (id_usuario, id_role)
SELECT u.id_usuario, r.id
FROM tb_usuarios u
JOIN tb_roles r ON r.nome = 'ROLE_ADMIN_ONG'
WHERE u.email IN (
    'ana.ong@voluntaria.local',
    'bruno.ong@voluntaria.local'
)
ON CONFLICT (id_usuario, id_role) DO NOTHING;

INSERT INTO tb_usuario_roles (id_usuario, id_role)
SELECT u.id_usuario, r.id
FROM tb_usuarios u
JOIN tb_roles r ON r.nome = 'ROLE_ADMIN_PLATAFORMA'
WHERE u.email = 'admin.plataforma@voluntaria.local'
ON CONFLICT (id_usuario, id_role) DO NOTHING;

INSERT INTO tb_perfil_voluntario (
    id_usuario,
    bio,
    disponibilidade,
    data_nascimento,
    telefone_contato,
    foto_perfil_url
)
SELECT
    u.id_usuario,
    'Voluntario focado em acoes sociais e apoio comunitario.',
    'Finais de semana',
    DATE '1994-03-12',
    '11987654321',
    'profiles/diego.jpg'
FROM tb_usuarios u
WHERE u.email = 'diego.voluntario@voluntaria.local'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO tb_perfil_voluntario (
    id_usuario,
    bio,
    disponibilidade,
    data_nascimento,
    telefone_contato,
    foto_perfil_url
)
SELECT
    u.id_usuario,
    'Atuo em cozinhas solidarias e distribuicao de alimentos.',
    'Noites de semana',
    DATE '1997-08-22',
    '11986543210',
    'profiles/elisa.jpg'
FROM tb_usuarios u
WHERE u.email = 'elisa.voluntaria@voluntaria.local'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO tb_perfil_voluntario (
    id_usuario,
    bio,
    disponibilidade,
    data_nascimento,
    telefone_contato,
    foto_perfil_url
)
SELECT
    u.id_usuario,
    'Gosto de projetos educacionais e oficinas para jovens.',
    'Tardes de sabado',
    DATE '1991-11-01',
    '21997654321',
    'profiles/fernanda.jpg'
FROM tb_usuarios u
WHERE u.email = 'fernanda.voluntaria@voluntaria.local'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO tb_perfil_voluntario (
    id_usuario,
    bio,
    disponibilidade,
    data_nascimento,
    telefone_contato,
    foto_perfil_url
)
SELECT
    u.id_usuario,
    'Experiencia com mutiroes e logistica de eventos comunitarios.',
    'Manhas de domingo',
    DATE '1989-05-14',
    '31999887766',
    'profiles/gustavo.jpg'
FROM tb_usuarios u
WHERE u.email = 'gustavo.voluntario@voluntaria.local'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO tb_perfil_voluntario (
    id_usuario,
    bio,
    disponibilidade,
    data_nascimento,
    telefone_contato,
    foto_perfil_url
)
SELECT
    u.id_usuario,
    'Responsavel por mobilizacao de voluntarios para acoes de impacto.',
    'Horario comercial',
    DATE '1990-02-02',
    '81993456789',
    'profiles/carla.jpg'
FROM tb_usuarios u
WHERE u.email = 'carla.responsavel@voluntaria.local'
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO tb_habilidades (nome_habilidade, descricao_habilidade) VALUES
('Primeiros Socorros', 'Atendimento inicial em situacoes de urgencia.'),
('Cozinha Comunitaria', 'Preparo e organizacao de refeicoes solidarias.'),
('Apoio Escolar', 'Acompanhamento de estudo e reforco educacional.'),
('Jardinagem', 'Plantio e manutencao de hortas comunitarias.'),
('Organizacao de Eventos', 'Planejamento e execucao de eventos sociais.'),
('Gestao de Voluntarios', 'Coordenacao de times e escalas de voluntariado.')
ON CONFLICT (nome_habilidade) DO NOTHING;

INSERT INTO tb_voluntario_habilidade (id_usuario, id_habilidade)
SELECT u.id_usuario, h.id_habilidade
FROM tb_usuarios u
JOIN tb_habilidades h ON h.nome_habilidade = 'Primeiros Socorros'
WHERE u.email = 'diego.voluntario@voluntaria.local'
ON CONFLICT (id_usuario, id_habilidade) DO NOTHING;

INSERT INTO tb_voluntario_habilidade (id_usuario, id_habilidade)
SELECT u.id_usuario, h.id_habilidade
FROM tb_usuarios u
JOIN tb_habilidades h ON h.nome_habilidade = 'Organizacao de Eventos'
WHERE u.email = 'diego.voluntario@voluntaria.local'
ON CONFLICT (id_usuario, id_habilidade) DO NOTHING;

INSERT INTO tb_voluntario_habilidade (id_usuario, id_habilidade)
SELECT u.id_usuario, h.id_habilidade
FROM tb_usuarios u
JOIN tb_habilidades h ON h.nome_habilidade = 'Cozinha Comunitaria'
WHERE u.email IN ('elisa.voluntaria@voluntaria.local', 'gustavo.voluntario@voluntaria.local')
ON CONFLICT (id_usuario, id_habilidade) DO NOTHING;

INSERT INTO tb_voluntario_habilidade (id_usuario, id_habilidade)
SELECT u.id_usuario, h.id_habilidade
FROM tb_usuarios u
JOIN tb_habilidades h ON h.nome_habilidade = 'Apoio Escolar'
WHERE u.email = 'fernanda.voluntaria@voluntaria.local'
ON CONFLICT (id_usuario, id_habilidade) DO NOTHING;

INSERT INTO tb_voluntario_habilidade (id_usuario, id_habilidade)
SELECT u.id_usuario, h.id_habilidade
FROM tb_usuarios u
JOIN tb_habilidades h ON h.nome_habilidade = 'Jardinagem'
WHERE u.email IN ('fernanda.voluntaria@voluntaria.local', 'gustavo.voluntario@voluntaria.local')
ON CONFLICT (id_usuario, id_habilidade) DO NOTHING;

INSERT INTO tb_voluntario_habilidade (id_usuario, id_habilidade)
SELECT u.id_usuario, h.id_habilidade
FROM tb_usuarios u
JOIN tb_habilidades h ON h.nome_habilidade = 'Gestao de Voluntarios'
WHERE u.email = 'carla.responsavel@voluntaria.local'
ON CONFLICT (id_usuario, id_habilidade) DO NOTHING;

INSERT INTO tb_ong (
    id_usuario_responsavel,
    nome_ong,
    cnpj,
    descricao,
    logradouro_ong,
    bairro_ong,
    complemento_ong,
    cidade_ong,
    estado_ong,
    cep_ong,
    telefone_ong,
    email_contato_ong,
    website,
    data_fundacao,
    logo_url,
    status_aprovacao,
    data_criacao_registro,
    data_aprovacao_rejeicao,
    token_aprovacao,
    data_expiracao_aprovacao
)
SELECT
    u.id_usuario,
    'Instituto Maos Solidarias',
    '12345678000195',
    'ONG focada em seguranca alimentar e apoio a familias vulneraveis.',
    'Rua da Solidariedade',
    'Centro',
    'Galpao A',
    'Sao Paulo',
    'SP',
    '01018000',
    '1130034001',
    'contato@maossolidarias.org',
    'https://maossolidarias.org',
    DATE '2014-06-15',
    'ongs/maos-solidarias.png',
    'APROVADA',
    CURRENT_TIMESTAMP - INTERVAL '60 days',
    CURRENT_TIMESTAMP - INTERVAL '59 days',
    NULL,
    NULL
FROM tb_usuarios u
WHERE u.email = 'ana.ong@voluntaria.local'
AND NOT EXISTS (
    SELECT 1 FROM tb_ong o WHERE o.cnpj = '12345678000195'
);

INSERT INTO tb_ong (
    id_usuario_responsavel,
    nome_ong,
    cnpj,
    descricao,
    logradouro_ong,
    bairro_ong,
    complemento_ong,
    cidade_ong,
    estado_ong,
    cep_ong,
    telefone_ong,
    email_contato_ong,
    website,
    data_fundacao,
    logo_url,
    status_aprovacao,
    data_criacao_registro,
    data_aprovacao_rejeicao,
    token_aprovacao,
    data_expiracao_aprovacao
)
SELECT
    u.id_usuario,
    'Rede Verde Urbana',
    '22345678000180',
    'Iniciativas de sustentabilidade, hortas urbanas e educacao ambiental.',
    'Av. das Acacias',
    'Jardins',
    'Predio 2',
    'Belo Horizonte',
    'MG',
    '30120010',
    '3135567002',
    'contato@redevverde.org',
    'https://redeverde.org',
    DATE '2018-03-02',
    'ongs/rede-verde.png',
    'APROVADA',
    CURRENT_TIMESTAMP - INTERVAL '55 days',
    CURRENT_TIMESTAMP - INTERVAL '54 days',
    NULL,
    NULL
FROM tb_usuarios u
WHERE u.email = 'bruno.ong@voluntaria.local'
AND NOT EXISTS (
    SELECT 1 FROM tb_ong o WHERE o.cnpj = '22345678000180'
);

INSERT INTO tb_ong (
    id_usuario_responsavel,
    nome_ong,
    cnpj,
    descricao,
    logradouro_ong,
    bairro_ong,
    complemento_ong,
    cidade_ong,
    estado_ong,
    cep_ong,
    telefone_ong,
    email_contato_ong,
    website,
    data_fundacao,
    logo_url,
    status_aprovacao,
    data_criacao_registro,
    data_aprovacao_rejeicao,
    token_aprovacao,
    data_expiracao_aprovacao
)
SELECT
    u.id_usuario,
    'Projeto Horizonte',
    '32345678000165',
    'ONG em fase de analise para projetos de insercao social de jovens.',
    'Rua Horizonte',
    'Boa Vista',
    'Casa 10',
    'Recife',
    'PE',
    '50050010',
    '8134789003',
    'contato@horizonte.org',
    'https://horizonte.org',
    DATE '2021-01-10',
    'ongs/projeto-horizonte.png',
    'APROVADA',
    CURRENT_TIMESTAMP - INTERVAL '8 days',
    NULL,
    'token-aprovacao-ong-pendente-001',
    CURRENT_TIMESTAMP + INTERVAL '1 day'
FROM tb_usuarios u
WHERE u.email = 'carla.responsavel@voluntaria.local'
AND NOT EXISTS (
    SELECT 1 FROM tb_ong o WHERE o.cnpj = '32345678000165'
);

INSERT INTO tb_ong (
    id_usuario_responsavel,
    nome_ong,
    cnpj,
    descricao,
    logradouro_ong,
    bairro_ong,
    complemento_ong,
    cidade_ong,
    estado_ong,
    cep_ong,
    telefone_ong,
    email_contato_ong,
    website,
    data_fundacao,
    logo_url,
    status_aprovacao,
    data_criacao_registro,
    data_aprovacao_rejeicao,
    token_aprovacao,
    data_expiracao_aprovacao
)
SELECT
    u.id_usuario,
    'Instituto Futuro Vivo',
    '42345678000150',
    'Aulas gratuitas ensino médio.',
    'Rua do Carmo',
    'Centro',
    'Sala 3',
    'Rio de Janeiro',
    'RJ',
    '20011020',
    '2134567890',
    'contato@futurovivo.org',
    'https://futurovivo.org',
    DATE '2019-09-12',
    'ongs/futuro-vivo.png',
    'APROVADA',
    CURRENT_TIMESTAMP - INTERVAL '20 days',
    CURRENT_TIMESTAMP - INTERVAL '18 days',
    NULL,
    NULL
FROM tb_usuarios u
WHERE u.email = 'renata.responsavel@voluntaria.local'
AND NOT EXISTS (
    SELECT 1 FROM tb_ong o WHERE o.cnpj = '42345678000150'
);

-- 7) Projetos (apenas ONGs aprovadas)
INSERT INTO tb_projeto (
    id_ong,
    nome_projeto,
    descricao_detalhada,
    objetivo,
    publico_alvo,
    data_inicio_prevista,
    data_fim_prevista,
    status_projeto,
    logradouro_projeto,
    bairro_projeto,
    complemento_projeto,
    cidade_projeto,
    estado_projeto,
    cep_projeto,
    url_imagem_destaque,
    data_criacao,
    data_atualizacao
)
SELECT
    o.id_ong,
    'Cozinha Solidaria Centro',
    'Producao e distribuicao de refeicoes para pessoas em vulnerabilidade social.',
    'Distribuir 800 refeicoes por mes na regiao central.',
    'Pessoas em situacao de rua',
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE + INTERVAL '90 days',
    'EM_ANDAMENTO',
    'Rua da Solidariedade',
    'Centro',
    'Galpao A',
    'Sao Paulo',
    'SP',
    '01018000',
    'projects/cozinha-solidaria-centro.png',
    CURRENT_TIMESTAMP - INTERVAL '45 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM tb_ong o
WHERE o.cnpj = '12345678000195'
AND NOT EXISTS (
    SELECT 1 FROM tb_projeto p WHERE p.id_ong = o.id_ong AND p.nome_projeto = 'Cozinha Solidaria Centro'
);

INSERT INTO tb_projeto (
    id_ong,
    nome_projeto,
    descricao_detalhada,
    objetivo,
    publico_alvo,
    data_inicio_prevista,
    data_fim_prevista,
    status_projeto,
    logradouro_projeto,
    bairro_projeto,
    complemento_projeto,
    cidade_projeto,
    estado_projeto,
    cep_projeto,
    url_imagem_destaque,
    data_criacao,
    data_atualizacao
)
SELECT
    o.id_ong,
    'Mutirao de Inverno 2026',
    'Campanha de arrecadacao e distribuicao de roupas e cobertores.',
    'Ampliar cobertura de atendimento no periodo de frio.',
    'Familias em vulnerabilidade',
    CURRENT_DATE + INTERVAL '15 days',
    CURRENT_DATE + INTERVAL '90 days',
    'PLANEJAMENTO',
    'Av. Tiradentes',
    'Luz',
    'Centro social',
    'Sao Paulo',
    'SP',
    '01101010',
    'projects/mutirao-inverno.png',
    CURRENT_TIMESTAMP - INTERVAL '12 days',
    NULL
FROM tb_ong o
WHERE o.cnpj = '12345678000195'
AND NOT EXISTS (
    SELECT 1 FROM tb_projeto p WHERE p.id_ong = o.id_ong AND p.nome_projeto = 'Mutirao de Inverno 2026'
);

INSERT INTO tb_projeto (
    id_ong,
    nome_projeto,
    descricao_detalhada,
    objetivo,
    publico_alvo,
    data_inicio_prevista,
    data_fim_prevista,
    status_projeto,
    logradouro_projeto,
    bairro_projeto,
    complemento_projeto,
    cidade_projeto,
    estado_projeto,
    cep_projeto,
    url_imagem_destaque,
    data_criacao,
    data_atualizacao
)
SELECT
    o.id_ong,
    'Horta Comunitaria Bairro Sul',
    'Implantacao de horta urbana para producao de alimentos e educacao ambiental.',
    'Engajar moradores na producao local de alimentos.',
    'Moradores do bairro e escolas locais',
    CURRENT_DATE - INTERVAL '150 days',
    CURRENT_DATE - INTERVAL '10 days',
    'CONCLUIDO',
    'Rua das Hortencias',
    'Bairro Sul',
    'Praca central',
    'Belo Horizonte',
    'MG',
    '30550050',
    'projects/horta-bairro-sul.png',
    CURRENT_TIMESTAMP - INTERVAL '120 days',
    CURRENT_TIMESTAMP - INTERVAL '10 days'
FROM tb_ong o
WHERE o.cnpj = '22345678000180'
AND NOT EXISTS (
    SELECT 1 FROM tb_projeto p WHERE p.id_ong = o.id_ong AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
);

INSERT INTO tb_projeto (
    id_ong,
    nome_projeto,
    descricao_detalhada,
    objetivo,
    publico_alvo,
    data_inicio_prevista,
    data_fim_prevista,
    status_projeto,
    logradouro_projeto,
    bairro_projeto,
    complemento_projeto,
    cidade_projeto,
    estado_projeto,
    cep_projeto,
    url_imagem_destaque,
    data_criacao,
    data_atualizacao
)
SELECT
    o.id_ong,
    'Recicla Escola',
    'Educacao para reciclagem e consumo consciente em escolas publicas.',
    'Capacitar alunos e professores em gestao de residuos.',
    'Alunos do ensino fundamental',
    CURRENT_DATE - INTERVAL '20 days',
    CURRENT_DATE + INTERVAL '120 days',
    'EM_ANDAMENTO',
    'Rua das Acacias',
    'Jardins',
    'Escola municipal',
    'Belo Horizonte',
    'MG',
    '30120010',
    'projects/recicla-escola.png',
    CURRENT_TIMESTAMP - INTERVAL '18 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM tb_ong o
WHERE o.cnpj = '22345678000180'
AND NOT EXISTS (
    SELECT 1 FROM tb_projeto p WHERE p.id_ong = o.id_ong AND p.nome_projeto = 'Recicla Escola'
);

-- 8) Atividades
INSERT INTO tb_atividade (
    id_projeto,
    nome_atividade,
    descricao_atividade,
    data_hora_inicio_atividade,
    data_hora_fim_atividade,
    local_atividade,
    vagas_totais,
    vagas_preenchidas_atividade,
    version,
    data_criacao,
    ultima_atualizacao
)
SELECT
    p.id_projeto,
    'Preparo de Refeicoes - Turma A',
    'Equipe para preparo de refeicoes de segunda a sexta.',
    CURRENT_TIMESTAMP + INTERVAL '3 days',
    CURRENT_TIMESTAMP + INTERVAL '3 days 4 hours',
    'Cozinha Central - Unidade Centro',
    12,
    1,
    0,
    CURRENT_TIMESTAMP - INTERVAL '10 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM tb_projeto p
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE o.cnpj = '12345678000195'
  AND p.nome_projeto = 'Cozinha Solidaria Centro'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_atividade a
      WHERE a.id_projeto = p.id_projeto
        AND a.nome_atividade = 'Preparo de Refeicoes - Turma A'
  );

INSERT INTO tb_atividade (
    id_projeto,
    nome_atividade,
    descricao_atividade,
    data_hora_inicio_atividade,
    data_hora_fim_atividade,
    local_atividade,
    vagas_totais,
    vagas_preenchidas_atividade,
    version,
    data_criacao,
    ultima_atualizacao
)
SELECT
    p.id_projeto,
    'Distribuicao de Marmitas',
    'Entrega de refeicoes em pontos de apoio da regiao.',
    CURRENT_TIMESTAMP + INTERVAL '5 days',
    CURRENT_TIMESTAMP + INTERVAL '5 days 3 hours',
    'Praca da Se',
    8,
    0,
    0,
    CURRENT_TIMESTAMP - INTERVAL '8 days',
    NULL
FROM tb_projeto p
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE o.cnpj = '12345678000195'
  AND p.nome_projeto = 'Cozinha Solidaria Centro'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_atividade a
      WHERE a.id_projeto = p.id_projeto
        AND a.nome_atividade = 'Distribuicao de Marmitas'
  );

INSERT INTO tb_atividade (
    id_projeto,
    nome_atividade,
    descricao_atividade,
    data_hora_inicio_atividade,
    data_hora_fim_atividade,
    local_atividade,
    vagas_totais,
    vagas_preenchidas_atividade,
    version,
    data_criacao,
    ultima_atualizacao
)
SELECT
    p.id_projeto,
    'Triagem de Doacoes',
    'Separacao e inventario de roupas e cobertores arrecadados.',
    CURRENT_TIMESTAMP + INTERVAL '16 days',
    CURRENT_TIMESTAMP + INTERVAL '16 days 2 hours',
    'Centro de Triagem Luz',
    15,
    0,
    0,
    CURRENT_TIMESTAMP - INTERVAL '6 days',
    NULL
FROM tb_projeto p
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE o.cnpj = '12345678000195'
  AND p.nome_projeto = 'Mutirao de Inverno 2026'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_atividade a
      WHERE a.id_projeto = p.id_projeto
        AND a.nome_atividade = 'Triagem de Doacoes'
  );

INSERT INTO tb_atividade (
    id_projeto,
    nome_atividade,
    descricao_atividade,
    data_hora_inicio_atividade,
    data_hora_fim_atividade,
    local_atividade,
    vagas_totais,
    vagas_preenchidas_atividade,
    version,
    data_criacao,
    ultima_atualizacao
)
SELECT
    p.id_projeto,
    'Plantio de Mudas - Etapa Final',
    'Mutirao de encerramento do ciclo de plantio no bairro.',
    CURRENT_TIMESTAMP - INTERVAL '35 days',
    CURRENT_TIMESTAMP - INTERVAL '35 days' + INTERVAL '5 hours',
    'Parque do Bairro Sul',
    10,
    2,
    0,
    CURRENT_TIMESTAMP - INTERVAL '60 days',
    CURRENT_TIMESTAMP - INTERVAL '34 days'
FROM tb_projeto p
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE o.cnpj = '22345678000180'
  AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_atividade a
      WHERE a.id_projeto = p.id_projeto
        AND a.nome_atividade = 'Plantio de Mudas - Etapa Final'
  );

INSERT INTO tb_atividade (
    id_projeto,
    nome_atividade,
    descricao_atividade,
    data_hora_inicio_atividade,
    data_hora_fim_atividade,
    local_atividade,
    vagas_totais,
    vagas_preenchidas_atividade,
    version,
    data_criacao,
    ultima_atualizacao
)
SELECT
    p.id_projeto,
    'Oficina de Reciclagem Criativa',
    'Oficina pratica com materiais reciclaveis para estudantes.',
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    CURRENT_TIMESTAMP + INTERVAL '7 days 3 hours',
    'Escola Municipal Aurora',
    20,
    1,
    0,
    CURRENT_TIMESTAMP - INTERVAL '9 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM tb_projeto p
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE o.cnpj = '22345678000180'
  AND p.nome_projeto = 'Recicla Escola'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_atividade a
      WHERE a.id_projeto = p.id_projeto
        AND a.nome_atividade = 'Oficina de Reciclagem Criativa'
  );

-- 9) Inscricoes com diferentes status
INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '4 days',
    'CONFIRMADA',
    NULL,
    NULL
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Preparo de Refeicoes - Turma A'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'diego.voluntario@voluntaria.local'
  AND p.nome_projeto = 'Cozinha Solidaria Centro'
  AND o.cnpj = '12345678000195'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    'PENDENTE',
    'token-inscricao-pendente-001',
    CURRENT_TIMESTAMP + INTERVAL '12 hours'
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Preparo de Refeicoes - Turma A'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'elisa.voluntaria@voluntaria.local'
  AND p.nome_projeto = 'Cozinha Solidaria Centro'
  AND o.cnpj = '12345678000195'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    'CANCELADA_PELO_VOLUNTARIO',
    NULL,
    NULL
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Distribuicao de Marmitas'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'fernanda.voluntaria@voluntaria.local'
  AND p.nome_projeto = 'Cozinha Solidaria Centro'
  AND o.cnpj = '12345678000195'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '36 days',
    'CONCLUIDA_PARTICIPACAO',
    NULL,
    NULL
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Plantio de Mudas - Etapa Final'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'diego.voluntario@voluntaria.local'
  AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
  AND o.cnpj = '22345678000180'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '36 days',
    'RECUSADA_PELA_ONG',
    NULL,
    NULL
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Plantio de Mudas - Etapa Final'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'elisa.voluntaria@voluntaria.local'
  AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
  AND o.cnpj = '22345678000180'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '37 days',
    'CONCLUIDA_PARTICIPACAO',
    NULL,
    NULL
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Plantio de Mudas - Etapa Final'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'gustavo.voluntario@voluntaria.local'
  AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
  AND o.cnpj = '22345678000180'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

INSERT INTO tb_inscricao (
    id_usuario,
    id_atividade,
    data_inscricao,
    status_inscricao,
    token_confirmacao,
    data_expiracao_token
)
SELECT
    u.id_usuario,
    a.id_atividade,
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    'CONFIRMADA',
    NULL,
    NULL
FROM tb_usuarios u
JOIN tb_atividade a ON a.nome_atividade = 'Oficina de Reciclagem Criativa'
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'fernanda.voluntaria@voluntaria.local'
  AND p.nome_projeto = 'Recicla Escola'
  AND o.cnpj = '22345678000180'
ON CONFLICT (id_usuario, id_atividade) DO NOTHING;

-- 10) Feedback (somente inscricoes concluidas)
INSERT INTO tb_feedback (
    id_inscricao,
    nota,
    comentario,
    data_feedback
)
SELECT
    i.id_inscricao,
    5,
    'Atividade muito bem organizada e com alto impacto social.',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
FROM tb_inscricao i
JOIN tb_usuarios u ON u.id_usuario = i.id_usuario
JOIN tb_atividade a ON a.id_atividade = i.id_atividade
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'diego.voluntario@voluntaria.local'
  AND a.nome_atividade = 'Plantio de Mudas - Etapa Final'
  AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
  AND o.cnpj = '22345678000180'
  AND i.status_inscricao = 'CONCLUIDA_PARTICIPACAO'
ON CONFLICT (id_inscricao) DO NOTHING;

INSERT INTO tb_feedback (
    id_inscricao,
    nota,
    comentario,
    data_feedback
)
SELECT
    i.id_inscricao,
    4,
    'Experiencia positiva, equipe acolhedora e bom planejamento.',
    CURRENT_TIMESTAMP - INTERVAL '29 days'
FROM tb_inscricao i
JOIN tb_usuarios u ON u.id_usuario = i.id_usuario
JOIN tb_atividade a ON a.id_atividade = i.id_atividade
JOIN tb_projeto p ON p.id_projeto = a.id_projeto
JOIN tb_ong o ON o.id_ong = p.id_ong
WHERE u.email = 'gustavo.voluntario@voluntaria.local'
  AND a.nome_atividade = 'Plantio de Mudas - Etapa Final'
  AND p.nome_projeto = 'Horta Comunitaria Bairro Sul'
  AND o.cnpj = '22345678000180'
  AND i.status_inscricao = 'CONCLUIDA_PARTICIPACAO'
ON CONFLICT (id_inscricao) DO NOTHING;

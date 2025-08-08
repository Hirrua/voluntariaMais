DROP TABLE IF EXISTS tb_feedback CASCADE;
DROP TABLE IF EXISTS tb_inscricao CASCADE;
DROP TABLE IF EXISTS tb_voluntario_habilidade CASCADE;
DROP TABLE IF EXISTS tb_habilidades CASCADE;
DROP TABLE IF EXISTS tb_atividade CASCADE;
DROP TABLE IF EXISTS tb_projeto CASCADE;
DROP TABLE IF EXISTS tb_ong CASCADE;
DROP TABLE IF EXISTS tb_perfil_voluntario CASCADE;
DROP TABLE IF EXISTS tb_usuario_roles CASCADE;
DROP TABLE IF EXISTS tb_roles CASCADE;
DROP TABLE IF EXISTS tb_usuarios CASCADE;

DROP TYPE IF EXISTS status_aprovacao_ong_enum CASCADE;
DROP TYPE IF EXISTS status_projeto_enum CASCADE;
DROP TYPE IF EXISTS status_inscricao_enum CASCADE;

CREATE TYPE status_aprovacao_ong_enum AS ENUM (
    'PENDENTE',
    'APROVADA',
    'REJEITADA'
);

CREATE TYPE status_projeto_enum AS ENUM (
    'PLANEJAMENTO',
    'EM_ANDAMENTO',
    'CONCLUIDO',
    'CANCELADO'
);

CREATE TYPE status_inscricao_enum AS ENUM (
    'PENDENTE',
    'CONFIRMADA',
    'CANCELADA_PELO_VOLUNTARIO',
    'RECUSADA_PELA_ONG',
    'CONCLUIDA_PARTICIPACAO'
);

CREATE TABLE tb_usuarios (
    id_usuario BIGSERIAL PRIMARY KEY,
    nome VARCHAR(60) NOT NULL,
    sobrenome VARCHAR(60) NOT NULL,
    email VARCHAR(40) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    logradouro_usuario VARCHAR(60),
    bairro_usuario VARCHAR(60),
    complemento_usuario VARCHAR(60),
    cidade_usuario VARCHAR(60),
    estado_usuario VARCHAR(2),
    cep_usuario VARCHAR(8),
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_roles (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE tb_usuario_roles (
    id_usuario BIGINT NOT NULL REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    id_role BIGINT NOT NULL REFERENCES tb_roles(id) ON DELETE CASCADE,
    PRIMARY KEY (id_usuario, id_role)
);

CREATE TABLE tb_perfil_voluntario (
    id_usuario BIGINT PRIMARY KEY REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    bio TEXT,
    disponibilidade VARCHAR(100),
    data_nascimento DATE,
    telefone_contato VARCHAR(20)
);

CREATE TABLE tb_habilidades (
    id_habilidade BIGSERIAL PRIMARY KEY,
    nome_habilidade VARCHAR(100) NOT NULL UNIQUE,
    descricao_habilidade TEXT
);

CREATE TABLE tb_voluntario_habilidade (
    id_usuario BIGINT NOT NULL REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    id_habilidade BIGINT NOT NULL REFERENCES tb_habilidades(id_habilidade) ON DELETE CASCADE,
    PRIMARY KEY (id_usuario, id_habilidade)
);

CREATE TABLE tb_ong (
    id_ong BIGSERIAL PRIMARY KEY,
    id_usuario_responsavel BIGINT NOT NULL UNIQUE REFERENCES tb_usuarios(id_usuario),
    nome_ong VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    descricao TEXT,
    logradouro_ong VARCHAR(60),
    bairro_ong VARCHAR(60),
    complemento_ong VARCHAR(60),
    cidade_ong VARCHAR(60),
    estado_ong VARCHAR(2),
    cep_ong VARCHAR(8),
    telefone_ong VARCHAR(20),
    email_contato_ong VARCHAR(255) NOT NULL UNIQUE,
    website VARCHAR(255),
    data_fundacao DATE,
    logo_url VARCHAR(512),
    status_aprovacao status_aprovacao_ong_enum DEFAULT 'PENDENTE',
    data_criacao_registro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_aprovacao_rejeicao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_projeto (
    id_projeto BIGSERIAL PRIMARY KEY,
    id_ong BIGINT NOT NULL REFERENCES tb_ong(id_ong) ON DELETE CASCADE,
    nome_projeto VARCHAR(255) NOT NULL,
    descricao_detalhada TEXT,
    objetivo TEXT,
    publico_alvo VARCHAR(255),
    data_inicio_prevista DATE,
    data_fim_prevista DATE,
    status_projeto status_projeto_enum DEFAULT 'PLANEJAMENTO',
    logradouro_projeto VARCHAR(60),
    bairro_projeto VARCHAR(60),
    complemento_projeto VARCHAR(60),
    cidade_projeto VARCHAR(60),
    estado_projeto VARCHAR(2),
    cep_projeto VARCHAR(8),
    url_imagem_destaque VARCHAR(512),
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_atividade (
    id_atividade BIGSERIAL PRIMARY KEY,
    id_projeto BIGINT NOT NULL REFERENCES tb_projeto(id_projeto) ON DELETE CASCADE,
    nome_atividade VARCHAR(255) NOT NULL,
    descricao_atividade TEXT,
    data_hora_inicio_atividade TIMESTAMP WITH TIME ZONE,
    data_hora_fim_atividade TIMESTAMP WITH TIME ZONE,
    local_atividade VARCHAR(255),
    vagas_totais INTEGER NOT NULL,
    vagas_preenchidas_atividade INTEGER DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_inscricao (
    id_inscricao BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    id_atividade BIGINT NOT NULL REFERENCES tb_atividade(id_atividade) ON DELETE CASCADE,
    data_inscricao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status_inscricao status_inscricao_enum DEFAULT 'PENDENTE',
    token_confirmacao VARCHAR(255),
    data_expiracao_token TIMESTAMP WITH TIME ZONE,
    UNIQUE (id_usuario, id_atividade)
);

CREATE TABLE tb_feedback (
    id_feedback BIGSERIAL PRIMARY KEY,
    id_inscricao BIGINT NOT NULL UNIQUE REFERENCES tb_inscricao(id_inscricao) ON DELETE CASCADE,
    nota INTEGER NOT NULL,
    comentario TEXT,
    data_feedback TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tb_roles (nome) VALUES ('ROLE_VOLUNTARIO');
INSERT INTO tb_roles (nome) VALUES ('ROLE_ADMIN_ONG');
INSERT INTO tb_roles (nome) VALUES ('ROLE_ADMIN_PLATAFORMA');

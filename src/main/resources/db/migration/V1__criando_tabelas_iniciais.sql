DROP TABLE IF EXISTS Feedback CASCADE;
DROP TABLE IF EXISTS Inscricao CASCADE;
DROP TABLE IF EXISTS AtividadeHabilidadeRequerida CASCADE;
DROP TABLE IF EXISTS VoluntarioHabilidade CASCADE;
DROP TABLE IF EXISTS Habilidade CASCADE;
DROP TABLE IF EXISTS Atividade CASCADE;
DROP TABLE IF EXISTS Projeto CASCADE;
DROP TABLE IF EXISTS ONG CASCADE;
DROP TABLE IF EXISTS PerfilVoluntario CASCADE;
DROP TABLE IF EXISTS Usuario CASCADE;

DROP TYPE IF EXISTS tipo_usuario_enum;
DROP TYPE IF EXISTS status_aprovacao_ong_enum;
DROP TYPE IF EXISTS status_projeto_enum;
DROP TYPE IF EXISTS status_inscricao_enum;

CREATE TYPE tipo_usuario_enum AS ENUM (
    'VOLUNTARIO',
    'ADMIN_ONG',
    'ADMIN_PLATAFORMA'
);

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
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(60) NOT NULL,
    sobrenome VARCHAR(60) NOT NULL,
    email VARCHAR(40) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    logradouro_usuario VARCHAR(60) NOT NULL,
    bairro_usuario VARCHAR(60) NOT NULL,
    complemento_usuario VARCHAR(60) NOT NULL,
    cidade_usuario VARCHAR(60) NOT NULL,
    estado_usuario VARCHAR(60) NOT NULL,
    tipo_usuario tipo_usuario_enum NOT NULL,
    ativo BOOLEAN,
    data_cadastro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_perfil_voluntario (
    id_usuario INTEGER PRIMARY KEY REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    bio VARCHAR(255),
    disponibilidade VARCHAR(30),
    data_nascimento DATE,
    telefone_contato VARCHAR(11)
);

CREATE TABLE tb_habilidades (
    id_habilidade SERIAL PRIMARY KEY,
    nome_habilidade VARCHAR(100) NOT NULL UNIQUE,
    descricao_habilidade VARCHAR(255)
);

CREATE TABLE tb_voluntario_habilidade (
    id_usuario INTEGER NOT NULL REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    id_habilidade INTEGER NOT NULL REFERENCES tb_habilidades(id_habilidade) ON DELETE CASCADE,
    PRIMARY KEY (id_usuario, id_habilidade)
);

CREATE TABLE tb_ong (
    id_ong SERIAL PRIMARY KEY,
    id_usuario_responsavel INTEGER NOT NULL UNIQUE REFERENCES tb_usuarios(id_usuario),
    nome_ong VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) UNIQUE,
    descricao TEXT,
    logradouro_ong VARCHAR(60) NOT NULL,
    bairro_ong VARCHAR(60) NOT NULL,
    complemento_ong VARCHAR(60) NOT NULL,
    cidade_ong VARCHAR(60) NOT NULL,
    estado_ong VARCHAR(60) NOT NULL,
    telefone_ong VARCHAR(20),
    email_contato_ong VARCHAR(255),
    website VARCHAR(255),
    data_fundacao DATE,
    logo_url VARCHAR(512),
    status_aprovacao status_aprovacao_ong_enum DEFAULT 'PENDENTE',
    data_criacao_registro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_aprovacao_rejeicao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_projeto (
    id_projeto SERIAL PRIMARY KEY,
    id_ong INTEGER NOT NULL REFERENCES tb_ong(id_ong) ON DELETE CASCADE,
    nome_projeto VARCHAR(255) NOT NULL,
    descricao_detalhada TEXT,
    objetivo TEXT,
    publico_alvo VARCHAR(255),
    data_inicio_prevista DATE,
    data_fim_prevista DATE,
    status_projeto status_projeto_enum DEFAULT 'PLANEJAMENTO',
    logradouro_projeto VARCHAR(60) NOT NULL,
    bairro_projeto VARCHAR(60) NOT NULL,
    complemento_projeto VARCHAR(60) NOT NULL,
    cidade_projeto VARCHAR(60) NOT NULL,
    estado_projeto VARCHAR(60) NOT NULL,
    url_imagem_destaque VARCHAR(512),
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_atividade (
    id_atividade SERIAL PRIMARY KEY,
    id_projeto INTEGER NOT NULL REFERENCES tb_projeto(id_projeto) ON DELETE CASCADE,
    nome_atividade VARCHAR(255) NOT NULL,
    descricao_atividade TEXT,
    data_hora_inicio_atividade TIMESTAMP WITH TIME ZONE,
    data_hora_fim_atividade TIMESTAMP WITH TIME ZONE,
    local_atividade VARCHAR(255),
    vagas_disponiveis_atividade INTEGER NOT NULL,
    vagas_preenchidas_atividade INTEGER DEFAULT 0,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_inscricao (
    id_inscricao SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES tb_usuarios(id_usuario) ON DELETE CASCADE,
    id_atividade INTEGER NOT NULL REFERENCES tb_atividade(id_atividade) ON DELETE CASCADE,
    data_inscricao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status_inscricao status_inscricao_enum DEFAULT 'PENDENTE',
    UNIQUE (id_usuario, id_atividade)
);

CREATE TABLE tb_feedback (
    id_feedback SERIAL PRIMARY KEY,
    id_inscricao INTEGER NOT NULL UNIQUE REFERENCES tb_inscricao(id_inscricao) ON DELETE CASCADE,
    nota INTEGER NOT NULL,
    comentario TEXT,
    data_feedback TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

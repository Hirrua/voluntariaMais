CREATE TYPE status_usuario_enum AS ENUM (
    'PENDENTE_CONFIRMACAO',
    'CONFIRMADO'
);

ALTER TABLE tb_usuarios
    ADD COLUMN status_usuario status_usuario_enum NOT NULL DEFAULT 'PENDENTE_CONFIRMACAO',
    ADD COLUMN token_confirmacao VARCHAR(255),
    ADD COLUMN data_expiracao_token TIMESTAMP WITH TIME ZONE;

UPDATE tb_usuarios
SET status_usuario = CASE
        WHEN ativo THEN 'CONFIRMADO'::status_usuario_enum
        ELSE 'PENDENTE_CONFIRMACAO'::status_usuario_enum
    END;

ALTER TABLE tb_usuarios
    ALTER COLUMN ativo SET DEFAULT FALSE;

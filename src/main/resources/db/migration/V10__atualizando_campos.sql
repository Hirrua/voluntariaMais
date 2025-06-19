ALTER TABLE tb_ong
    DROP CONSTRAINT IF EXISTS tb_ong_id_usuario_responsavel_fkey;

ALTER TABLE tb_usuarios
    ALTER COLUMN id_usuario TYPE BIGINT;

ALTER TABLE tb_ong
    ALTER COLUMN id_usuario_responsavel TYPE BIGINT;

ALTER TABLE tb_ong
    ADD CONSTRAINT tb_ong_id_usuario_responsavel_fkey
        FOREIGN KEY (id_usuario_responsavel)
        REFERENCES tb_usuarios (id_usuario);

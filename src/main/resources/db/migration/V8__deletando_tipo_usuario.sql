ALTER TABLE tb_usuarios
DROP COLUMN IF EXISTS tipo_usuario;

DROP TYPE IF EXISTS tipo_usuario_enum;

ALTER TABLE tb_ong
    ALTER COLUMN logradouro_ong TYPE VARCHAR(60),
    ALTER COLUMN bairro_ong TYPE VARCHAR(60),
    ALTER COLUMN complemento_ong TYPE VARCHAR(60),
    ALTER COLUMN cidade_ong TYPE VARCHAR(60),
    ALTER COLUMN estado_ong TYPE VARCHAR(2),
    ALTER COLUMN cep_ong TYPE VARCHAR(8);

ALTER TABLE tb_usuarios
    ALTER COLUMN logradouro_usuario TYPE VARCHAR(60),
    ALTER COLUMN bairro_usuario TYPE VARCHAR(60),
    ALTER COLUMN complemento_usuario TYPE VARCHAR(60),
    ALTER COLUMN cidade_usuario TYPE VARCHAR(60),
    ALTER COLUMN estado_usuario TYPE VARCHAR(2),
    ALTER COLUMN cep_usuario TYPE VARCHAR(8);

ALTER TABLE tb_projeto
    ALTER COLUMN logradouro_projeto TYPE VARCHAR(60),
    ALTER COLUMN bairro_projeto TYPE VARCHAR(60),
    ALTER COLUMN complemento_projeto TYPE VARCHAR(60),
    ALTER COLUMN cidade_projeto TYPE VARCHAR(60),
    ALTER COLUMN estado_projeto TYPE VARCHAR(2),
    ALTER COLUMN cep_projeto TYPE VARCHAR(8);

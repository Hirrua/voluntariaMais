CREATE TABLE tb_roles (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE tb_usuario_roles (
    id_usuario INTEGER NOT NULL REFERENCES tb_usuarios(id_usuario),
    id_role INTEGER NOT NULL,
    PRIMARY KEY (id_usuario, id_role),
    FOREIGN KEY (id_role) REFERENCES tb_roles(id)
);

INSERT INTO tb_roles (nome) VALUES ('ROLE_VOLUNTARIO');
INSERT INTO tb_roles (nome) VALUES ('ROLE_ADMIN_ONG');
INSERT INTO tb_roles (nome) VALUES ('ROLE_ADMIN_PLATAFORMA');

Hibernate:
select

    -- dados usuário --
    ue1_0.id_usuario,
    ue1_0.ativo,
    ue1_0.cpf,
    ue1_0.data_cadastro,
    ue1_0.email,
    ue1_0.bairro_usuario,
    ue1_0.cep_usuario,
    ue1_0.cidade_usuario,
    ue1_0.complemento_usuario,
    ue1_0.estado_usuario,
    ue1_0.logradouro_usuario,
    ue1_0.nome,
    ue1_0.senha,
    ue1_0.sobrenome,

    -- dados perfil --
    pv1_0.id_usuario,
    pv1_0.bio,
    pv1_0.data_nascimento,
    pv1_0.disponibilidade,
    pv1_0.telefone_contato,

    -- dados roles --
    r1_0.id_usuario,
    r1_1.id,r1_1.nome,

from tb_usuarios ue1_0
    left join tb_perfil_voluntario pv1_0
on ue1_0.id_usuario=pv1_0.id_usuario
    left join tb_usuario_roles r1_0
on ue1_0.id_usuario=r1_0.id_usuario
    left join tb_roles r1_1
on r1_1.id=r1_0.id_role
where ue1_0.id_usuario=?

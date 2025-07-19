ALTER TABLE tb_ong
ALTER COLUMN status_aprovacao TYPE status_aprovacao_ong_enum
USING status_aprovacao::status_aprovacao_ong_enum;

ALTER TABLE tb_projeto
ALTER COLUMN status_projeto TYPE status_projeto_enum
USING status_projeto::status_projeto_enum;

ALTER TABLE tb_inscricao
ALTER COLUMN status_inscricao TYPE status_inscricao_enum
USING status_inscricao::status_inscricao_enum;

ALTER TABLE tb_ong
ALTER COLUMN status_aprovacao SET DEFAULT 'PENDENTE';

ALTER TABLE tb_projeto
ALTER COLUMN status_projeto SET DEFAULT 'PLANEJAMENTO';

ALTER TABLE tb_inscricao
ALTER COLUMN status_inscricao SET DEFAULT 'PENDENTE';

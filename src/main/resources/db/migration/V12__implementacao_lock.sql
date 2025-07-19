ALTER TABLE tb_atividade ADD COLUMN version INTEGER;
UPDATE tb_atividade SET version = 0;
ALTER TABLE tb_atividade ALTER COLUMN version SET NOT NULL;

ALTER TABLE tb_atividade ADD COLUMN vagas_totais INTEGER;

UPDATE tb_atividade
SET vagas_totais = vagas_preenchidas_atividade + vagas_disponiveis_atividade;

ALTER TABLE tb_atividade ALTER COLUMN vagas_totais SET NOT NULL;

ALTER TABLE tb_atividade DROP COLUMN vagas_disponiveis_atividade;

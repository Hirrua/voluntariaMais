ALTER TABLE tb_inscricao
ADD COLUMN token_confirmacao UUID,
ADD COLUMN data_expiracao_token TIMESTAMP WITH TIME ZONE;

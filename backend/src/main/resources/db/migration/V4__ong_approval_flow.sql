ALTER TABLE tb_ong
    ADD COLUMN token_aprovacao VARCHAR(255),
    ADD COLUMN data_expiracao_aprovacao TIMESTAMP WITH TIME ZONE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_tb_ong_token_aprovacao ON tb_ong (token_aprovacao);

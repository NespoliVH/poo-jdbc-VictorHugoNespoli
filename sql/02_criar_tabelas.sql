-- Execute conectado ao banco poo_exercicios.
-- Este script cria as tabelas em uma instalação nova; não apaga dados.
BEGIN;

CREATE TABLE desenvolvedora (
    id SERIAL PRIMARY KEY,
    razao_social VARCHAR(120) NOT NULL,
    pais_origem VARCHAR(60),
    ano_criacao INT,
    CONSTRAINT ck_desenvolvedora_razao_social
        CHECK (LENGTH(TRIM(razao_social)) > 0),
    CONSTRAINT ck_desenvolvedora_ano_criacao
        CHECK (ano_criacao BETWEEN 1 AND 9999)
);

CREATE TABLE jogo (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    preco_venda NUMERIC(10,2) NOT NULL,
    tamanho_gb NUMERIC(6,2) NOT NULL,
    desenvolvedora_id INT NOT NULL,
    CONSTRAINT fk_jogo_desenvolvedora
        FOREIGN KEY (desenvolvedora_id)
        REFERENCES desenvolvedora(id) ON DELETE RESTRICT,
    CONSTRAINT ck_jogo_titulo CHECK (LENGTH(TRIM(titulo)) > 0),
    CONSTRAINT ck_jogo_preco CHECK (preco_venda >= 0),
    CONSTRAINT ck_jogo_tamanho CHECK (tamanho_gb >= 0)
);

CREATE INDEX idx_jogo_desenvolvedora_id ON jogo(desenvolvedora_id);

COMMIT;

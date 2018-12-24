INSERT INTO `empresa` (`id`, `cnpj`, `data_atualizacao`, `data_criacao`, `razao_social`)
VALUES (NULL, '22122365000121', CURRENT_DATE(), CURRENT_DATE(), 'JULIOMENDES90');

INSERT INTO `funcionario` (`id`, `cpf`, `data_atualizacao`, `data_criacao`, `email`, `nome`, `perfil`, `qtd_horas_almoco`, `qtd_horas_trabalho_dia`, `senha`,
                           `valor_hora`, `empresa_id`)
VALUES (NULL, '94688953003', CURRENT_DATE(), CURRENT_DATE(), 'admin@juliomendes90.com', 'ADMIN', 'ROLE_ADMIN', NULL, NULL,
        '$10$qxlKoV7YOkDxquRdcRl97enXUQXfRqx9wXWouprz7P4cin7RT1Htq', NULL, (SELECT `id` FROM `empresa` WHERE `cnpj` = '22122365000121'));
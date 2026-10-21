# Arquitetura e guia de utilização

## Fluxo de navegação

```text
TelaPrincipal
├── Fazer um pedido → PainelCliente → checkout → recibo PDF
└── Área da equipa → TelaLogin → TelaGerente
                              ├── Dashboard
                              ├── Vendas / Pedidos / Clientes
                              ├── Produtos / Promoções / Stock / Relatórios (Administrador e Gerente)
                              └── Utilizadores (Administrador)
```

`AplicacaoController` cria `DadosSistema`, mantém o utilizador autenticado e informa à interface quais secções podem ser apresentadas. Dessa forma, as telas não tomam decisões de perfil por conta própria.

## Persistência

Ao arrancar, `InicializadorBD` cria as tabelas necessárias. Cada entidade utiliza um DAO e um service:

- Produtos: `ProdutoDAO` / `ProdutoService`
- Promoções: `PromocaoDAO` / `PromocaoService`
- Clientes: `ClienteDAO` / `ClienteService`
- Stock e auditoria: `StockDAO` / `StockService`
- Pedidos, linhas e pagamentos: `PedidoDAO` / `PedidoService`
- Utilizadores e perfis: `UsuarioDAO` / `AuthService`

O salvamento de um pedido é transacional: pedido, itens, pagamento e redução de stock são confirmados ou revertidos juntos. `PedidoDAO.listar()` reconstitui os itens, o pagamento e o histórico no próximo arranque.

## Regras e validação

`Validador` é usado pelos services e concentra as regras principais:

- Campos obrigatórios (nome, categoria, utilizador);
- Preço positivo e percentagens entre 0 e 100;
- Quantidades de stock não negativas;
- Formato e unicidade de nome de utilizador;
- Impedimento de remoção da própria conta ou do último administrador.

Erros de regra são comunicados como `ValidacaoException`; erros de infraestrutura JDBC são encapsulados com uma mensagem apropriada para a tela.

## Organização de componentes visuais

- `Ui`: paleta, cartões, botões e tabelas consistentes;
- `PainelDashboard`: cartões de indicadores e atalhos;
- tabelas de produtos, pedidos, promoções e utilizadores: pesquisa, filtros e ordenação por coluna;
- `PainelCliente`: separa cardápio, carrinho, total e ações de checkout;
- `PainelPedidos`: permite atualizar o estado e reemitir recibo PDF.

Todas as telas visuais têm pares `.java` + `.form` e método `initComponents()`. No NetBeans, abra qualquer `.java` e seleccione **Design** para reposicionar componentes por drag-and-drop. As regras de negócio e os listeners ficam fora do bloco gerado para que alterações visuais não apaguem a lógica.

## Cenários de aceitação sugeridos

1. Entre como `admin/admin123` (ou a senha antiga `1234`) e confirme que o menu **Utilizadores** é visível.
2. Entre como `gerente/gerente123` e confirme acesso a stock e relatórios, mas não a utilizadores.
3. Entre como `atendente/atendente123` e confirme que as opções de administração não aparecem.
4. Registe um cliente, crie um pedido, aplique uma promoção e conclua o checkout.
5. Gere o PDF na venda; reinicie a aplicação; abra **Pedidos** e gere o mesmo recibo novamente.
6. Tente inserir preço negativo, desconto acima de 100 ou stock negativo e confirme a mensagem de validação.

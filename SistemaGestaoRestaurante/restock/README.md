# Restaurante UEM — Sistema de Gestão

## Entrega académica: POO, CRUD e base de dados

O sistema contém classes de domínio para restaurante, produtos, clientes, pedidos, promoções, stock, utilizadores e pagamentos, com atributos encapsulados e regras de negócio. `Gerente` especializa `Usuario` (herança). `Pagamento` é uma interface implementada por dinheiro, cartão, M-Pesa, e-Mola e mKesh (polimorfismo).

O CRUD está organizado em serviços e DAOs para produtos, clientes, promoções, stock, utilizadores e pedidos. `DadosSistema` inicializa os DAOs e `InicializadorBD` cria automaticamente as tabelas MySQL.

### Activar a base de dados

Não escreva `RESTAURANTE_DB_URL`, `RESTAURANTE_DB_USER` ou `RESTAURANTE_DB_PASSWORD` na janela de SQL: são nomes de variáveis de configuração, não comandos SQL. No MySQL Workbench abra e execute o ficheiro [database/gestao_restaurante.sql](database/gestao_restaurante.sql), que começa por `CREATE DATABASE` e `USE gestao_restaurante`.

Depois configure as variáveis no sistema (ou nas propriedades da JVM):

```text
RESTAURANTE_DB_URL=jdbc:mysql://localhost:3306/gestao_restaurante?useSSL=false&serverTimezone=Africa/Maputo&allowPublicKeyRetrieval=true
RESTAURANTE_DB_USER=root
RESTAURANTE_DB_PASSWORD=a_sua_password
```

Ao iniciar a aplicação, `InicializadorBD` cria as tabelas que ainda não existirem.

Aplicação desktop em **Java 17+**, Swing, Maven e MySQL para gerir um restaurante: vendas, pedidos, clientes, produtos, promoções, inventário, utilizadores e recibos PDF.

## Principais melhorias desta versão

- Dashboard moderno com menu lateral, cartões de indicadores, atalhos e estado da ligação à base de dados.
- Navegação central entre venda, pedidos, clientes, produtos, promoções, stock, relatórios e utilizadores.
- Três perfis com permissões: **Administrador**, **Gerente** e **Atendente**.
- Autenticação persistida na tabela `utilizadores`; as senhas são guardadas como hash SHA-256, nunca em texto simples.
- Persistência de pedidos completa: cabeçalho, itens, pagamento, estado e relação com o cliente são recuperados ao reiniciar.
- Carrinho de compra, filtros, ordenação nas tabelas e validação de dados de negócio.
- Recibos em PDF, disponíveis logo após a venda e também na gestão de pedidos para reemissão.
- Relatórios CSV de produtos, promoções e pedidos.

## Estrutura MVC

| Camada | Pacote | Responsabilidade |
|---|---|---|
| Model | `model`, `security` | Entidades de negócio, estados e perfis |
| View | `ui` | Telas Swing, tabelas, formulários e interação |
| Controller | `controller` | Sessão, navegação e autorização de acesso |
| Service | `service` | Regras de negócio, validação e casos de uso |
| DAO | `dao`, `database` | JDBC, transações e persistência MySQL |
| Support | `validation`, `exception`, `report` | Validação, erros e exportação CSV/PDF |

O guia completo está em [`docs/ARQUITETURA_E_GUIA.md`](docs/ARQUITETURA_E_GUIA.md).

## Perfis de acesso

| Função | Administrador | Gerente | Atendente |
|---|:---:|:---:|:---:|
| Dashboard e vendas | Sim | Sim | Sim |
| Clientes e pedidos | Sim | Sim | Sim |
| Produtos, promoções, stock e relatórios | Sim | Sim | Não |
| Criar/editar/remover utilizadores | Sim | Não | Não |

Contas demonstrativas criadas na primeira execução:

| Perfil | Utilizador | Senha inicial |
|---|---|---|
| Administrador | `admin` | `admin123` (também aceita a senha antiga `1234`) |
| Gerente | `gerente` | `gerente123` |
| Atendente | `atendente` | `atendente123` |

Altere as senhas de demonstração antes de qualquer utilização real.

## Executar no NetBeans

1. Abra a pasta `restock` através de **File → Open Project**.
2. Configure o JDK 17 ou superior.
3. Configure o MySQL conforme a secção seguinte.
4. Execute a classe `mz.uem.restaurante.Main` ou use **Run Project**.

### Configurar MySQL

1. Inicie o MySQL e execute [`database/gestao_restaurante.sql`](database/gestao_restaurante.sql).
2. Defina as variáveis de ambiente (recomendado):

```text
RESTAURANTE_DB_URL=jdbc:mysql://localhost:3306/gestao_restaurante?useSSL=false&serverTimezone=Africa/Maputo&allowPublicKeyRetrieval=true
RESTAURANTE_DB_USER=utilizador_mysql
RESTAURANTE_DB_PASSWORD=senha_mysql
```

3. Execute `mvn test` e `mvn package` (ou use as ações Maven do NetBeans).

Se o MySQL não estiver disponível, a aplicação continua em modo temporário na memória; o dashboard sinaliza esse estado. Os dados desse modo não sobrevivem ao encerramento.

## Recibos e relatórios

- Na tela **Nova venda**, finalize o pedido e use **Baixar último recibo (PDF)**.
- Na tela **Pedidos**, selecione qualquer pedido histórico e use **Gerar recibo PDF**.
- Em **Relatórios**, o administrador ou gerente pode exportar CSV de produtos, promoções e pedidos.

O PDF é produzido pelo próprio projeto, sem serviço externo nem dependência adicional.

## Interface e organização visual

Todas as 11 telas visuais têm agora o par `.java` + `.form` e podem ser abertas no separador **Design** do NetBeans: `TelaPrincipal`, `TelaLogin`, `TelaGerente`, `PainelDashboard`, `PainelCliente`, `PainelPedidos`, `PainelProdutos`, `PainelPromocoes`, `PainelGestaoClientes`, `PainelStock` e `PainelUtilizadores`. Os componentes fixos usam `initComponents()` gerado pelo GUI Builder; a lógica de eventos e os dados são configurados depois da criação visual.

## Testes e verificação

- Testes unitários: `mvn test`
- Build: `mvn package`
- Compilação direta de contingência: `javac -encoding UTF-8 -d target/javac-check ...`

`ReciboPdfServiceTest` verifica que um recibo é criado com cabeçalho PDF válido. Consulte também [`TESTES_E_LIMITACOES.txt`](TESTES_E_LIMITACOES.txt).

## Limitações conhecidas

- Pagamentos em dinheiro, cartão e M-Pesa são simulados: não existe integração bancária real.
- O hash SHA-256 foi aplicado como melhoria académica; para produção use um algoritmo adaptativo com salt, como BCrypt ou Argon2.
- A baixa automática de ingredientes por receita ainda não está ligada ao stock de produtos.

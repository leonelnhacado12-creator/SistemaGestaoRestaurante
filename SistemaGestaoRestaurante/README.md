# Sistema de Gestão de Restaurante — NetBeans GUI Builder

Esta versão corrige os formulários `.form` para serem realmente reconhecidos pelo NetBeans GUI Builder (Matisse). Os componentes têm posições/constraints no formulário e aparecem no quadro branco em **Design**.

## Abrir
1. Extraia o ZIP.
2. Abra o projecto Maven no NetBeans.
3. Em `src/main/java/mz/uem/restaurante/ui`, abra um ficheiro `.java` e clique em **Design**.
4. Agora JLabel, JTextField, JButton, JTable, JScrollPane, JSpinner etc. aparecem no quadro e podem ser movidos/editados por drag-and-drop.

## Formulários editáveis
- TelaPrincipal — JFrame Form
- TelaLogin — JDialog Form
- TelaGerente — JFrame Form
- PainelCliente — JPanel Form
- PainelProdutos — JPanel Form
- PainelPedidos — JPanel Form
- PainelPromocoes — JPanel Form

A lógica de negócio permanece separada nos pacotes `model` e `service`.

## Login de demonstração
Utilizador: `admin`
Senha: `1234`

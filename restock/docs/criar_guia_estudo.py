from pathlib import Path
from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Inches, Pt, RGBColor


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs" / "Guia_de_Estudo_e_Apresentacao_Restaurante_UEM.docx"
WELCOME_IMAGE = ROOT / "src" / "main" / "resources" / "images" / "welcome-restaurant-hero.png"
LOGIN_IMAGE = ROOT / "src" / "main" / "resources" / "images" / "login-food-hero.png"

BLACK = "000000"
DARK_GREEN = "1F4D35"
GREEN = "5A9A38"
AMBER = "F7B241"
LIGHT_GREEN = "EAF4E4"
LIGHT_AMBER = "FFF3DC"
LIGHT_GRAY = "F4F4F4"
MID_GRAY = "D9D9D9"
TEXT_GRAY = RGBColor(76, 76, 76)


def set_cell_shading(cell, fill):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = tc_pr.find(qn("w:shd"))
    if shd is None:
        shd = OxmlElement("w:shd")
        tc_pr.append(shd)
    shd.set(qn("w:fill"), fill)


def set_cell_border(cell, color=MID_GRAY):
    tc_pr = cell._tc.get_or_add_tcPr()
    borders = tc_pr.first_child_found_in("w:tcBorders")
    if borders is None:
        borders = OxmlElement("w:tcBorders")
        tc_pr.append(borders)
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        tag = qn(f"w:{edge}")
        element = borders.find(tag)
        if element is None:
            element = OxmlElement(f"w:{edge}")
            borders.append(element)
        element.set(qn("w:val"), "single")
        element.set(qn("w:sz"), "6")
        element.set(qn("w:space"), "0")
        element.set(qn("w:color"), color)


def set_cell_margins(cell, top=110, start=120, bottom=110, end=120):
    tc_pr = cell._tc.get_or_add_tcPr()
    mar = tc_pr.first_child_found_in("w:tcMar")
    if mar is None:
        mar = OxmlElement("w:tcMar")
        tc_pr.append(mar)
    for side, value in (("top", top), ("start", start), ("bottom", bottom), ("end", end)):
        node = mar.find(qn(f"w:{side}"))
        if node is None:
            node = OxmlElement(f"w:{side}")
            mar.append(node)
        node.set(qn("w:w"), str(value))
        node.set(qn("w:type"), "dxa")


def set_font(run, size=None, bold=None, color=BLACK, italic=None):
    run.font.name = "Aptos"
    run._element.rPr.rFonts.set(qn("w:ascii"), "Aptos")
    run._element.rPr.rFonts.set(qn("w:hAnsi"), "Aptos")
    if size:
        run.font.size = Pt(size)
    if bold is not None:
        run.bold = bold
    if italic is not None:
        run.italic = italic
    if color:
        run.font.color.rgb = RGBColor.from_string(color)


def add_text(doc, text, size=10.5, bold=False, color=BLACK, align=None, space_after=7, keep=False):
    p = doc.add_paragraph()
    if align is not None:
        p.alignment = align
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.keep_with_next = keep
    run = p.add_run(text)
    set_font(run, size=size, bold=bold, color=color)
    return p


def add_body(doc, text, lead=None):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(7)
    p.paragraph_format.line_spacing = 1.15
    if lead:
        r = p.add_run(lead)
        set_font(r, size=10.5, bold=True)
    r = p.add_run(text)
    set_font(r, size=10.5, color=BLACK)
    return p


def add_bullets(doc, items):
    for item in items:
        p = doc.add_paragraph(style="List Bullet")
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.line_spacing = 1.1
        run = p.add_run(item)
        set_font(run, size=10.2, color=BLACK)


def add_numbered(doc, items):
    for item in items:
        p = doc.add_paragraph(style="List Number")
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.line_spacing = 1.1
        run = p.add_run(item)
        set_font(run, size=10.2, color=BLACK)


def add_heading(doc, text, level=1):
    p = doc.add_paragraph(style=f"Heading {level}")
    p.paragraph_format.space_before = Pt(16 if level == 1 else 10)
    p.paragraph_format.space_after = Pt(7)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    set_font(run, size=16 if level == 1 else 12.5, bold=True, color=BLACK)
    return p


def set_cell_text(cell, text, bold=False, color=BLACK, size=9.3, align=None):
    cell.text = ""
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.05
    if align is not None:
        p.alignment = align
    r = p.add_run(str(text))
    set_font(r, size=size, bold=bold, color=color)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER
    set_cell_margins(cell)
    set_cell_border(cell)


def add_table(doc, headers, rows, widths=None):
    table = doc.add_table(rows=1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    for i, header in enumerate(headers):
        cell = table.rows[0].cells[i]
        set_cell_shading(cell, DARK_GREEN)
        set_cell_text(cell, header, bold=True, color="FFFFFF", size=9.1, align=WD_ALIGN_PARAGRAPH.CENTER)
        if widths:
            cell.width = Cm(widths[i])
    for row_i, row in enumerate(rows):
        cells = table.add_row().cells
        shade = "FFFFFF" if row_i % 2 == 0 else LIGHT_GRAY
        for i, value in enumerate(row):
            set_cell_shading(cells[i], shade)
            alignment = WD_ALIGN_PARAGRAPH.CENTER if len(str(value)) <= 20 else WD_ALIGN_PARAGRAPH.LEFT
            set_cell_text(cells[i], value, size=9.0, align=alignment)
            if widths:
                cells[i].width = Cm(widths[i])
    doc.add_paragraph().paragraph_format.space_after = Pt(3)
    return table


def add_footer(section):
    footer = section.footer
    p = footer.paragraphs[0]
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(3)
    r = p.add_run("Restaurante UEM | Guia de estudo e apresentação | Página ")
    set_font(r, size=8.5, color="666666")
    fld = OxmlElement("w:fldSimple")
    fld.set(qn("w:instr"), "PAGE")
    p._p.append(fld)


def configure_document(doc):
    section = doc.sections[0]
    section.page_width = Cm(21)
    section.page_height = Cm(29.7)
    section.top_margin = Cm(1.85)
    section.bottom_margin = Cm(1.65)
    section.left_margin = Cm(2.0)
    section.right_margin = Cm(2.0)
    add_footer(section)
    styles = doc.styles
    styles["Normal"].font.name = "Aptos"
    styles["Normal"]._element.rPr.rFonts.set(qn("w:ascii"), "Aptos")
    styles["Normal"]._element.rPr.rFonts.set(qn("w:hAnsi"), "Aptos")
    styles["Normal"].font.size = Pt(10.5)
    for style_name in ("Title", "Heading 1", "Heading 2", "Heading 3"):
        style = styles[style_name]
        style.font.name = "Aptos Display" if style_name == "Title" else "Aptos"
        style._element.rPr.rFonts.set(qn("w:ascii"), style.font.name)
        style._element.rPr.rFonts.set(qn("w:hAnsi"), style.font.name)
        style.font.color.rgb = RGBColor(0, 0, 0)


def cover(doc):
    p = doc.add_paragraph(style="Title")
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_after = Pt(10)
    r = p.add_run("Guia de Estudo e Apresentação do Sistema de Gestão de Restaurante")
    set_font(r, size=25, bold=True, color=BLACK)
    add_text(doc, "Restaurante UEM", size=15, bold=True, color=DARK_GREEN, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4)
    add_text(doc, "Manual em linguagem simples para estudar, demonstrar e explicar o sistema", size=11.5, color="555555", align=WD_ALIGN_PARAGRAPH.CENTER, space_after=18)
    if WELCOME_IMAGE.exists():
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_after = Pt(14)
        p.add_run().add_picture(str(WELCOME_IMAGE), width=Inches(6.55))
    add_text(doc, "Objetivo deste guia", size=13, bold=True, color=BLACK, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=7)
    add_text(doc, "Este documento explica o funcionamento do sistema passo a passo. Pode ser usado para preparar a demonstração, rever a arquitetura, responder às perguntas do avaliador e mostrar como cada funcionalidade resolve uma necessidade real de um restaurante.", size=10.5, color=BLACK, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=12)
    add_text(doc, "Versão preparada para a apresentação académica", size=9.5, color="666666", align=WD_ALIGN_PARAGRAPH.CENTER, space_after=0)
    doc.add_page_break()


def overview(doc):
    add_heading(doc, "1 Como usar este guia", 1)
    add_body(doc, "Comece pela visão geral e pelos fluxos do cliente e da equipa. Depois pratique o roteiro de demonstração. Não é necessário decorar cada classe do projeto; o importante é saber explicar o problema, a solução e as regras que evitam erros no restaurante.")
    add_bullets(doc, [
        "Para uma apresentação curta, estude primeiro as secções 2, 5, 7, 8, 9 e 15.",
        "Quando mencionar uma funcionalidade, explique sempre quem a usa, qual problema resolve e qual é o resultado.",
        "Use os exemplos deste guia para mostrar que o sistema não é apenas visual: ele valida dados, guarda informação e protege os fluxos importantes.",
    ])
    add_heading(doc, "Conteúdo do guia", 2)
    add_numbered(doc, [
        "Visão geral e problema resolvido", "Tecnologias e arquitetura MVC", "Perfis de acesso e credenciais", "Como iniciar o sistema", "Fluxo do cliente", "Fluxo da equipa", "Pedidos, pagamentos e notificações", "Reservas de mesa", "Produtos, receitas e stock", "Relatórios, backup e auditoria", "Validações e segurança", "Roteiro de apresentação", "Demonstração prática", "Perguntas prováveis", "Testes, limitações e melhorias futuras",
    ])
    add_heading(doc, "2 Visão geral e problema resolvido", 1)
    add_body(doc, "O Sistema de Gestão de Restaurante UEM é uma aplicação desktop criada para organizar as operações de um restaurante. Ele junta, numa única aplicação, os pedidos dos clientes, o controlo do cardápio, o pagamento, as reservas, o inventário, a comunicação e os relatórios.")
    add_body(doc, "Antes de um sistema assim, é comum o restaurante registar pedidos em papel, confirmar pagamentos por mensagens e controlar ingredientes de memória. Isso causa atrasos, perdas de informação, erros no troco, mesas duplicadas e dificuldade em acompanhar o pedido do cliente.")
    add_table(doc, ["Problema do restaurante", "Como o sistema responde"], [
        ["Cliente não sabe se o pedido está pronto", "Notificações mostram cada estado do pedido e avisam quando está pronto para levantar."],
        ["Equipa não sabe se pagamento móvel foi confirmado", "Pagamento fica em validação e só a equipa pode confirmar ou recusar antes da preparação."],
        ["Risco de reservar a mesma mesa duas vezes", "O sistema verifica capacidade e conflito de horário em blocos de duas horas."],
        ["Ingredientes acabam sem aviso", "Stock, ficha técnica e baixa automática controlam o consumo ao confirmar o pagamento."],
        ["Dificuldade em consultar operações", "Dashboard, relatórios, auditoria e backup facilitam o acompanhamento."],
    ], [7.0, 9.8])


def architecture(doc):
    add_heading(doc, "3 Tecnologias e arquitetura", 1)
    add_body(doc, "O programa foi desenvolvido em Java e é executado como aplicação desktop. A interface foi construída com Swing. Maven organiza as dependências e os testes. MySQL guarda os dados de forma persistente quando está configurado.")
    add_table(doc, ["Tecnologia", "Papel no sistema", "Explicação simples"], [
        ["Java 17 ou superior", "Linguagem principal", "Contém as regras, telas e comunicação com a base de dados."],
        ["Swing", "Interface gráfica", "Cria janelas, botões, tabelas, formulários e mensagens."],
        ["Maven", "Construção e testes", "Compila o projeto e executa os testes com um comando."],
        ["MySQL", "Base de dados", "Guarda clientes, pedidos, reservas, pagamentos, stock e utilizadores."],
        ["JDBC", "Ligação Java MySQL", "Permite que os DAOs enviem e leiam dados da base de dados."],
    ], [3.5, 4.5, 8.8])
    add_heading(doc, "Arquitetura MVC explicada", 2)
    add_body(doc, "O projeto segue a ideia de MVC. Isto separa a parte que aparece ao utilizador da parte que decide as regras e da parte que guarda os dados. A separação facilita a manutenção e evita colocar toda a lógica num único ficheiro.")
    add_table(doc, ["Camada", "Exemplos", "Responsabilidade"], [
        ["Model", "Pedido, Produto, Reserva, Cliente", "Representa os objetos do mundo real e os seus dados."],
        ["View", "TelaPrincipal, TelaLogin, PainelPedidos", "Mostra telas e recolhe ações do utilizador."],
        ["Controller", "AplicacaoController", "Mantém a sessão e orienta a navegação conforme o perfil."],
        ["Service", "PedidoService, ReservaService, AuthService", "Aplica regras de negócio e valida dados."],
        ["DAO", "PedidoDAO, ReservaDAO, UsuarioDAO", "Executa operações SQL e comunica com MySQL."],
    ], [3.0, 5.3, 8.5])
    add_body(doc, "Exemplo simples: quando alguém cria uma reserva, a tela recolhe os dados. O ReservaService confirma se a data é futura, se a mesa existe, se tem lugares suficientes e se não há sobreposição. Só depois o ReservaDAO grava a reserva no MySQL.", lead="Exemplo: ")
    add_heading(doc, "Persistência e modo temporário", 2)
    add_body(doc, "Quando a ligação MySQL está ativa, os dados permanecem guardados depois de fechar a aplicação. Se MySQL estiver desligado ou mal configurado, a aplicação continua em modo temporário na memória. Isso permite demonstrar as telas, mas os dados são perdidos quando o programa fecha.")


def accounts_and_start(doc):
    add_heading(doc, "4 Perfis de acesso e credenciais", 1)
    add_body(doc, "O sistema possui três perfis. Cada perfil vê apenas as funções necessárias para o seu trabalho. Esta regra reduz o risco de uma pessoa alterar dados que não deve alterar.")
    add_table(doc, ["Perfil", "Utilizador", "Senha inicial", "O que pode fazer"], [
        ["Administrador", "admin", "admin123", "Acesso completo, incluindo gerir utilizadores."],
        ["Gerente", "gerente", "gerente123", "Vendas, pedidos, clientes, produtos, promoções, stock e relatórios."],
        ["Atendente", "atendente", "atendente123", "Atende clientes, cria pedidos e acompanha pedidos."],
    ], [3.0, 3.0, 3.3, 7.5])
    add_body(doc, "As senhas antigas 123 e 1234 para o administrador não são aceites. A aplicação normaliza uma única vez as contas demonstrativas numa base de dados antiga, para que as credenciais acima funcionem. Depois disso, uma conta pode ser alterada pela gestão de utilizadores, se for necessário.")
    add_heading(doc, "5 Como iniciar o sistema", 1)
    add_numbered(doc, [
        "Inicie o serviço MySQL e crie a base de dados gestao_restaurante com o script SQL do projeto.",
        "No Windows, configure as variáveis de ambiente RESTAURANTE_DB_URL, RESTAURANTE_DB_USER e RESTAURANTE_DB_PASSWORD. A senha não deve ser escrita no código nem enviada ao Git.",
        "Abra o projeto no NetBeans com Java 17 ou superior.",
        "Execute Clean and Build Project quando houver alterações e depois use Run Project.",
        "Na tela inicial, escolha Fazer um pedido para o cliente ou Entrar na área da equipa para autenticar um utilizador interno.",
    ])
    add_body(doc, "A forma recomendada de validar o projeto é executar mvn test. Os testes verificam regras importantes, como redes de carteiras móveis, reserva sem conflito, pagamento antes da preparação, receita e bloqueio de credenciais antigas.")


def customer_flow(doc):
    add_heading(doc, "6 Fluxo do cliente", 1)
    add_body(doc, "A área do cliente foi pensada para uma pessoa que deseja fazer pedido sem precisar conhecer a parte administrativa. O cliente vê os produtos, adiciona itens ao carrinho, confirma os dados e escolhe uma forma de pagamento.")
    add_table(doc, ["Passo", "O que o cliente faz", "O que o sistema garante"], [
        ["1", "Consulta o cardápio", "Mostra apenas produtos disponíveis e permite procurar itens."],
        ["2", "Adiciona produtos ao carrinho", "Calcula quantidade, subtotal, desconto e total."],
        ["3", "Confirma pedido e contacto", "Cria o pedido e permite associar telefone para notificações."],
        ["4", "Escolhe pagamento", "Valida a carteira móvel pelo prefixo do número."],
        ["5", "Acompanha pedido", "Vê notificações e recebe alerta quando o pedido está pronto."],
    ], [1.5, 6.2, 9.1])
    add_heading(doc, "Comunicação com a equipa", 2)
    add_body(doc, "O botão Falar com atendente abre uma conversa para dúvidas, reclamações ou feedback. O atendente visualiza a conversa na gestão de clientes e responde. As mensagens ficam guardadas, portanto o pedido do cliente não se perde quando a janela é fechada.")
    add_heading(doc, "Reserva feita pelo cliente", 2)
    add_bullets(doc, [
        "O cliente informa nome, telefone, data, hora e número de pessoas.",
        "Pode consultar mesas disponíveis para o horário escolhido.",
        "Pode abrir Minhas reservas, consultar pelo telefone e cancelar a própria reserva antes do horário.",
        "Depois de enviada, a reserva fica pendente até a equipa confirmar, cancelar ou concluir.",
    ])


def orders_payments(doc):
    add_heading(doc, "7 Pedidos, pagamentos e notificações", 1)
    add_body(doc, "O pedido passa por estados que representam o trabalho real do restaurante. A equipa usa a tela Pedidos para acompanhar a fila, validar pagamento, iniciar a preparação e entregar o pedido.")
    add_table(doc, ["Estado do pedido", "Significado", "Notificação ao cliente"], [
        ["PENDENTE", "Pedido recebido, ainda aguarda tratamento.", "O cliente pode consultar que o pedido foi recebido."],
        ["EM PREPARACAO", "A cozinha começou a preparar.", "O cliente sabe que o pedido está em preparação."],
        ["PRONTO", "Pedido terminado e pronto para levantar.", "É enviado aviso de levantamento; com a área aberta, aparece também um alerta."],
        ["ENTREGUE", "Pedido entregue ao cliente.", "O histórico mostra a conclusão do pedido."],
    ], [3.2, 7.0, 6.6])
    add_heading(doc, "Pagamento móvel com confirmação da equipa", 2)
    add_body(doc, "M-Pesa, e-Mola e mKesh não são confirmados automaticamente por uma instituição financeira nesta versão. O sistema simula o processo correto: o pedido fica em validação, a equipa confere o comprovativo e confirma ou recusa. Enquanto o pagamento não estiver confirmado, a cozinha não pode iniciar a preparação.")
    add_table(doc, ["Carteira", "Prefixos válidos", "Exemplo de regra"], [
        ["M-Pesa", "84 ou 85", "Um número iniciado por 86 não pode ser usado como M-Pesa."],
        ["e-Mola", "86 ou 87", "O sistema aceita número nacional ou com +258."],
        ["mKesh", "82 ou 83", "Um prefixo fora destes valores é recusado."],
    ], [3.5, 4.2, 9.1])
    add_body(doc, "Este controlo evita que um pedido entre na cozinha antes de haver prova de pagamento. Quando a equipa decide, o estado do pagamento e a notificação do cliente são atualizados.")
    add_heading(doc, "Recibos", 2)
    add_body(doc, "Depois da venda, é possível gerar um recibo em PDF. Também é possível selecionar um pedido antigo e emitir novamente o recibo. Isto é importante quando o cliente perde o comprovativo ou quando a equipa precisa rever uma venda anterior.")


def reservations(doc):
    add_heading(doc, "8 Reservas de mesa", 1)
    add_body(doc, "A reserva não é apenas uma mensagem com uma data. O sistema trata a mesa como um recurso limitado e impede que a mesma mesa seja prometida a duas pessoas no mesmo período.")
    add_table(doc, ["Regra", "Como funciona", "Benefício"], [
        ["Data futura", "A reserva deve ser marcada para uma data e hora posteriores ao momento atual.", "Evita reservas inválidas no passado."],
        ["Capacidade", "O número de pessoas não pode ser maior que os lugares da mesa.", "Evita alocar uma mesa pequena a um grupo grande."],
        ["Conflito", "Cada reserva bloqueia a mesa durante duas horas e o sistema procura sobreposição.", "Evita dupla reserva da mesma mesa."],
        ["Estados", "Pendente, confirmada, cancelada, concluída e não compareceu.", "A equipa sabe a situação real de cada reserva."],
        ["Lembrete", "Reservas confirmadas nas próximas 24 horas recebem um lembrete uma única vez.", "Reduz esquecimentos sem enviar mensagens repetidas."],
    ], [3.0, 8.0, 5.8])
    add_body(doc, "Uma reserva só pode ser marcada como não compareceu depois do horário marcado. Esta regra evita que a equipa libere uma mesa antes da hora combinada com o cliente.")


def stock_and_management(doc):
    add_heading(doc, "9 Produtos, receitas e stock", 1)
    add_body(doc, "Produtos são os itens vendidos ao cliente, por exemplo hambúrguer, sumo ou pizza. Itens de stock são os ingredientes ou materiais físicos, por exemplo farinha, carne, tomate, bebidas ou embalagens. A ficha técnica liga um produto aos ingredientes necessários para produzir uma unidade.")
    add_table(doc, ["Elemento", "Exemplo", "Papel"], [
        ["Produto", "Pizza frango", "É mostrado no cardápio e vendido ao cliente."],
        ["Item de stock", "Farinha em quilogramas", "Representa a quantidade física guardada no inventário."],
        ["Ficha técnica", "0,3 kg de farinha por pizza", "Diz quanto stock deve ser consumido em cada unidade vendida."],
        ["Movimento de stock", "SAÍDA por pedido número 15", "Regista de onde saiu uma quantidade e qual foi o saldo depois."],
    ], [3.4, 5.1, 8.3])
    add_body(doc, "A baixa de ingredientes acontece quando o pagamento é confirmado. A operação é transacional: se não houver ingrediente suficiente, a confirmação falha e os dados não ficam pela metade. O sistema bloqueia o item de stock durante a alteração, reduzindo o risco de duas vendas consumirem a mesma quantidade ao mesmo tempo.")
    add_heading(doc, "Promoções, clientes e utilizadores", 2)
    add_bullets(doc, [
        "Promoções podem ter desconto, período de validade e estado ativo ou inativo.",
        "Clientes podem ser registados para associar pedidos e contacto.",
        "A gestão de utilizadores é exclusiva do administrador e impede eliminar a própria conta ou o último administrador.",
        "Tabelas de gestão oferecem pesquisa, filtros e ordenação para encontrar informação rapidamente.",
    ])
    if LOGIN_IMAGE.exists():
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(4)
        p.paragraph_format.space_after = Pt(5)
        p.add_run().add_picture(str(LOGIN_IMAGE), width=Inches(2.2))
        cap = doc.add_paragraph()
        cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cap.paragraph_format.space_after = Pt(8)
        run = cap.add_run("Imagem utilizada na composição visual da tela de login.")
        set_font(run, size=8.5, italic=True, color="666666")


def dashboard_backup_security(doc):
    add_heading(doc, "10 Dashboard, relatórios, backup e auditoria", 1)
    add_body(doc, "O dashboard transforma dados operacionais em indicadores rápidos. O gerente não precisa abrir várias telas para perceber se há pedidos a tratar, pagamentos pendentes ou ingredientes em falta.")
    add_table(doc, ["Indicador", "O que mostra", "Decisão que ajuda a tomar"], [
        ["Pedidos por tratar", "Pedidos pendentes ou em preparação.", "Verificar se a cozinha ou atendimento está atrasado."],
        ["Faturação hoje", "Soma de pedidos com pagamento confirmado no dia.", "Acompanhar desempenho diário."],
        ["Pagamentos por validar", "Pedidos móveis que aguardam confirmação.", "Conferir comprovativos antes de liberar preparação."],
        ["Pedidos prontos", "Pedidos à espera de levantamento ou entrega.", "Chamar o cliente e evitar atraso."],
        ["Reservas hoje e stock baixo", "Reservas ativas e itens abaixo do mínimo.", "Planear mesas e compras."],
    ], [3.6, 7.2, 6.0])
    add_heading(doc, "Relatórios e cópia de segurança", 2)
    add_body(doc, "A área Relatórios permite exportar produtos, promoções e pedidos em CSV. Também cria uma cópia SQL dos dados. A cópia deve ser importada depois de o esquema da aplicação existir, porque ela guarda os dados e não a estrutura completa da base.")
    add_heading(doc, "Auditoria e migrações", 2)
    add_body(doc, "Auditoria é o registo de ações importantes, por exemplo alterações de estado, atualização de receita, movimentos de stock e criação de backup. Migrações são versões da estrutura da base de dados. A tabela schema_migracoes permite acrescentar recursos novos, como receitas ou lembretes, sem apagar os dados já existentes.")
    add_heading(doc, "11 Validações e segurança", 1)
    add_table(doc, ["Proteção", "Explicação simples"], [
        ["Validação de campos", "Impede nomes vazios, preços negativos, percentagens fora de 0 a 100 e quantidades inválidas."],
        ["Senhas", "São guardadas como hash SHA-256, não como texto normal. Para produção, BCrypt ou Argon2 seriam opções mais fortes."],
        ["Perfis", "Limita menus e ações conforme Administrador, Gerente ou Atendente."],
        ["Transações", "Pedido, itens, pagamento e baixa de stock são confirmados juntos ou revertidos juntos quando ocorre erro."],
        ["Validação de pagamento", "Bloqueia preparação enquanto um pagamento móvel ainda aguarda confirmação."],
    ], [4.2, 12.6])


def presentation(doc):
    add_heading(doc, "12 Roteiro para apresentar o sistema", 1)
    add_body(doc, "A melhor apresentação segue a jornada do restaurante. Em vez de abrir telas sem contexto, conte uma história: um cliente quer pedir, pagar, saber quando levantar e talvez reservar uma mesa; a equipa precisa organizar tudo sem perder controlo.")
    add_table(doc, ["Tempo", "O que mostrar", "Frase simples para explicar"], [
        ["0 a 1 min", "Tela inicial", "Este sistema integra o atendimento ao cliente e a operação interna do restaurante."],
        ["1 a 2 min", "Login e perfis", "Cada utilizador só tem acesso às funções do seu trabalho."],
        ["2 a 4 min", "Área do cliente e carrinho", "O cliente escolhe produtos, confirma o pedido e informa contacto para receber atualizações."],
        ["4 a 6 min", "Pagamento e pedidos", "Pagamento móvel fica em validação; a cozinha só inicia depois da confirmação da equipa."],
        ["6 a 7 min", "Notificações e chat", "O cliente acompanha o pedido e pode falar com o atendente para dúvidas ou reclamações."],
        ["7 a 9 min", "Reserva", "O sistema valida capacidade e horário para evitar duas reservas na mesma mesa."],
        ["9 a 11 min", "Stock e receita", "Uma venda confirmada baixa os ingredientes necessários, com proteção contra stock insuficiente."],
        ["11 a 12 min", "Dashboard, relatório e backup", "O gerente acompanha indicadores e pode exportar dados para continuidade operacional."],
    ], [2.0, 5.0, 9.8])
    add_heading(doc, "Como explicar POO durante a apresentação", 2)
    add_bullets(doc, [
        "Encapsulamento: os atributos dos objetos, como preço e estado, são manipulados por métodos em vez de ficarem expostos livremente.",
        "Herança: Gerente é uma especialização de Usuario e reutiliza dados comuns, como nome e utilizador.",
        "Polimorfismo: formas de pagamento seguem uma interface comum, mas cada método de pagamento pode ter comportamento próprio.",
        "Abstração: Pedido, Reserva, Produto e Cliente representam elementos reais do restaurante de forma simplificada no programa.",
    ])
    add_heading(doc, "13 Demonstração prática recomendada", 1)
    add_numbered(doc, [
        "Abra a tela inicial e explique os dois caminhos: cliente e equipa.",
        "Entre como admin usando admin e admin123. Mostre que Utilizadores aparece para este perfil.",
        "Abra a área de cliente, coloque um produto no carrinho e finalize com um telefone válido.",
        "Escolha M-Pesa com número iniciado por 84 ou 85. Mostre que o pedido fica em validação.",
        "Volte à área Pedidos como equipa e confirme o pagamento. Só então atualize o pedido para EM PREPARACAO e PRONTO.",
        "Abra as notificações do cliente para demonstrar que ele sabe quando deve levantar o pedido.",
        "Faça uma reserva e tente repetir a mesma mesa no mesmo intervalo; explique que o sistema bloqueia o conflito.",
        "Mostre uma ficha técnica de produto, um item de stock e o dashboard ou relatório de backup.",
    ])


def questions_and_conclusion(doc):
    add_heading(doc, "14 Perguntas prováveis e respostas curtas", 1)
    add_table(doc, ["Pergunta", "Resposta sugerida"], [
        ["Por que usar MVC?", "Porque separa telas, regras e acesso aos dados. Assim é mais fácil corrigir e ampliar o sistema."],
        ["Como impede dupla reserva?", "Verifica a mesa, o número de pessoas e se existe outra reserva pendente ou confirmada que sobreponha o bloco de duas horas."],
        ["Por que pagamento móvel não é automático?", "A versão atual simula a integração. A equipa confirma o comprovativo; numa versão real seria ligado à API da carteira móvel."],
        ["Quando o stock baixa?", "Quando o pagamento é confirmado. Se faltar ingrediente, a transação falha para não deixar informação inconsistente."],
        ["O que acontece sem MySQL?", "O sistema continua em modo temporário para demonstração, mas os dados desaparecem ao fechar."],
        ["Como as senhas são protegidas?", "São guardadas como hash SHA-256. Para produção, a melhoria recomendada é usar BCrypt ou Argon2 com salt."],
        ["Por que existe backup?", "Para exportar os dados e reduzir o risco de perder histórico de vendas, clientes, reservas e stock."],
    ], [6.4, 10.4])
    add_heading(doc, "15 Testes, limitações e próximos passos", 1)
    add_body(doc, "Foram preparados testes automatizados para regras de maior risco: prefixos de carteiras móveis, conflitos de reserva, bloqueio da preparação enquanto o pagamento está pendente, validação da ficha técnica e rejeição de senhas antigas. O recibo PDF também possui teste próprio.")
    add_table(doc, ["Limitação atual", "Melhoria futura"], [
        ["Pagamento é confirmado manualmente", "Integrar APIs oficiais de M-Pesa, e-Mola e mKesh."],
        ["Notificações são internas à aplicação", "Integrar SMS, WhatsApp Business ou push notifications com credenciais autorizadas."],
        ["Senha usa SHA-256 por motivo académico", "Adotar BCrypt ou Argon2, política de senha e recuperação de conta."],
        ["Aplicação é desktop local", "Criar API, versão web ou aplicação móvel para acesso remoto controlado."],
    ], [7.5, 9.3])
    add_heading(doc, "Conclusão", 1)
    add_body(doc, "O valor principal deste projeto é ligar o cliente à operação do restaurante. O cliente recebe informação sobre pedido e reserva; a equipa controla pagamento, preparação, mesas e stock; a gerência acompanha resultados e mantém registos. Durante a apresentação, mostre esta ligação entre problema, regra de negócio e resultado prático.")
    add_text(doc, "Dica final: apresente com calma, siga o roteiro e explique sempre o motivo de cada validação. Isso mostra domínio do sistema mesmo sem decorar todo o código.", size=10.5, bold=True, color=DARK_GREEN, align=WD_ALIGN_PARAGRAPH.LEFT, space_after=0)


def main():
    doc = Document()
    configure_document(doc)
    cover(doc)
    overview(doc)
    architecture(doc)
    accounts_and_start(doc)
    customer_flow(doc)
    orders_payments(doc)
    reservations(doc)
    stock_and_management(doc)
    dashboard_backup_security(doc)
    presentation(doc)
    questions_and_conclusion(doc)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    doc.core_properties.title = "Guia de Estudo e Apresentação do Sistema de Gestão de Restaurante"
    doc.core_properties.subject = "Manual de estudo do Restaurante UEM"
    doc.core_properties.author = "Restaurante UEM"
    doc.save(OUT)
    print(OUT)


if __name__ == "__main__":
    main()

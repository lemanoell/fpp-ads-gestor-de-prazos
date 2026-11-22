<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="br.com.gestordeprazo.model.Usuario" %>
<%@ page import="br.com.gestordeprazo.model.Usuario.Perfil" %>
<%@ page import="br.com.gestordeprazo.model.Categoria" %>
<%@ page import="br.com.gestordeprazo.model.Documento" %>
<%@ page import="br.com.gestordeprazo.model.Documento.SituacaoPrazo" %>
<%@ page import="br.com.gestordeprazo.model.Documento.StatusRegistro" %>
<%
    String ctx = request.getContextPath() != null ? request.getContextPath() : "";
    HttpSession sessaoAtual = request.getSession(false);
    Usuario usuarioLogado = (sessaoAtual != null) ? (Usuario) sessaoAtual.getAttribute("usuarioLogado") : null;
    boolean isAdmin = (usuarioLogado != null && usuarioLogado.getPerfil() == Perfil.ADMIN);

    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
    Map<Integer, List<Documento>> docsPorCategoria = (Map<Integer, List<Documento>>) request.getAttribute("docsPorCategoria");
    DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>
<!DOCTYPE html>
<html lang="pt-BR" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GestorDePrazo - Início</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
</head>
<body>
    <div class="app-shell">
        <jsp:include page="/WEB-INF/jsp/fragments/menu-lateral.jsp" />

        <div class="content-area">
            <main class="container">
                <header class="topbar">
                    <div>
                        <div class="brand-label">Painel Principal</div>
                        <h1>Gestão de Prazos e Pendências</h1>
                    </div>
                    <div class="topbar-actions">
                        <button type="button" class="btn-secondary mobile-menu-btn" data-open-sidebar>Menu</button>
                    </div>
                </header>

                <%
                    String mensagem = (String) request.getAttribute("mensagem");
                    if (mensagem != null && !mensagem.isBlank()) {
                %>
                    <div class="alert" role="status">
                        <strong><%= mensagem %></strong>
                    </div>
                <%
                    }
                %>

                <section class="card">
                    <h2>Filtrar Documentos</h2>
                    <div class="table-toolbar" style="margin-bottom: 0;">
                        <div class="toolbar-filters">
                            <input type="search"
                                   id="filtroBuscaHome"
                                   placeholder="Buscar por nome, setor, responsável ou observação..."
                                   aria-label="Buscar documentos">
                            <select id="filtroStatusHome" aria-label="Filtrar por status">
                                <option value="PENDENTES_ATIVOS" selected>Pendentes ativos (Em dia, Próximo, Vencido)</option>
                                <option value="EM_DIA">Em dia</option>
                                <option value="PROXIMO_VENCIMENTO">Próximo do vencimento (até 7 dias)</option>
                                <option value="VENCIDO">Vencido</option>
                                <option value="CONCLUIDO">Concluído</option>
                                <option value="CANCELADO">Cancelado</option>
                                <option value="TODOS">Todos (incluindo concluídos e cancelados)</option>
                            </select>
                        </div>
                        <span id="contadorDocumentosHome" class="count-info"></span>
                    </div>
                </section>

                <%
                    if (categorias == null || categorias.isEmpty()) {
                %>
                    <section class="card">
                        <p class="empty-state">Nenhuma categoria cadastrada no sistema.</p>
                    </section>
                <%
                    } else {
                        for (Categoria cat : categorias) {
                            List<Documento> listaCat = (docsPorCategoria != null)
                                    ? docsPorCategoria.getOrDefault(cat.getId(), Collections.emptyList())
                                    : Collections.emptyList();
                %>
                    <section class="card" data-categoria-section>
                        <h2><%= cat.getNome() %></h2>

                        <% if (listaCat.isEmpty()) { %>
                            <p class="empty-state">Nenhum documento cadastrado nesta categoria.</p>
                        <% } else { %>
                            <div class="table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>Setor</th>
                                            <th>Categoria</th>
                                            <th>Responsável</th>
                                            <th>Vencimento / Situação</th>
                                            <th>Observações</th>
                                            <th>Status</th>
                                            <th>Ações</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (Documento d : listaCat) {
                                                SituacaoPrazo sit = d.getSituacaoPrazo();
                                                StatusRegistro stReg = (d.getStatusRegistro() != null)
                                                        ? d.getStatusRegistro()
                                                        : StatusRegistro.PENDENTE;
                                                boolean encerrado = (stReg == StatusRegistro.CONCLUIDO || stReg == StatusRegistro.CANCELADO);

                                                String badgeClass = "badge-role";
                                                if (sit == SituacaoPrazo.VENCIDO || sit == SituacaoPrazo.CANCELADO) {
                                                    badgeClass = "badge-inactive";
                                                } else if (sit == SituacaoPrazo.CONCLUIDO) {
                                                    badgeClass = "badge-active";
                                                }

                                                String dataFmt = (d.getDataVencimento() != null) ? d.getDataVencimento().format(fmtData) : "";
                                                String buscaLinha = ((d.getNome() != null ? d.getNome() : "") + " "
                                                        + (d.getSetorNome() != null ? d.getSetorNome() : "") + " "
                                                        + (d.getCategoriaNome() != null ? d.getCategoriaNome() : "") + " "
                                                        + (d.getResponsavelBeneficiario() != null ? d.getResponsavelBeneficiario() : "") + " "
                                                        + (d.getObservacoes() != null ? d.getObservacoes() : "")).toLowerCase();
                                        %>
                                        <tr data-doc-row
                                            data-search="<%= buscaLinha %>"
                                            data-situacao="<%= sit.name() %>"
                                            data-status-registro="<%= stReg.name() %>"
                                            data-encerrado="<%= encerrado %>">
                                            <td><strong><%= d.getNome() %></strong></td>
                                            <td><%= d.getSetorNome() %></td>
                                            <td><%= d.getCategoriaNome() %></td>
                                            <td><%= d.getResponsavelBeneficiario() %></td>
                                            <td><%= dataFmt %></td>
                                            <td><%= (d.getObservacoes() != null && !d.getObservacoes().isBlank()) ? d.getObservacoes() : "—" %></td>
                                            <td><span class="badge <%= badgeClass %>"><%= sit.getRotulo() %></span></td>
                                            <td>
                                                <div style="display: flex; flex-wrap: wrap; gap: 6px; align-items: center;">
                                                    <% if (isAdmin) { %>
                                                        <a href="<%= ctx %>/registrar-documento?id=<%= d.getId() %>"
                                                           class="btn-secondary">Editar</a>
                                                    <% } %>

                                                    <% if (!encerrado) { %>
                                                        <form action="<%= ctx %>/home" method="post" class="inline-form" data-loading-text="Concluindo...">
                                                            <input type="hidden" name="acao" value="confirmar">
                                                            <input type="hidden" name="id" value="<%= d.getId() %>">
                                                            <button type="submit" class="btn-success-soft">Confirmar</button>
                                                        </form>

                                                        <form action="<%= ctx %>/home"
                                                              method="post"
                                                              class="inline-form"
                                                              data-loading-text="Cancelando..."
                                                              data-confirm="Deseja cancelar o documento <%= d.getNome() %>?">
                                                            <input type="hidden" name="acao" value="cancelar">
                                                            <input type="hidden" name="id" value="<%= d.getId() %>">
                                                            <button type="submit" class="btn-danger-soft">Cancelar</button>
                                                        </form>
                                                    <% } %>

                                                    <form action="<%= ctx %>/home"
                                                          method="post"
                                                          class="inline-form"
                                                          data-duplicar-form
                                                          data-loading-text="Duplicando...">
                                                        <input type="hidden" name="acao" value="duplicar">
                                                        <input type="hidden" name="id" value="<%= d.getId() %>">
                                                        <input type="date"
                                                               name="novaDataVencimento"
                                                               aria-label="Nova data de vencimento"
                                                               data-duplicar-data
                                                               style="display: none; width: auto; padding: 6px 8px; font-size: 0.82rem;">
                                                        <button type="button" class="btn-secondary" data-duplicar-trigger>Duplicar</button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </div>
                        <% } %>
                    </section>
                <%
                        }
                    }
                %>
            </main>
        </div>
    </div>

    <script src="<%= ctx %>/js/app.js"></script>
</body>
</html>
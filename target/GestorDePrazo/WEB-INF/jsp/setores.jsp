<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.gestordeprazo.model.Setor" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="pt-BR" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GestorDePrazo - Cadastro de Setores</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
</head>
<body>
    <div class="app-shell">
        <jsp:include page="/WEB-INF/jsp/fragments/menu-lateral.jsp" />

        <div class="content-area">
            <main class="container">
                <header class="topbar">
                    <div>
                        <div class="brand-label">Administração</div>
                        <h1>Cadastro de Setores</h1>
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
                    <h2>Novo Setor</h2>
                    <form action="<%= ctx %>/setores" method="post" class="form-grid" data-loading-text="Cadastrando...">
                        <input type="hidden" name="acao" value="cadastrar">

                        <div class="field">
                            <label for="nome">Nome do setor</label>
                            <input type="text" id="nome" name="nome" placeholder="Ex.: Financeiro, Jurídico, TI" required>
                        </div>

                        <div class="field">
                            <button type="submit" class="btn-primary">Cadastrar setor</button>
                        </div>
                    </form>
                </section>

                <section class="card">
                    <h2>Setores Cadastrados</h2>

                    <%
                        List<Setor> setores = (List<Setor>) request.getAttribute("setores");
                        if (setores == null || setores.isEmpty()) {
                    %>
                        <p class="empty-state">Nenhum setor cadastrado.</p>
                    <%
                        } else {
                    %>
                        <div class="table-toolbar">
                            <div class="toolbar-filters">
                                <input type="search"
                                       data-table-search="#tabelaSetores"
                                       placeholder="Buscar por ID ou nome do setor..."
                                       aria-label="Buscar setores">
                                <select data-table-status-filter="#tabelaSetores" aria-label="Filtrar por status">
                                    <option value="TODOS">Todos os status</option>
                                    <option value="ATIVO">Ativos</option>
                                    <option value="INATIVO">Inativos</option>
                                </select>
                            </div>
                            <span data-table-count data-unit="setor(es)" class="count-info"></span>
                        </div>

                        <div class="table-wrapper">
                            <table id="tabelaSetores">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nome</th>
                                        <th>Status</th>
                                        <th>Ação</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Setor s : setores) {
                                            String nomeSeguro = s.getNome() != null ? s.getNome() : "";
                                            boolean ativo = s.isAtivo();
                                            String buscaTexto = (s.getId() + " " + nomeSeguro).toLowerCase();
                                            String statusChave = ativo ? "ATIVO" : "INATIVO";
                                    %>
                                    <tr data-search="<%= buscaTexto %>" data-status="<%= statusChave %>">
                                        <td><%= s.getId() %></td>
                                        <td><strong><%= nomeSeguro %></strong></td>
                                        <td>
                                            <span class="badge <%= ativo ? "badge-active" : "badge-inactive" %>">
                                                <%= ativo ? "Ativo" : "Inativo" %>
                                            </span>
                                        </td>
                                        <td>
                                            <form action="<%= ctx %>/setores"
                                                  method="post"
                                                  class="inline-form"
                                                  data-loading-text="Atualizando..."
                                                  <% if (ativo) { %>
                                                      data-confirm="Deseja realmente desativar o setor <%= nomeSeguro %>?"
                                                  <% } %>>
                                                <input type="hidden" name="acao" value="alternar_status">
                                                <input type="hidden" name="id" value="<%= s.getId() %>">
                                                <input type="hidden" name="novoStatus" value="<%= !ativo %>">
                                                <button type="submit" class="<%= ativo ? "btn-danger-soft" : "btn-success-soft" %>">
                                                    <%= ativo ? "Desativar" : "Ativar" %>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    <%
                        }
                    %>
                </section>
            </main>
        </div>
    </div>

    <script src="<%= ctx %>/js/app.js"></script>
</body>
</html>
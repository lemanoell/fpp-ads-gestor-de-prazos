<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.gestordeprazo.model.Categoria" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="pt-BR" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GestorDePrazo - Cadastro de Categorias</title>
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
                        <h1>Cadastro de Categorias</h1>
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
                    <h2>Nova Categoria</h2>
                    <form action="<%= ctx %>/categorias" method="post" class="form-grid" data-loading-text="Cadastrando...">
                        <input type="hidden" name="acao" value="cadastrar">

                        <div class="field">
                            <label for="nome">Nome da categoria</label>
                            <input type="text" id="nome" name="nome" placeholder="Ex.: Financeiro, Jurídico, Operacional" required>
                        </div>

                        <div class="field">
                            <button type="submit" class="btn-primary">Cadastrar categoria</button>
                        </div>
                    </form>
                </section>

                <section class="card">
                    <h2>Categorias Cadastradas</h2>

                    <%
                        List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
                        if (categorias == null || categorias.isEmpty()) {
                    %>
                        <p class="empty-state">Nenhuma categoria cadastrada.</p>
                    <%
                        } else {
                    %>
                        <div class="table-toolbar">
                            <div class="toolbar-filters">
                                <input type="search"
                                       data-table-search="#tabelaCategorias"
                                       placeholder="Buscar por ID ou nome da categoria..."
                                       aria-label="Buscar categorias">
                                <select data-table-status-filter="#tabelaCategorias" aria-label="Filtrar por status">
                                    <option value="TODOS">Todos os status</option>
                                    <option value="ATIVO">Ativas</option>
                                    <option value="INATIVO">Inativas</option>
                                </select>
                            </div>
                            <span data-table-count data-unit="categoria(s)" class="count-info"></span>
                        </div>

                        <div class="table-wrapper">
                            <table id="tabelaCategorias">
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
                                        for (Categoria c : categorias) {
                                            String nomeSeguro = c.getNome() != null ? c.getNome() : "";
                                            boolean ativo = c.isAtivo();
                                            String buscaTexto = (c.getId() + " " + nomeSeguro).toLowerCase();
                                            String statusChave = ativo ? "ATIVO" : "INATIVO";
                                    %>
                                    <tr data-search="<%= buscaTexto %>" data-status="<%= statusChave %>">
                                        <td><%= c.getId() %></td>
                                        <td><strong><%= nomeSeguro %></strong></td>
                                        <td>
                                            <span class="badge <%= ativo ? "badge-active" : "badge-inactive" %>">
                                                <%= ativo ? "Ativa" : "Inativa" %>
                                            </span>
                                        </td>
                                        <td>
                                            <form action="<%= ctx %>/categorias"
                                                  method="post"
                                                  class="inline-form"
                                                  data-loading-text="Atualizando..."
                                                  <% if (ativo) { %>
                                                      data-confirm="Deseja realmente desativar a categoria <%= nomeSeguro %>?"
                                                  <% } %>>
                                                <input type="hidden" name="acao" value="alternar_status">
                                                <input type="hidden" name="id" value="<%= c.getId() %>">
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
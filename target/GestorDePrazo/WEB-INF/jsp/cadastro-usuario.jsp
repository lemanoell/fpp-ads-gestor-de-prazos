<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.gestordeprazo.model.Usuario" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="pt-BR" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GestorDePrazo - Gerenciamento de Usuários</title>
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
                        <h1>Gerenciamento de Usuários</h1>
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
                    <h2>Novo Usuário</h2>
                    <form action="<%= ctx %>/cadastro-usuario" method="post" class="form-grid" data-loading-text="Cadastrando...">
                        <input type="hidden" name="acao" value="cadastrar">

                        <div class="field">
                            <label for="nome">Nome</label>
                            <input type="text" id="nome" name="nome" placeholder="Nome completo" required>
                        </div>

                        <div class="field">
                            <label for="email">E-mail</label>
                            <input type="email" id="email" name="email" placeholder="usuario@empresa.com" required>
                        </div>

                        <div class="field">
                            <label for="senha">Senha</label>
                            <input type="password" id="senha" name="senha" placeholder="Defina uma senha" required>
                        </div>

                        <div class="field">
                            <label for="perfil">Perfil</label>
                            <select id="perfil" name="perfil" required>
                                <option value="USUARIO">USUARIO</option>
                                <option value="GESTOR">GESTOR</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>

                        <div class="field">
                            <button type="submit" class="btn-primary">Cadastrar usuário</button>
                        </div>
                    </form>
                </section>

                <section class="card">
                    <h2>Usuários Cadastrados</h2>

                    <%
                        List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                        if (usuarios == null || usuarios.isEmpty()) {
                    %>
                        <p class="empty-state">Nenhum usuário cadastrado.</p>
                    <%
                        } else {
                    %>
                        <div class="table-toolbar">
                            <div class="toolbar-filters">
                                <input type="search"
                                       data-table-search="#tabelaUsuarios"
                                       placeholder="Buscar por ID, nome, e-mail ou perfil..."
                                       aria-label="Buscar usuários">
                                <select data-table-status-filter="#tabelaUsuarios" aria-label="Filtrar por status">
                                    <option value="TODOS">Todos os status</option>
                                    <option value="ATIVO">Ativos</option>
                                    <option value="INATIVO">Inativos</option>
                                </select>
                            </div>
                            <span data-table-count data-unit="usuário(s)" class="count-info"></span>
                        </div>

                        <div class="table-wrapper">
                            <table id="tabelaUsuarios">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nome</th>
                                        <th>E-mail</th>
                                        <th>Perfil</th>
                                        <th>Status</th>
                                        <th>Alterar Perfil</th>
                                        <th>Ação</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Usuario u : usuarios) {
                                            String nomeSeguro = u.getNome() != null ? u.getNome() : "";
                                            String emailSeguro = u.getEmail() != null ? u.getEmail() : "";
                                            String perfilNome = u.getPerfil() != null ? u.getPerfil().name() : "USUARIO";
                                            boolean ativo = u.isAtivo();
                                            String buscaTexto = (u.getId() + " " + nomeSeguro + " " + emailSeguro + " " + perfilNome).toLowerCase();
                                            String statusChave = ativo ? "ATIVO" : "INATIVO";
                                    %>
                                    <tr data-search="<%= buscaTexto %>" data-status="<%= statusChave %>">
                                        <td><%= u.getId() %></td>
                                        <td><strong><%= nomeSeguro %></strong></td>
                                        <td><%= emailSeguro %></td>
                                        <td><span class="badge badge-role"><%= perfilNome %></span></td>
                                        <td>
                                            <span class="badge <%= ativo ? "badge-active" : "badge-inactive" %>">
                                                <%= ativo ? "Ativo" : "Inativo" %>
                                            </span>
                                        </td>
                                        <td>
                                            <form action="<%= ctx %>/cadastro-usuario"
                                                  method="post"
                                                  class="inline-form"
                                                  data-loading-text="Salvando...">
                                                <input type="hidden" name="acao" value="alterar_perfil">
                                                <input type="hidden" name="id" value="<%= u.getId() %>">
                                                <select name="perfil" aria-label="Perfil de <%= nomeSeguro %>">
                                                    <option value="USUARIO" <%= u.getPerfil() == Usuario.Perfil.USUARIO ? "selected" : "" %>>USUARIO</option>
                                                    <option value="GESTOR" <%= u.getPerfil() == Usuario.Perfil.GESTOR ? "selected" : "" %>>GESTOR</option>
                                                    <option value="ADMIN" <%= u.getPerfil() == Usuario.Perfil.ADMIN ? "selected" : "" %>>ADMIN</option>
                                                </select>
                                                <button type="submit" class="btn-secondary">Salvar</button>
                                            </form>
                                        </td>
                                        <td>
                                            <form action="<%= ctx %>/cadastro-usuario"
                                                  method="post"
                                                  class="inline-form"
                                                  data-loading-text="Atualizando..."
                                                  <% if (ativo) { %>
                                                      data-confirm="Deseja realmente desativar <%= nomeSeguro %>?"
                                                  <% } %>>
                                                <input type="hidden" name="acao" value="alternar_status">
                                                <input type="hidden" name="id" value="<%= u.getId() %>">
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
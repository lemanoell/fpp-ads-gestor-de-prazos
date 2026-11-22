<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.gestordeprazo.model.Usuario" %>
<%@ page import="br.com.gestordeprazo.model.Usuario.Perfil" %>
<%
    String menuCtx = request.getContextPath() != null ? request.getContextPath() : "";
    HttpSession menuSessao = request.getSession(false);
    Usuario menuUsuario = (menuSessao != null) ? (Usuario) menuSessao.getAttribute("usuarioLogado") : null;
    if (menuUsuario == null && request.getAttribute("usuarioLogado") instanceof Usuario) {
        menuUsuario = (Usuario) request.getAttribute("usuarioLogado");
    }

    Perfil menuPerfil = (menuUsuario != null) ? menuUsuario.getPerfil() : null;
    boolean menuIsAdmin = (menuPerfil == Perfil.ADMIN);
    boolean menuPodeVerIndicadores = (menuPerfil != null && menuPerfil != Perfil.USUARIO);

    String uriAtual = request.getRequestURI() != null ? request.getRequestURI() : "";
    boolean isGerenciamento = uriAtual.endsWith("/home") || uriAtual.endsWith("/home.jsp");
    boolean isRegistrarDocumento = uriAtual.endsWith("/registrar-documento") || uriAtual.endsWith("/registrar-documento.jsp");
    boolean isIndicadores = uriAtual.endsWith("/indicadores") || uriAtual.endsWith("/indicadores.jsp");
    boolean isUsuarios = uriAtual.endsWith("/cadastro-usuario") || uriAtual.endsWith("/cadastro-usuario.jsp");
    boolean isCategorias = uriAtual.endsWith("/categorias") || uriAtual.endsWith("/categorias.jsp");
    boolean isSetores = uriAtual.endsWith("/setores") || uriAtual.endsWith("/setores.jsp");
    boolean isConfigAberta = isUsuarios || isCategorias || isSetores;
%>
<div class="sidebar-backdrop" data-sidebar-backdrop></div>

<aside class="sidebar" data-sidebar aria-label="Menu lateral">
    <div>
        <div class="sidebar-brand">
            <a href="<%= menuCtx %>/home" class="brand-logo-link" aria-label="GestorDePrazo - Gerenciamento">
                <img src="<%= menuCtx %>/img/logo.png" alt="GestorDePrazo" class="brand-logo-img">
            </a>
        </div>

        <nav class="sidebar-nav">
            <a href="<%= menuCtx %>/home" class="nav-btn <%= isGerenciamento ? "is-active" : "" %>">Gerenciamento</a>
            <a href="<%= menuCtx %>/registrar-documento"
               class="nav-btn <%= isRegistrarDocumento ? "is-active" : "" %>">Registrar Documento</a>
            <% if (menuPodeVerIndicadores) { %>
                <a href="<%= menuCtx %>/indicadores"
                   class="nav-btn <%= isIndicadores ? "is-active" : "" %>">Indicadores on-line</a>
            <% } %>

            <% if (menuIsAdmin) { %>
                <div class="nav-group <%= isConfigAberta ? "is-open" : "" %>" data-collapse-group>
                    <button type="button"
                            class="nav-group-toggle"
                            data-collapse-toggle
                            aria-expanded="<%= isConfigAberta %>">
                        <span>Configurações</span>
                        <span class="nav-group-chevron" aria-hidden="true">›</span>
                    </button>
                    <div class="nav-submenu">
                        <a href="<%= menuCtx %>/cadastro-usuario"
                           class="nav-btn nav-subbtn <%= isUsuarios ? "is-active" : "" %>">Usuários</a>
                        <a href="<%= menuCtx %>/categorias"
                           class="nav-btn nav-subbtn <%= isCategorias ? "is-active" : "" %>">Categorias</a>
                        <a href="<%= menuCtx %>/setores"
                           class="nav-btn nav-subbtn <%= isSetores ? "is-active" : "" %>">Setores</a>
                    </div>
                </div>
            <% } %>
        </nav>
    </div>

    <div class="sidebar-footer">
        <button type="button" class="btn-secondary" data-toggle-theme>Tema escuro</button>
        <a href="<%= menuCtx %>/logout" class="btn-link btn-danger-soft">Sair</a>
    </div>
</aside>
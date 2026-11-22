<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<%@ page import="br.com.gestordeprazo.model.Documento.SituacaoPrazo" %>
<%
    String ctx = request.getContextPath() != null ? request.getContextPath() : "";
    Integer totalObj = (Integer) request.getAttribute("totalDocumentos");
    int totalDocumentos = (totalObj != null) ? totalObj : 0;

    @SuppressWarnings("unchecked")
    Map<SituacaoPrazo, Integer> porSituacao = (Map<SituacaoPrazo, Integer>) request.getAttribute("porSituacao");
    if (porSituacao == null) {
        porSituacao = Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    Map<String, Integer> porCategoria = (Map<String, Integer>) request.getAttribute("porCategoria");
    if (porCategoria == null) {
        porCategoria = Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    Map<String, Integer> porSetor = (Map<String, Integer>) request.getAttribute("porSetor");
    if (porSetor == null) {
        porSetor = Collections.emptyMap();
    }
%>
<!DOCTYPE html>
<html lang="pt-BR" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GestorDePrazo - Indicadores on-line</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
</head>
<body>
    <div class="app-shell">
        <jsp:include page="/WEB-INF/jsp/fragments/menu-lateral.jsp" />

        <div class="content-area">
            <main class="container">
                <header class="topbar">
                    <div>
                        <div class="brand-label">Painel Gerencial</div>
                        <h1>Indicadores on-line</h1>
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
                    <h2>Resumo por Situação de Prazo</h2>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>Situação</th>
                                    <th>Quantidade</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><strong>Total de Documentos</strong></td>
                                    <td><strong><%= totalDocumentos %></strong></td>
                                </tr>
                                <%
                                    for (SituacaoPrazo sit : SituacaoPrazo.values()) {
                                        int qtd = porSituacao.getOrDefault(sit, 0);
                                %>
                                <tr>
                                    <td><%= sit.getRotulo() %></td>
                                    <td><%= qtd %></td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </section>

                <section class="card">
                    <h2>Resumo por Categoria (Tipo de Documento)</h2>
                    <% if (porCategoria.isEmpty()) { %>
                        <p class="empty-state">Nenhum documento registrado para compor o resumo por categoria.</p>
                    <% } else { %>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Categoria</th>
                                        <th>Quantidade</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
                                    %>
                                    <tr>
                                        <td><%= entry.getKey() %></td>
                                        <td><%= entry.getValue() %></td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </section>

                <section class="card">
                    <h2>Resumo por Setor</h2>
                    <% if (porSetor.isEmpty()) { %>
                        <p class="empty-state">Nenhum documento registrado para compor o resumo por setor.</p>
                    <% } else { %>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Setor</th>
                                        <th>Quantidade</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Map.Entry<String, Integer> entry : porSetor.entrySet()) {
                                    %>
                                    <tr>
                                        <td><%= entry.getKey() %></td>
                                        <td><%= entry.getValue() %></td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </section>
            </main>
        </div>
    </div>

    <script src="<%= ctx %>/js/app.js"></script>
</body>
</html>
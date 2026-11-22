<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.gestordeprazo.model.Documento" %>
<%@ page import="br.com.gestordeprazo.model.Documento.TipoPessoa" %>
<%@ page import="br.com.gestordeprazo.model.Setor" %>
<%@ page import="br.com.gestordeprazo.model.Categoria" %>
<%
    String ctx = request.getContextPath();
    Documento doc = (Documento) request.getAttribute("documento");
    boolean editando = (doc != null && doc.getId() > 0);

    String nomeVal = (doc != null && doc.getNome() != null) ? doc.getNome() : "";
    int setorSel = (doc != null) ? doc.getSetorId() : 0;
    int categoriaSel = (doc != null) ? doc.getCategoriaId() : 0;
    TipoPessoa tipoSel = (doc != null) ? doc.getTipoPessoa() : null;
    String respVal = (doc != null && doc.getResponsavelBeneficiario() != null) ? doc.getResponsavelBeneficiario() : "";
    String vencVal = (doc != null) ? doc.getDataVencimentoIso() : "";
    String obsVal = (doc != null && doc.getObservacoes() != null) ? doc.getObservacoes() : "";

    List<Setor> setores = (List<Setor>) request.getAttribute("setores");
    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
%>
<!DOCTYPE html>
<html lang="pt-BR" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GestorDePrazo - Registro de Documento/Pendência</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
</head>
<body>
    <div class="app-shell">
        <jsp:include page="/WEB-INF/jsp/fragments/menu-lateral.jsp" />

        <div class="content-area">
            <main class="container">
                <header class="topbar">
                    <div>
                        <div class="brand-label">Registro de Documento/Pendência</div>
                        <h1><%= editando ? "Editar Documento / Pendência #" + doc.getId() : "Registro de Documento / Pendência" %></h1>
                    </div>
                    <div class="topbar-actions">
                        <% if (editando) { %>
                            <a href="<%= ctx %>/registrar-documento" class="btn-secondary">Novo registro</a>
                        <% } %>
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
                    <h2><%= editando ? "Dados do Registro" : "Novo Registro" %></h2>

                    <form action="<%= ctx %>/registrar-documento" method="post" class="form-grid" data-loading-text="Salvando...">
                        <% if (editando) { %>
                            <input type="hidden" name="id" value="<%= doc.getId() %>">
                        <% } %>

                        <div class="field">
                            <label for="nome">Nome do registro</label>
                            <input type="text"
                                   id="nome"
                                   name="nome"
                                   value="<%= nomeVal %>"
                                   placeholder="ex. boleto"
                                   required>
                        </div>

                        <div class="field">
                            <label for="setorId">Setor</label>
                            <select id="setorId" name="setorId" required>
                                <option value="">Selecione...</option>
                                <%
                                    if (setores != null) {
                                        for (Setor s : setores) {
                                            String selected = (s.getId() == setorSel) ? "selected" : "";
                                %>
                                    <option value="<%= s.getId() %>" <%= selected %>><%= s.getNome() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <div class="field">
                            <label for="categoriaId">Categoria</label>
                            <select id="categoriaId" name="categoriaId" required>
                                <option value="">selecione a categoria</option>
                                <%
                                    if (categorias != null) {
                                        for (Categoria c : categorias) {
                                            String selected = (c.getId() == categoriaSel) ? "selected" : "";
                                %>
                                    <option value="<%= c.getId() %>" <%= selected %>><%= c.getNome() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <div class="field">
                            <label for="tipoPessoa">Tipo de Pessoa</label>
                            <select id="tipoPessoa" name="tipoPessoa" required>
                                <option value="">selecione</option>
                                <%
                                    for (TipoPessoa tp : TipoPessoa.values()) {
                                        String selected = (tp == tipoSel) ? "selected" : "";
                                %>
                                    <option value="<%= tp.name() %>" <%= selected %>><%= tp.getRotulo() %></option>
                                <%
                                    }
                                %>
                            </select>
                        </div>

                        <div class="field">
                            <label for="responsavelBeneficiario">Responsável/Beneficiário</label>
                            <input type="text"
                                   id="responsavelBeneficiario"
                                   name="responsavelBeneficiario"
                                   value="<%= respVal %>"
                                   placeholder="nome da pessoa/empresa beneficiada"
                                   required>
                        </div>

                        <div class="field">
                            <label for="dataVencimento">Data de vencimento</label>
                            <input type="date"
                                   id="dataVencimento"
                                   name="dataVencimento"
                                   value="<%= vencVal %>"
                                   placeholder="dd/mm/aaaa"
                                   required>
                        </div>

                        <div class="field" style="grid-column: 1 / -1;">
                            <label for="observacoes">Observações</label>
                            <textarea id="observacoes"
                                      name="observacoes"
                                      rows="4"
                                      placeholder="Registre observações sobre esse documento"><%= obsVal %></textarea>
                        </div>

                        <div class="field">
                            <button type="submit" class="btn-primary">Salvar</button>
                        </div>
                    </form>
                </section>
            </main>
        </div>
    </div>

    <script src="<%= ctx %>/js/app.js"></script>
</body>
</html>
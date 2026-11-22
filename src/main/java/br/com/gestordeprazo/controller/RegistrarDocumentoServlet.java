package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.CategoriaDAO;
import br.com.gestordeprazo.dao.DocumentoDAO;
import br.com.gestordeprazo.dao.SetorDAO;
import br.com.gestordeprazo.model.Categoria;
import br.com.gestordeprazo.model.Documento;
import br.com.gestordeprazo.model.Documento.TipoPessoa;
import br.com.gestordeprazo.model.Setor;
import br.com.gestordeprazo.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/registrar-documento")
public class RegistrarDocumentoServlet extends HttpServlet {

    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final SetorDAO setorDAO = new SetorDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuario = (sessao != null) ? (Usuario) sessao.getAttribute("usuarioLogado") : null;

        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        if (sessao != null) {
            Object msgSessao = sessao.getAttribute("mensagem");
            if (msgSessao != null) {
                req.setAttribute("mensagem", msgSessao.toString());
                sessao.removeAttribute("mensagem");
            }
        }

        Documento documentoEdicao = null;
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                documentoEdicao = documentoDAO.buscarPorId(id);
                if (documentoEdicao == null) {
                    req.setAttribute("mensagem", "Documento não encontrado para edição.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("mensagem", "Identificador de documento inválido.");
            } catch (SQLException e) {
                req.setAttribute("mensagem", "Erro ao buscar documento: " + e.getMessage());
            }
        }

        carregarOpcoes(req, documentoEdicao);
        req.setAttribute("documento", documentoEdicao);
        req.getRequestDispatcher("/WEB-INF/jsp/registrar-documento.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession sessao = req.getSession(false);
        Usuario usuario = (sessao != null) ? (Usuario) sessao.getAttribute("usuarioLogado") : null;

        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        String idStr = req.getParameter("id");
        String nome = req.getParameter("nome");
        String setorIdStr = req.getParameter("setorId");
        String categoriaIdStr = req.getParameter("categoriaId");
        String tipoPessoaStr = req.getParameter("tipoPessoa");
        String responsavel = req.getParameter("responsavelBeneficiario");
        String dataVencStr = req.getParameter("dataVencimento");
        String observacoes = req.getParameter("observacoes");

        Documento rascunho = new Documento();
        int idEdicao = 0;
        if (idStr != null && !idStr.isBlank()) {
            try {
                idEdicao = Integer.parseInt(idStr.trim());
                rascunho.setId(idEdicao);
            } catch (NumberFormatException ignored) {
            }
        }
        rascunho.setNome(nome);
        rascunho.setResponsavelBeneficiario(responsavel);
        rascunho.setObservacoes(observacoes);

        try {
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Informe o nome do registro.");
            }
            if (setorIdStr == null || setorIdStr.isBlank()) {
                throw new IllegalArgumentException("Selecione um setor.");
            }
            if (categoriaIdStr == null || categoriaIdStr.isBlank()) {
                throw new IllegalArgumentException("Selecione uma categoria.");
            }
            if (tipoPessoaStr == null || tipoPessoaStr.isBlank()) {
                throw new IllegalArgumentException("Selecione o tipo de pessoa.");
            }
            if (responsavel == null || responsavel.isBlank()) {
                throw new IllegalArgumentException("Informe o responsável ou beneficiário.");
            }
            if (dataVencStr == null || dataVencStr.isBlank()) {
                throw new IllegalArgumentException("Informe a data de vencimento.");
            }

            int setorId = Integer.parseInt(setorIdStr.trim());
            int categoriaId = Integer.parseInt(categoriaIdStr.trim());
            TipoPessoa tipoPessoa = TipoPessoa.valueOf(tipoPessoaStr.trim());
            LocalDate dataVencimento = LocalDate.parse(dataVencStr.trim());

            rascunho.setSetorId(setorId);
            rascunho.setCategoriaId(categoriaId);
            rascunho.setTipoPessoa(tipoPessoa);
            rascunho.setDataVencimento(dataVencimento);
            rascunho.setUsuarioId(usuario.getId());

            if (idEdicao > 0) {
                documentoDAO.atualizar(rascunho);
                sessao.setAttribute("mensagem", "Documento/pendência atualizado com sucesso!");
                resp.sendRedirect(req.getContextPath() + "/registrar-documento?id=" + idEdicao);
            } else {
                documentoDAO.inserir(rascunho);
                sessao.setAttribute("mensagem", "Documento/pendência registrado com sucesso!");
                resp.sendRedirect(req.getContextPath() + "/registrar-documento");
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            req.setAttribute("mensagem", e instanceof DateTimeParseException
                    ? "Data de vencimento inválida."
                    : e.getMessage());
            req.setAttribute("documento", rascunho);
            carregarOpcoes(req, rascunho);
            req.getRequestDispatcher("/WEB-INF/jsp/registrar-documento.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("mensagem", "Erro ao salvar documento: " + e.getMessage());
            req.setAttribute("documento", rascunho);
            carregarOpcoes(req, rascunho);
            req.getRequestDispatcher("/WEB-INF/jsp/registrar-documento.jsp").forward(req, resp);
        }
    }

    private void carregarOpcoes(HttpServletRequest req, Documento docAtual) {
        try {
            List<Setor> ativosSetores = setorDAO.listarTodos().stream()
                    .filter(s -> s.isAtivo() || (docAtual != null && s.getId() == docAtual.getSetorId()))
                    .collect(Collectors.toList());
            List<Categoria> ativasCategorias = categoriaDAO.listarTodas().stream()
                    .filter(c -> c.isAtivo() || (docAtual != null && c.getId() == docAtual.getCategoriaId()))
                    .collect(Collectors.toList());

            req.setAttribute("setores", ativosSetores);
            req.setAttribute("categorias", ativasCategorias);
        } catch (SQLException e) {
            req.setAttribute("setores", Collections.emptyList());
            req.setAttribute("categorias", Collections.emptyList());
            if (req.getAttribute("mensagem") == null) {
                req.setAttribute("mensagem", "Erro ao carregar setores ou categorias: " + e.getMessage());
            }
        }
    }
}
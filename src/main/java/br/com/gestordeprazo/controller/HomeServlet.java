package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.CategoriaDAO;
import br.com.gestordeprazo.dao.DocumentoDAO;
import br.com.gestordeprazo.model.Categoria;
import br.com.gestordeprazo.model.Documento;
import br.com.gestordeprazo.model.Documento.StatusRegistro;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final DocumentoDAO documentoDAO = new DocumentoDAO();
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

        try {
            List<Categoria> categorias = categoriaDAO.listarTodas();
            List<Documento> documentos = documentoDAO.listarTodosComDetalhes();

            Map<Integer, List<Documento>> docsPorCategoria = new LinkedHashMap<>();
            for (Categoria cat : categorias) {
                docsPorCategoria.put(cat.getId(), new ArrayList<>());
            }
            for (Documento doc : documentos) {
                docsPorCategoria
                        .computeIfAbsent(doc.getCategoriaId(), k -> new ArrayList<>())
                        .add(doc);
            }

            req.setAttribute("categorias", categorias);
            req.setAttribute("docsPorCategoria", docsPorCategoria);
        } catch (SQLException e) {
            req.setAttribute("categorias", Collections.emptyList());
            req.setAttribute("docsPorCategoria", Collections.emptyMap());
            req.setAttribute("mensagem", "Erro ao carregar painel de documentos: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
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

        String acao = req.getParameter("acao");
        String mensagem;

        try {
            int id = Integer.parseInt(req.getParameter("id"));

            if ("confirmar".equals(acao)) {
                documentoDAO.atualizarStatusRegistro(id, StatusRegistro.CONCLUIDO);
                mensagem = "Documento concluído com sucesso!";
            } else if ("cancelar".equals(acao)) {
                documentoDAO.atualizarStatusRegistro(id, StatusRegistro.CANCELADO);
                mensagem = "Documento cancelado.";
            } else if ("duplicar".equals(acao)) {
                String novaDataStr = req.getParameter("novaDataVencimento");
                if (novaDataStr == null || novaDataStr.isBlank()) {
                    mensagem = "Informe uma nova data de vencimento para duplicar o documento.";
                } else {
                    LocalDate novaData = LocalDate.parse(novaDataStr.trim());
                    documentoDAO.duplicarComNovaData(id, novaData, usuario.getId());
                    mensagem = "Documento duplicado com nova data de vencimento!";
                }
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Identificador de documento inválido.";
        } catch (DateTimeParseException e) {
            mensagem = "Nova data de vencimento inválida.";
        } catch (SQLException e) {
            mensagem = "Erro ao atualizar documento: " + e.getMessage();
        }

        sessao.setAttribute("mensagem", mensagem);
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
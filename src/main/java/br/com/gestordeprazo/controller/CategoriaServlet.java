package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.CategoriaDAO;
import br.com.gestordeprazo.model.Categoria;
import br.com.gestordeprazo.model.Usuario;
import br.com.gestordeprazo.model.Usuario.Perfil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {

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

        if (usuario.getPerfil() != Perfil.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/home");
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
            req.setAttribute("categorias", categorias);
        } catch (SQLException e) {
            req.setAttribute("categorias", Collections.emptyList());
            req.setAttribute("mensagem", "Erro ao carregar categorias: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/categorias.jsp").forward(req, resp);
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

        if (usuario.getPerfil() != Perfil.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String acao = req.getParameter("acao");
        String mensagem;

        try {
            if ("cadastrar".equals(acao)) {
                String nome = req.getParameter("nome");
                if (nome == null || nome.isBlank()) {
                    mensagem = "Informe o nome da categoria.";
                } else if (categoriaDAO.existePorNome(nome)) {
                    mensagem = "Já existe uma categoria cadastrada com esse nome.";
                } else {
                    categoriaDAO.inserir(nome);
                    mensagem = "Categoria cadastrada com sucesso!";
                }
            } else if ("alternar_status".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean novoStatus = Boolean.parseBoolean(req.getParameter("novoStatus"));
                categoriaDAO.atualizarStatus(id, novoStatus);
                mensagem = novoStatus ? "Categoria ativada." : "Categoria desativada.";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "ID de categoria inválido.";
        } catch (SQLException e) {
            mensagem = "Erro ao salvar categoria: " + e.getMessage();
        }

        sessao.setAttribute("mensagem", mensagem);
        resp.sendRedirect(req.getContextPath() + "/categorias");
    }
}
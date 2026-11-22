package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.SetorDAO;
import br.com.gestordeprazo.model.Setor;
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

@WebServlet("/setores")
public class SetorServlet extends HttpServlet {

    private final SetorDAO setorDAO = new SetorDAO();

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
            List<Setor> setores = setorDAO.listarTodos();
            req.setAttribute("setores", setores);
        } catch (SQLException e) {
            req.setAttribute("setores", Collections.emptyList());
            req.setAttribute("mensagem", "Erro ao carregar setores: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/setores.jsp").forward(req, resp);
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
                    mensagem = "Informe o nome do setor.";
                } else if (setorDAO.existePorNome(nome)) {
                    mensagem = "Já existe um setor cadastrado com esse nome.";
                } else {
                    setorDAO.inserir(nome);
                    mensagem = "Setor cadastrado com sucesso!";
                }
            } else if ("alternar_status".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean novoStatus = Boolean.parseBoolean(req.getParameter("novoStatus"));
                setorDAO.atualizarStatus(id, novoStatus);
                mensagem = novoStatus ? "Setor ativado." : "Setor desativado.";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "ID de setor inválido.";
        } catch (SQLException e) {
            mensagem = "Erro ao salvar setor: " + e.getMessage();
        }

        sessao.setAttribute("mensagem", mensagem);
        resp.sendRedirect(req.getContextPath() + "/setores");
    }
}
package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.UsuarioDAO;
import br.com.gestordeprazo.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            req.setAttribute("erro", "Preencha e-mail e senha.");
            req.getRequestDispatcher("/login.html").forward(req, resp);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(email.trim(), senha);
            if (usuario != null) {
                HttpSession sessao = req.getSession(true);
                sessao.setAttribute("usuarioLogado", usuario);
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                req.setAttribute("erro", "E-mail ou senha inválidos.");
                req.getRequestDispatcher("/login.html").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Erro ao autenticar usuário no banco de dados.", e);
        }
    }
}
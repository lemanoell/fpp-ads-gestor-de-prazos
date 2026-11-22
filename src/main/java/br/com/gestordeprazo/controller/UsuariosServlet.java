package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.UsuarioDAO;
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
import java.util.List;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuarioLogado = (sessao != null) ? (Usuario) sessao.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        if (usuarioLogado.getPerfil() != Perfil.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso permitido apenas para perfil ADMIN.");
            return;
        }

        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            req.setAttribute("usuarios", usuarios);
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Erro ao consultar a lista de usuários.", e);
        }
    }
}
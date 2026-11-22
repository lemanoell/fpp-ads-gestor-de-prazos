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

@WebServlet("/cadastro-usuario")
public class CadastroUsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!autorizarAdmin(req, resp)) {
            return;
        }
        carregarPagina(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!autorizarAdmin(req, resp)) {
            return;
        }

        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        if (acao == null || acao.isBlank() || "cadastrar".equals(acao)) {
            processarCadastro(req, resp);
            return;
        }

        HttpSession sessao = req.getSession(false);
        Usuario usuarioLogado = (sessao != null) ? (Usuario) sessao.getAttribute("usuarioLogado") : null;

        String idParam = req.getParameter("id");
        int idAlvo;
        try {
            idAlvo = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            carregarPagina(req, resp, "ID de usuário inválido.");
            return;
        }

        try {
            if ("alternar_status".equals(acao)) {
                if (usuarioLogado != null && usuarioLogado.getId() == idAlvo) {
                    carregarPagina(req, resp, "Você não pode desativar o próprio usuário logado.");
                    return;
                }

                boolean novoStatus = Boolean.parseBoolean(req.getParameter("novoStatus"));
                usuarioDAO.atualizarStatus(idAlvo, novoStatus);
                carregarPagina(req, resp, "Status do usuário atualizado para " + (novoStatus ? "Ativo" : "Inativo") + ".");
                return;
            }

            if ("alterar_perfil".equals(acao)) {
                String perfilParam = req.getParameter("perfil");
                Perfil novoPerfil;
                try {
                    novoPerfil = Perfil.valueOf(perfilParam.trim().toUpperCase());
                } catch (Exception e) {
                    carregarPagina(req, resp, "Perfil inválido selecionado.");
                    return;
                }

                if (usuarioLogado != null && usuarioLogado.getId() == idAlvo && novoPerfil != Perfil.ADMIN) {
                    carregarPagina(req, resp, "Você não pode remover o perfil ADMIN da sua própria sessão ativa.");
                    return;
                }

                usuarioDAO.atualizarPerfil(idAlvo, novoPerfil);
                carregarPagina(req, resp, "Perfil do usuário atualizado para " + novoPerfil.name() + ".");
                return;
            }

            carregarPagina(req, resp, "Ação desconhecida.");
        } catch (SQLException e) {
            throw new ServletException("Erro ao atualizar usuário no banco de dados.", e);
        }
    }

    private void processarCadastro(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");
        String perfilParam = req.getParameter("perfil");

        if (nome == null || nome.isBlank()
                || email == null || email.isBlank()
                || senha == null || senha.isBlank()
                || perfilParam == null || perfilParam.isBlank()) {
            carregarPagina(req, resp, "Preencha todos os campos obrigatórios.");
            return;
        }

        Perfil perfil;
        try {
            perfil = Perfil.valueOf(perfilParam.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            carregarPagina(req, resp, "Perfil inválido.");
            return;
        }

        String emailNormalizado = email.trim().toLowerCase();

        try {
            if (usuarioDAO.emailExiste(emailNormalizado)) {
                carregarPagina(req, resp, "Já existe um usuário cadastrado com este e-mail.");
                return;
            }

            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome.trim());
            novoUsuario.setEmail(emailNormalizado);
            novoUsuario.setSenha(senha);
            novoUsuario.setPerfil(perfil);
            novoUsuario.setAtivo(true);

            usuarioDAO.inserir(novoUsuario);
            carregarPagina(req, resp, "Usuário cadastrado com sucesso (" + perfil.name() + ").");
        } catch (SQLException e) {
            throw new ServletException("Erro ao cadastrar usuário no banco de dados.", e);
        }
    }

    private void carregarPagina(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            req.setAttribute("usuarios", usuarios);
            if (mensagem != null) {
                req.setAttribute("mensagem", mensagem);
            }
            req.getRequestDispatcher("/WEB-INF/jsp/cadastro-usuario.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Erro ao listar usuários.", e);
        }
    }

    private boolean autorizarAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuarioLogado = (sessao != null) ? (Usuario) sessao.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return false;
        }

        if (usuarioLogado.getPerfil() != Perfil.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso permitido apenas para perfil ADMIN.");
            return false;
        }

        return true;
    }
}
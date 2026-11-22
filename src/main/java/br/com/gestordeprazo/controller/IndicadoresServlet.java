package br.com.gestordeprazo.controller;

import br.com.gestordeprazo.dao.DocumentoDAO;
import br.com.gestordeprazo.model.Documento;
import br.com.gestordeprazo.model.Documento.SituacaoPrazo;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/indicadores")
public class IndicadoresServlet extends HttpServlet {

    private final DocumentoDAO documentoDAO = new DocumentoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuario = (sessao != null) ? (Usuario) sessao.getAttribute("usuarioLogado") : null;

        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        if (usuario.getPerfil() == Perfil.USUARIO) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        Map<SituacaoPrazo, Integer> porSituacao = new EnumMap<>(SituacaoPrazo.class);
        for (SituacaoPrazo sit : SituacaoPrazo.values()) {
            porSituacao.put(sit, 0);
        }

        Map<String, Integer> porCategoria = new LinkedHashMap<>();
        Map<String, Integer> porSetor = new LinkedHashMap<>();
        int totalDocumentos = 0;

        try {
            List<Documento> docs = documentoDAO.listarTodosComDetalhes();
            totalDocumentos = docs.size();

            for (Documento d : docs) {
                SituacaoPrazo sit = d.getSituacaoPrazo();
                porSituacao.put(sit, porSituacao.getOrDefault(sit, 0) + 1);

                String catNome = (d.getCategoriaNome() != null && !d.getCategoriaNome().isBlank())
                        ? d.getCategoriaNome()
                        : "Sem categoria";
                porCategoria.put(catNome, porCategoria.getOrDefault(catNome, 0) + 1);

                String setorNome = (d.getSetorNome() != null && !d.getSetorNome().isBlank())
                        ? d.getSetorNome()
                        : "Sem setor";
                porSetor.put(setorNome, porSetor.getOrDefault(setorNome, 0) + 1);
            }
        } catch (SQLException e) {
            req.setAttribute("mensagem", "Erro ao carregar indicadores: " + e.getMessage());
        }

        req.setAttribute("totalDocumentos", totalDocumentos);
        req.setAttribute("porSituacao", porSituacao);
        req.setAttribute("porCategoria", porCategoria.isEmpty() ? Collections.emptyMap() : porCategoria);
        req.setAttribute("porSetor", porSetor.isEmpty() ? Collections.emptyMap() : porSetor);

        req.getRequestDispatcher("/WEB-INF/jsp/indicadores.jsp").forward(req, resp);
    }
}
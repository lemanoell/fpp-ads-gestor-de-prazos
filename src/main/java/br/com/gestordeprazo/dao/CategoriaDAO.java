package br.com.gestordeprazo.dao;

import br.com.gestordeprazo.model.Categoria;
import br.com.gestordeprazo.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private Connection getConnection() throws SQLException {
        return Conexao.getConnection();
    }

    public List<Categoria> listarTodas() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nome, ativo FROM categorias ORDER BY nome ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setAtivo(rs.getBoolean("ativo"));
                lista.add(c);
            }
        }
        return lista;
    }

    public boolean existePorNome(String nome) throws SQLException {
        String sql = "SELECT 1 FROM categorias WHERE LOWER(nome) = LOWER(?) LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void inserir(String nome) throws SQLException {
        String sql = "INSERT INTO categorias (nome, ativo) VALUES (?, TRUE)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(int id, boolean ativo) throws SQLException {
        String sql = "UPDATE categorias SET ativo = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
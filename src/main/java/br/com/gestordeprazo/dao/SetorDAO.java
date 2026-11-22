package br.com.gestordeprazo.dao;

import br.com.gestordeprazo.model.Setor;
import br.com.gestordeprazo.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SetorDAO {

    private Connection getConnection() throws SQLException {
        return Conexao.getConnection();
    }

    public List<Setor> listarTodos() throws SQLException {
        List<Setor> lista = new ArrayList<>();
        String sql = "SELECT id, nome, ativo FROM setores ORDER BY nome ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Setor s = new Setor();
                s.setId(rs.getInt("id"));
                s.setNome(rs.getString("nome"));
                s.setAtivo(rs.getBoolean("ativo"));
                lista.add(s);
            }
        }
        return lista;
    }

    public boolean existePorNome(String nome) throws SQLException {
        String sql = "SELECT 1 FROM setores WHERE LOWER(nome) = LOWER(?) LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void inserir(String nome) throws SQLException {
        String sql = "INSERT INTO setores (nome, ativo) VALUES (?, TRUE)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(int id, boolean ativo) throws SQLException {
        String sql = "UPDATE setores SET ativo = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
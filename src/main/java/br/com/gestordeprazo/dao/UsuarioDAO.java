package br.com.gestordeprazo.dao;
import java.util.ArrayList;
import java.util.List;

import br.com.gestordeprazo.model.Usuario;
import br.com.gestordeprazo.model.Usuario.Perfil;
import br.com.gestordeprazo.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT id, nome, email, senha, perfil, ativo FROM usuarios WHERE email = ? AND senha = ? AND ativo = TRUE";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setPerfil(Perfil.valueOf(rs.getString("perfil")));
                    usuario.setAtivo(rs.getBoolean("ativo"));
                    return usuario;
                }
            }
        }
        return null;
    }

    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, perfil, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil().name());
            stmt.setBoolean(5, usuario.isAtivo());
            stmt.executeUpdate();
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, email, perfil, ativo FROM usuarios ORDER BY nome ASC";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setPerfil(Usuario.Perfil.valueOf(rs.getString("perfil").trim().toUpperCase()));
                u.setAtivo(rs.getBoolean("ativo"));
                usuarios.add(u);
            }
        }

        return usuarios;
    }

    public void atualizarStatus(int id, boolean ativo) throws SQLException {
        String sql = "UPDATE usuarios SET ativo = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarPerfil(int id, Usuario.Perfil perfil) throws SQLException {
        String sql = "UPDATE usuarios SET perfil = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, perfil.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

}
package br.com.gestordeprazo.dao;

import br.com.gestordeprazo.model.Documento;
import br.com.gestordeprazo.model.Documento.StatusRegistro;
import br.com.gestordeprazo.model.Documento.TipoPessoa;
import br.com.gestordeprazo.util.Conexao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    private Connection getConnection() throws SQLException {
        return Conexao.getConnection();
    }

    public Documento buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT d.id, d.nome, d.setor_id, s.nome AS setor_nome,
                       d.categoria_id, c.nome AS categoria_nome,
                       d.tipo_pessoa, d.responsavel_beneficiario,
                       d.data_vencimento, d.observacoes, d.status_registro, d.usuario_id
                FROM documentos d
                INNER JOIN setores s ON s.id = d.setor_id
                INNER JOIN categorias c ON c.id = d.categoria_id
                WHERE d.id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Documento doc = new Documento();
                    doc.setId(rs.getInt("id"));
                    doc.setNome(rs.getString("nome"));
                    doc.setSetorId(rs.getInt("setor_id"));
                    doc.setSetorNome(rs.getString("setor_nome"));
                    doc.setCategoriaId(rs.getInt("categoria_id"));
                    doc.setCategoriaNome(rs.getString("categoria_nome"));

                    String tipoStr = rs.getString("tipo_pessoa");
                    if (tipoStr != null) {
                        doc.setTipoPessoa(TipoPessoa.valueOf(tipoStr));
                    }

                    doc.setResponsavelBeneficiario(rs.getString("responsavel_beneficiario"));

                    Date dataVenc = rs.getDate("data_vencimento");
                    if (dataVenc != null) {
                        doc.setDataVencimento(dataVenc.toLocalDate());
                    }

                    doc.setObservacoes(rs.getString("observacoes"));

                    String statusStr = rs.getString("status_registro");
                    if (statusStr != null) {
                        doc.setStatusRegistro(StatusRegistro.valueOf(statusStr));
                    }

                    int uid = rs.getInt("usuario_id");
                    doc.setUsuarioId(rs.wasNull() ? null : uid);
                    return doc;
                }
            }
        }
        return null;
    }

    public List<Documento> listarTodosComDetalhes() throws SQLException {
        List<Documento> lista = new ArrayList<>();
        String sql = """
                SELECT d.id, d.nome, d.setor_id, s.nome AS setor_nome,
                       d.categoria_id, c.nome AS categoria_nome,
                       d.tipo_pessoa, d.responsavel_beneficiario,
                       d.data_vencimento, d.observacoes, d.status_registro, d.usuario_id
                FROM documentos d
                INNER JOIN setores s ON s.id = d.setor_id
                INNER JOIN categorias c ON c.id = d.categoria_id
                ORDER BY d.data_vencimento ASC, d.id ASC
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Documento doc = new Documento();
                doc.setId(rs.getInt("id"));
                doc.setNome(rs.getString("nome"));
                doc.setSetorId(rs.getInt("setor_id"));
                doc.setSetorNome(rs.getString("setor_nome"));
                doc.setCategoriaId(rs.getInt("categoria_id"));
                doc.setCategoriaNome(rs.getString("categoria_nome"));

                String tipoStr = rs.getString("tipo_pessoa");
                if (tipoStr != null) {
                    doc.setTipoPessoa(TipoPessoa.valueOf(tipoStr));
                }

                doc.setResponsavelBeneficiario(rs.getString("responsavel_beneficiario"));

                Date dataVenc = rs.getDate("data_vencimento");
                if (dataVenc != null) {
                    doc.setDataVencimento(dataVenc.toLocalDate());
                }

                doc.setObservacoes(rs.getString("observacoes"));

                String statusStr = rs.getString("status_registro");
                if (statusStr != null) {
                    doc.setStatusRegistro(StatusRegistro.valueOf(statusStr));
                }

                int uid = rs.getInt("usuario_id");
                doc.setUsuarioId(rs.wasNull() ? null : uid);
                lista.add(doc);
            }
        }
        return lista;
    }

    public void inserir(Documento doc) throws SQLException {
        String statusNome = (doc.getStatusRegistro() != null)
                ? doc.getStatusRegistro().name()
                : StatusRegistro.PENDENTE.name();

        String sql = """
                INSERT INTO documentos (
                    nome, setor_id, categoria_id, tipo_pessoa,
                    responsavel_beneficiario, data_vencimento, observacoes, status_registro, usuario_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doc.getNome().trim());
            stmt.setInt(2, doc.getSetorId());
            stmt.setInt(3, doc.getCategoriaId());
            stmt.setString(4, doc.getTipoPessoa().name());
            stmt.setString(5, doc.getResponsavelBeneficiario().trim());
            stmt.setDate(6, Date.valueOf(doc.getDataVencimento()));
            if (doc.getObservacoes() == null || doc.getObservacoes().isBlank()) {
                stmt.setNull(7, Types.LONGVARCHAR);
            } else {
                stmt.setString(7, doc.getObservacoes().trim());
            }
            stmt.setString(8, statusNome);
            if (doc.getUsuarioId() == null) {
                stmt.setNull(9, Types.INTEGER);
            } else {
                stmt.setInt(9, doc.getUsuarioId());
            }
            stmt.executeUpdate();
        }
    }

    public void atualizar(Documento doc) throws SQLException {
        String sql = """
                UPDATE documentos
                SET nome = ?,
                    setor_id = ?,
                    categoria_id = ?,
                    tipo_pessoa = ?,
                    responsavel_beneficiario = ?,
                    data_vencimento = ?,
                    observacoes = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doc.getNome().trim());
            stmt.setInt(2, doc.getSetorId());
            stmt.setInt(3, doc.getCategoriaId());
            stmt.setString(4, doc.getTipoPessoa().name());
            stmt.setString(5, doc.getResponsavelBeneficiario().trim());
            stmt.setDate(6, Date.valueOf(doc.getDataVencimento()));
            if (doc.getObservacoes() == null || doc.getObservacoes().isBlank()) {
                stmt.setNull(7, Types.LONGVARCHAR);
            } else {
                stmt.setString(7, doc.getObservacoes().trim());
            }
            stmt.setInt(8, doc.getId());
            stmt.executeUpdate();
        }
    }

    public void atualizarStatusRegistro(int id, StatusRegistro novoStatus) throws SQLException {
        String sql = "UPDATE documentos SET status_registro = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void duplicarComNovaData(int idOrigem, LocalDate novaDataVencimento, Integer usuarioId) throws SQLException {
        Documento origem = buscarPorId(idOrigem);
        if (origem == null) {
            throw new SQLException("Documento de origem não encontrado.");
        }

        Documento copia = new Documento();
        copia.setNome(origem.getNome());
        copia.setSetorId(origem.getSetorId());
        copia.setCategoriaId(origem.getCategoriaId());
        copia.setTipoPessoa(origem.getTipoPessoa());
        copia.setResponsavelBeneficiario(origem.getResponsavelBeneficiario());
        copia.setDataVencimento(novaDataVencimento);
        copia.setObservacoes(origem.getObservacoes());
        copia.setStatusRegistro(StatusRegistro.PENDENTE);
        copia.setUsuarioId(usuarioId);

        inserir(copia);
    }
}
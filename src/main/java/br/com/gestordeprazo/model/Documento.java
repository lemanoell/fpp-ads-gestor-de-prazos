package br.com.gestordeprazo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Documento {

    public enum TipoPessoa {
        PESSOA_FISICA("Pessoa Física"),
        PESSOA_JURIDICA("Pessoa Jurídica"),
        OUTRO("Outro");

        private final String rotulo;

        TipoPessoa(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    private int id;
    private String nome;
    private int setorId;
    private String setorNome;
    private int categoriaId;
    private String categoriaNome;
    private TipoPessoa tipoPessoa;
    private String responsavelBeneficiario;
    private LocalDate dataVencimento;
    private String observacoes;
    private boolean concluido;
    private Integer usuarioId;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getSetorId() {
        return setorId;
    }

    public void setSetorId(int setorId) {
        this.setorId = setorId;
    }

    public String getSetorNome() {
        return setorNome;
    }

    public void setSetorNome(String setorNome) {
        this.setorNome = setorNome;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getResponsavelBeneficiario() {
        return responsavelBeneficiario;
    }

    public void setResponsavelBeneficiario(String responsavelBeneficiario) {
        this.responsavelBeneficiario = responsavelBeneficiario;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getDataVencimentoIso() {
        return dataVencimento != null ? dataVencimento.toString() : "";
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public enum StatusRegistro {
        PENDENTE,
        CONCLUIDO,
        CANCELADO
    }

    public enum SituacaoPrazo {
        EM_DIA("Em dia"),
        PROXIMO_VENCIMENTO("Próximo do vencimento"),
        VENCIDO("Vencido"),
        CONCLUIDO("Concluído"),
        CANCELADO("Cancelado");

        private final String rotulo;

        SituacaoPrazo(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    private StatusRegistro statusRegistro = StatusRegistro.PENDENTE;

    public StatusRegistro getStatusRegistro() {
        return statusRegistro;
    }

    public void setStatusRegistro(StatusRegistro statusRegistro) {
        this.statusRegistro = statusRegistro;
    }

    public SituacaoPrazo getSituacaoPrazo() {
        if (statusRegistro == StatusRegistro.CONCLUIDO) {
            return SituacaoPrazo.CONCLUIDO;
        }
        if (statusRegistro == StatusRegistro.CANCELADO) {
            return SituacaoPrazo.CANCELADO;
        }
        if (dataVencimento == null) {
            return SituacaoPrazo.EM_DIA;
        }
        LocalDate hoje = LocalDate.now();
        if (dataVencimento.isBefore(hoje)) {
            return SituacaoPrazo.VENCIDO;
        }
        if (!dataVencimento.isAfter(hoje.plusDays(7))) {
            return SituacaoPrazo.PROXIMO_VENCIMENTO;
        }
        return SituacaoPrazo.EM_DIA;
    }
}
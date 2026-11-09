package com.antifraude.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A conta de origem é obrigatória")
    @Column(nullable = false)
    private String contaOrigem;

    @NotBlank(message = "A conta de destino é obrigatória")
    @Column(nullable = false)
    private String contaDestino;

    @NotNull(message = "O valor da transação é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior do que zero")
    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String status; // Ex.: "APROVADA", "REJEITADA", "EM_ANALISE"

    private String motivo; // Justificativa em caso de rejeição ou suspeita

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Transacao() {
    }

    public Transacao(String contaOrigem, String contaDestino, BigDecimal valor, String status, String motivo) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.status = status;
        this.motivo = motivo;
        this.dataHora = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(String contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(String contaDestino) {
        this.contaDestino = contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
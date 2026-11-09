package com.antifraude.servico;

import com.antifraude.modelo.Transacao;
import com.antifraude.repositorio.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public Transacao analisarTransacao(Transacao transacao) {
        LocalDateTime agora = LocalDateTime.now();
        transacao.setDataHora(agora);


        long transacoesRecentes = transacaoRepository.countByContaOrigemAndDataHoraAfter(
                transacao.getContaOrigem(), agora.minusMinutes(2)
        );

        if (transacoesRecentes >= 3) {
            transacao.setStatus("REJEITADA");
            transacao.setMotivo("Bloqueio por frequência excessiva: mais de 3 transações em menos de 2 minutos");
            return transacaoRepository.save(transacao);
        }


        LocalTime horaAtual = agora.toLocalTime();
        boolean horarioNoturno = horaAtual.isAfter(LocalTime.of(20, 0)) || horaAtual.isBefore(LocalTime.of(6, 0));

        if (horarioNoturno && transacao.getValor().compareTo(new BigDecimal("1000.00")) > 0) {
            transacao.setStatus("REJEITADA");
            transacao.setMotivo("Transação excede o limite noturno permitido de R$ 1.000,00 (20h às 06h)");
            return transacaoRepository.save(transacao);
        }


        if (transacao.getValor().compareTo(new BigDecimal("10000.00")) > 0) {
            transacao.setStatus("EM_ANALISE");
            transacao.setMotivo("Valor acima de R$ 10.000,00 encaminhado para análise manual de risco");
            return transacaoRepository.save(transacao);
        }


        transacao.setStatus("APROVADA");
        transacao.setMotivo("Transação analisada e aprovada sem ocorrências de risco");
        return transacaoRepository.save(transacao);
    }

    public List<Transacao> listarTodas() {
        return transacaoRepository.findAll();
    }

    public List<Transacao> listarPorConta(String conta) {
        return transacaoRepository.findByContaOrigem(conta);
    }
}
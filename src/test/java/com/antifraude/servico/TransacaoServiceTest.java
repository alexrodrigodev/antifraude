package com.antifraude.servico;

import com.antifraude.modelo.Transacao;
import com.antifraude.repositorio.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Transacao transacaoPadrao;

    @BeforeEach
    void setup() {
        transacaoPadrao = new Transacao();
        transacaoPadrao.setContaOrigem("1001");
        transacaoPadrao.setContaDestino("2002");
        transacaoPadrao.setValor(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve aprovar transação padrão que respeita todas as regras em horário diurno")
    void deveAprovarTransacaoPadrao() {
        LocalDateTime horarioDiurno = LocalDateTime.of(2026, 11, 7, 14, 0);

        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(LocalDateTime::now).thenReturn(horarioDiurno);

            when(transacaoRepository.countByContaOrigemAndDataHoraAfter(eq("1001"), any(LocalDateTime.class)))
                    .thenReturn(0L);
            when(transacaoRepository.save(any(Transacao.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Transacao resultado = transacaoService.analisarTransacao(transacaoPadrao);

            assertEquals("APROVADA", resultado.getStatus());
            assertEquals("Transação analisada e aprovada sem ocorrências de risco", resultado.getMotivo());
            verify(transacaoRepository, times(1)).save(transacaoPadrao);
        }
    }

    @Test
    @DisplayName("Deve rejeitar transação por Velocity Check quando houver 3 ou mais operações recentes")
    void deveRejeitarPorVelocityCheck() {
        when(transacaoRepository.countByContaOrigemAndDataHoraAfter(eq("1001"), any(LocalDateTime.class)))
                .thenReturn(3L);
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao resultado = transacaoService.analisarTransacao(transacaoPadrao);

        assertEquals("REJEITADA", resultado.getStatus());
        assertEquals("Bloqueio por frequência excessiva: mais de 3 transações em menos de 2 minutos", resultado.getMotivo());
        verify(transacaoRepository, times(1)).save(transacaoPadrao);
    }

    @Test
    @DisplayName("Deve rejeitar transação noturna que excede R$ 1.000,00")
    void deveRejeitarTransacaoNoturnaExcedente() {
        LocalDateTime horarioNoturno = LocalDateTime.of(2026, 11, 7, 22, 30);

        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(LocalDateTime::now).thenReturn(horarioNoturno);

            transacaoPadrao.setValor(new BigDecimal("1200.00"));

            when(transacaoRepository.countByContaOrigemAndDataHoraAfter(eq("1001"), any(LocalDateTime.class)))
                    .thenReturn(0L);
            when(transacaoRepository.save(any(Transacao.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Transacao resultado = transacaoService.analisarTransacao(transacaoPadrao);

            assertEquals("REJEITADA", resultado.getStatus());
            assertEquals("Transação excede o limite noturno permitido de R$ 1.000,00 (20h às 06h)", resultado.getMotivo());
            verify(transacaoRepository, times(1)).save(transacaoPadrao);
        }
    }

    @Test
    @DisplayName("Deve encaminhar para análise manual transação diurna com valor superior a R$ 10.000,00")
    void deveEncaminharParaAnaliseManualValorAlto() {
        LocalDateTime horarioDiurno = LocalDateTime.of(2026, 11, 7, 14, 0);

        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(LocalDateTime::now).thenReturn(horarioDiurno);

            transacaoPadrao.setValor(new BigDecimal("15000.00"));

            when(transacaoRepository.countByContaOrigemAndDataHoraAfter(eq("1001"), any(LocalDateTime.class)))
                    .thenReturn(0L);
            when(transacaoRepository.save(any(Transacao.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Transacao resultado = transacaoService.analisarTransacao(transacaoPadrao);

            assertEquals("EM_ANALISE", resultado.getStatus());
            assertEquals("Valor acima de R$ 10.000,00 encaminhado para análise manual de risco", resultado.getMotivo());
            verify(transacaoRepository, times(1)).save(transacaoPadrao);
        }
    }

    @Test
    @DisplayName("Deve listar todas as transações")
    void deveListarTodas() {
        when(transacaoRepository.findAll()).thenReturn(List.of(transacaoPadrao));

        List<Transacao> resultado = transacaoService.listarTodas();

        assertEquals(1, resultado.size());
        verify(transacaoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar transações filtrando por conta de origem")
    void deveListarPorConta() {
        when(transacaoRepository.findByContaOrigem("1001")).thenReturn(List.of(transacaoPadrao));

        List<Transacao> resultado = transacaoService.listarPorConta("1001");

        assertEquals(1, resultado.size());
        verify(transacaoRepository, times(1)).findByContaOrigem("1001");
    }
}
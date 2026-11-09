package com.antifraude.repositorio;

import com.antifraude.modelo.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {


    List<Transacao> findByContaOrigem(String contaOrigem);


    long countByContaOrigemAndDataHoraAfter(String contaOrigem, LocalDateTime dataHora);
}
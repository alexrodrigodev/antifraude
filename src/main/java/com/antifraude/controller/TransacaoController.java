package com.antifraude.controller;

import com.antifraude.modelo.Transacao;
import com.antifraude.servico.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }


    @PostMapping
    public ResponseEntity<Transacao> criarTransacao(@Valid @RequestBody Transacao transacao) {
        Transacao transacaoAnalisada = transacaoService.analisarTransacao(transacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoAnalisada);
    }


    @GetMapping
    public ResponseEntity<List<Transacao>> listarTodas() {
        List<Transacao> transacoes = transacaoService.listarTodas();
        return ResponseEntity.ok(transacoes);
    }


    @GetMapping("/conta/{conta}")
    public ResponseEntity<List<Transacao>> listarPorConta(@PathVariable String conta) {
        List<Transacao> transacoes = transacaoService.listarPorConta(conta);
        return ResponseEntity.ok(transacoes);
    }
}
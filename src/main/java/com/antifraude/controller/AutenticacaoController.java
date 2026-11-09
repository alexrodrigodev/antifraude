package com.antifraude.controller;

import com.antifraude.dto.DadosAutenticacao;
import com.antifraude.dto.DadosRegistro;
import com.antifraude.dto.DadosTokenJWT;
import com.antifraude.modelo.Usuario;
import com.antifraude.repositorio.UsuarioRepository;
import com.antifraude.servico.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoController(AuthenticationManager authenticationManager,
                                  UsuarioRepository usuarioRepository,
                                  TokenService tokenService,
                                  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@Valid @RequestBody DadosRegistro dados) {
        if (usuarioRepository.findByLogin(dados.login()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login já cadastrado");
        }


        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        String role = (dados.role() == null || dados.role().isBlank()) ? "USER" : dados.role().toUpperCase();

        Usuario novoUsuario = new Usuario(dados.login(), senhaCriptografada, role);
        usuarioRepository.save(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Utilizador registado com sucesso");
    }


    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> login(@Valid @RequestBody DadosAutenticacao dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = authenticationManager.authenticate(authToken);

        var usuario = (Usuario) authentication.getPrincipal();
        String tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}
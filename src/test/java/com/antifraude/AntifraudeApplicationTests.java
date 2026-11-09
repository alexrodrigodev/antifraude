package com.antifraude;

import com.antifraude.controller.AutenticacaoController;
import com.antifraude.controller.TransacaoController;
import com.antifraude.dto.DadosAutenticacao;
import com.antifraude.modelo.Usuario;
import com.antifraude.repositorio.UsuarioRepository;
import com.antifraude.seguranca.TratadorDeErros;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AntifraudeApplicationTests {

	@Autowired
	private TransacaoController transacaoController;

	@Autowired
	private AutenticacaoController autenticacaoController;

	@Autowired
	private TratadorDeErros tratadorDeErros;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Instanciação direta sem depender de injeção de dependências do Spring
	private final ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvcTransacoes;
	private MockMvc mockMvcAuth;

	@BeforeEach
	void setup() {
		this.mockMvcTransacoes = MockMvcBuilders.standaloneSetup(transacaoController)
				.setControllerAdvice(tratadorDeErros)
				.build();

		this.mockMvcAuth = MockMvcBuilders.standaloneSetup(autenticacaoController)
				.setControllerAdvice(tratadorDeErros)
				.build();

		if (usuarioRepository.findByLogin("admin") == null) {
			usuarioRepository.save(new Usuario("admin", passwordEncoder.encode("123"), "ADMIN"));
		}
	}

	@Test
	@DisplayName("Deve carregar o contexto da aplicação com sucesso")
	void contextLoads() {
	}

	@Test
	@DisplayName("Deve listar transações com sucesso via endpoint GET")
	void deveListarTransacoes() throws Exception {
		mockMvcTransacoes.perform(get("/transacoes"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Deve retornar 400 Bad Request ao submeter transação com dados inválidos")
	void deveRetornar400AoEnviarTransacaoInvalida() throws Exception {
		String transacaoInvalidaJson = """
                {
                    "contaOrigem": "1001",
                    "contaDestino": "",
                    "valor": -50.00
                }
                """;

		mockMvcTransacoes.perform(post("/transacoes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(transacaoInvalidaJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@DisplayName("Deve autenticar com sucesso no endpoint de login e obter token JWT")
	void deveAutenticarComSucesso() throws Exception {
		DadosAutenticacao dadosLogin = new DadosAutenticacao("admin", "123");

		mockMvcAuth.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dadosLogin)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
}
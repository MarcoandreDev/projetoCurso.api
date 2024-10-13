package com.marco.projetocurso.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.marco.projetocurso.exception.ErroAutenticacao;
import com.marco.projetocurso.exception.RegraNegocioException;
import com.marco.projetocurso.model.entity.Usuario;
import com.marco.projetocurso.model.repository.UsuarioRepository;
import com.marco.projetocurso.service.impl.UsuarioServiceImpl;

@SpringBootTest
public class UsuarioServiceTest {
	
    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @BeforeEach
    public void setUp() {
        // Sem redefinir o service manualmente, pois o @SpyBean já cria a instância
    }
    
    @Test
    public void deveSalvarUmUsuario() {
        // CENARIO
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        
        // ACAO
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
        
        // VERIFICACAO
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }
    
    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
    	String email = "email@email.com";
    	Usuario usuario = Usuario.builder().email(email).build();
    	Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
    	
    	//acao
    	assertThrows(
    	        RegraNegocioException.class,
    	        () -> service.salvarUsuario(usuario)
    	    );
    	
    	//verificacao
    	Mockito.verify(repository, Mockito.never()).save(usuario);
    	
    }
    
    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        // Cenario
        String email = "email@email.com";
        String senha = "senha";
        
        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        
        Usuario result = service.autenticar(email, senha);
        
        Assertions.assertThat(result).isNotNull();
    }
    
    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
        // Cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        
        // Acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
                  .hasMessage("usuario nao encontrado para o email informado");
    }

    @Test 
    public void deveValidarEmail() {
        // Cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        
        // Acao
        service.validarEmail("email@email.com");
    }
    
    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {
        // Cenario
        String senha = "Senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
        
        // Acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "234"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
                  .hasMessage("Senha Invalida");
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        // Cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        
        // Acao e verificação
        RegraNegocioException exception = assertThrows(
            RegraNegocioException.class,
            () -> service.validarEmail("email@email.com")
        );

        // Verifica se a mensagem da exceção corresponde à esperada
        assertEquals("JA EXISTE EMAIL", exception.getMessage());
    }
}

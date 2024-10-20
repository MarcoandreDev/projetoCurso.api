package com.marco.projetocurso.service;



import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.marco.projetocurso.exception.RegraNegocioException;
import com.marco.projetocurso.model.entity.Lancamento;
import com.marco.projetocurso.model.entity.Usuario;
import com.marco.projetocurso.model.enums.StatusLancamento;
import com.marco.projetocurso.model.repository.LancamentoRepository;
import com.marco.projetocurso.model.repository.LancamentoRepositoryTest;
import com.marco.projetocurso.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository,Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTestarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository,Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		service.deletar(lancamento);
		
		Mockito.verify(repository).delete(lancamento);
	}
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1L); // Use '1L' em vez de '1l'
	    
	    List<Lancamento> lista = Collections.singletonList(lancamento);
	    Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
	    
	    List<Lancamento> resultado = service.buscar(lancamento);
	    Assertions
	        .assertThat(resultado)
	        .isNotEmpty()
	        .hasSize(1)
	        .contains(lancamento); // Verifique se o objeto retornado é o mesmo
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1l);
	    lancamento.setStatus(StatusLancamento.PENDENTE);
	    
	    StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
	    Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
	    
	    service.atualizarStatus(lancamento, novoStatus);
	    
	    Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
	    Mockito.verify(service).atualizar(lancamento);

	}
	
	@Test
	public void meuTesteDeveObterLancamentoPorId() {
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1l);
	    Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
	    
	    Optional<Lancamento> resultado = service.obterPorId(id);
	    
	    Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1l);
	    Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
	    
	    Optional<Lancamento> resultado = service.obterPorId(id);
	    
	    Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descricao valida");
		lancamento.setDescricao(" ");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descricao valida");
		lancamento.setDescricao("salario");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mes valido");
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mes valido");
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mes valido");
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido");
		lancamento.setAno(201);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido");
		lancamento.setAno(2018);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario Valido");
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario Valido");
		lancamento.getUsuario().setId(1l);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor valido");
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor valido");
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lancamento");
		
	}

}

package com.marco.projetocurso.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marco.projetocurso.api.dto.AtualizaStatusDTO;
import com.marco.projetocurso.api.dto.LancamentoDTO;
import com.marco.projetocurso.exception.RegraNegocioException;
import com.marco.projetocurso.model.entity.Lancamento;
import com.marco.projetocurso.model.entity.Usuario;
import com.marco.projetocurso.model.enums.StatusLancamento;
import com.marco.projetocurso.model.enums.TipoLancamento;
import com.marco.projetocurso.service.LancamentoService;
import com.marco.projetocurso.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity<?> buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario
			) {
		Lancamento lancamentoFiltro = new Lancamento();
		if (mes != null) {
	        lancamentoFiltro.setMes(mes);
	    }
	    if (ano != null) {
	        lancamentoFiltro.setAno(ano);
	    }
		lancamentoFiltro.setDescricao(descricao);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Nao foi possivel realizar a consulta. Usuario nao encontrado para o Id informado");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			service.salvar(entidade);
			return new ResponseEntity<>(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Tipo ou Status do lançamento inválido.");
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
	    return service.obterPorId(id).map(entity -> {
	        try {
	            Lancamento lancamento = converter(dto);
	            lancamento.setId(entity.getId());
	            service.atualizar(lancamento);
	            return ResponseEntity.ok(lancamento);
	        } catch (RegraNegocioException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body("Tipo ou Status do lançamento inválido.");
	        }
	    }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lancamento nao encontrado na base de dados"));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
	    return service.obterPorId(id).map(entity -> {
	        try {
	            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
	            entity.setStatus(statusSelecionado);
	            service.atualizar(entity);
	            return ResponseEntity.ok(entity);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body("Nao foi possivel atualizar o status do lancamento, envie um status valido");
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        }
	    }).orElseGet(() -> 
	        ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lancamento nao encontrado na base de dados.")
	    );
	}


	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
			.orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado para o Id informado"));
		lancamento.setUsuario(usuario);

		try {
			if(dto.getTipo() != null) {
				lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
			}
			if(dto.getStatus() != null) {
				lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
			}
			
		} catch (IllegalArgumentException e) {
			throw new RegraNegocioException("Tipo ou Status do lançamento inválido.");
		}
		
		return lancamento;
	}
}

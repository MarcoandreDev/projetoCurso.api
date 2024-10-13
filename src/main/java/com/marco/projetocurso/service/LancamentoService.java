package com.marco.projetocurso.service;

import java.util.List;

import com.marco.projetocurso.model.entity.Lancamento;
import com.marco.projetocurso.model.enums.StatusLancamento;

public interface LancamentoService {
		Lancamento salvar(Lancamento lancamento);
		
		Lancamento atualizar(Lancamento lancamento);
		
		void deletar(Lancamento lancamento);
		
		List<Lancamento> buscar( Lancamento lancamentoFiltro);
		
		void atualizarStatus(Lancamento lancamento, StatusLancamento Status);
		
		void validar (Lancamento lancamento);
}

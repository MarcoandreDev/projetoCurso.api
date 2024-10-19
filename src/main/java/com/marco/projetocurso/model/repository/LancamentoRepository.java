package com.marco.projetocurso.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marco.projetocurso.model.entity.Lancamento;
import com.marco.projetocurso.model.enums.TipoLancamento;


public interface LancamentoRepository extends JpaRepository<Lancamento, Long>  {
	
	@Query("SELECT COALESCE(SUM(l.valor), 0) FROM Lancamento l " +
		       "JOIN l.usuario u " +
		       "WHERE u.id = :idUsuario AND l.tipo = :tipo")
		BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo);

}

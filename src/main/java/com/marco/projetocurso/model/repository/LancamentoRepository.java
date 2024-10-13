package com.marco.projetocurso.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marco.projetocurso.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>  {

}

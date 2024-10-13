package com.marco.projetocurso.exception;

public class ErroAutenticacao extends RuntimeException{
	
	public ErroAutenticacao(String mensagem) {
		super(mensagem);
	}
}

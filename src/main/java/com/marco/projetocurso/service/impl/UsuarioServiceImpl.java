package com.marco.projetocurso.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marco.projetocurso.exception.ErroAutenticacao;
import com.marco.projetocurso.exception.RegraNegocioException;
import com.marco.projetocurso.model.entity.Usuario;
import com.marco.projetocurso.model.repository.UsuarioRepository;
import com.marco.projetocurso.service.UsuarioService;

import jakarta.transaction.Transactional;


@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	
	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("usuario nao encontrado para o email informado");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha Invalida");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("JA EXISTE EMAIL");
		}
	}
	
}

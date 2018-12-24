package br.com.juliomendes90.pontointeligente.api.security.services;

import java.util.Optional;

import br.com.juliomendes90.pontointeligente.api.security.entities.Usuario;

public interface UsuarioService {

	/**
	 * Busca e retornar um usuário dado um email.
	 * 
	 * @param email
	 * @return Optional<Usuario>
	 */
	Optional<Usuario> buscarPorEmail(String email);
}

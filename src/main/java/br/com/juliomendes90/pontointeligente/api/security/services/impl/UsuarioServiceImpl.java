package br.com.juliomendes90.pontointeligente.api.security.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.juliomendes90.pontointeligente.api.security.entities.Usuario;
import br.com.juliomendes90.pontointeligente.api.security.repositories.UsuarioRepository;
import br.com.juliomendes90.pontointeligente.api.security.services.UsuarioServiceOLD;

@Service
public class UsuarioServiceImpl implements UsuarioServiceOLD {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Optional<Usuario> buscarPorEmail(String email) {
		return Optional.ofNullable(this.usuarioRepository.findByEmail(email));
	}
}
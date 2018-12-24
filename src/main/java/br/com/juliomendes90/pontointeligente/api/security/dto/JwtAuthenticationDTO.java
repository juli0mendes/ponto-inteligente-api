package br.com.juliomendes90.pontointeligente.api.security.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class JwtAuthenticationDTO {

	private String email;
	private String senha;

	public JwtAuthenticationDTO() {
		super();
	}

	@NotEmpty(message = "Email nao pode ser vazio")
	@Email(message = "Emil inválido")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty(message = "Senha não pode ser vazia")
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationDTO [email=" + email + ", senha=" + senha + "]";
	}
}
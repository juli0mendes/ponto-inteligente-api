package br.com.juliomendes90.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliomendes90.pontointeligente.api.dtos.CadastroPfDTO;
import br.com.juliomendes90.pontointeligente.api.entities.Empresa;
import br.com.juliomendes90.pontointeligente.api.entities.Funcionario;
import br.com.juliomendes90.pontointeligente.api.enums.PerfilEnum;
import br.com.juliomendes90.pontointeligente.api.response.Response;
import br.com.juliomendes90.pontointeligente.api.services.EmpresaService;
import br.com.juliomendes90.pontointeligente.api.services.FuncionarioService;
import br.com.juliomendes90.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class PessoaFisicaController {

	private static final Logger log = LoggerFactory.getLogger(PessoaFisicaController.class);

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private FuncionarioService funcionarioService;

	/**
	 * Cadastra um funcionário pessoa física no sistema.
	 * 
	 * @param cadastroPfDTO
	 * @param result
	 * @return ResponseEntity<Response<CadastroPfDTO>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPfDTO>> cadastrar(@Valid @RequestBody CadastroPfDTO cadastroPfDTO,
			BindingResult result) throws NoSuchAlgorithmException {

		log.info("Cadastrando PF: {}", cadastroPfDTO.toString());

		Response<CadastroPfDTO> response = new Response<CadastroPfDTO>();

		this.validarDadosExistentes(cadastroPfDTO, result);

		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPfDTO, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro de PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPfDTO.getCnpj());

		empresa.ifPresent(emp -> funcionario.setEmpresa(emp));

		this.funcionarioService.persistir(funcionario);

		response.setData(this.converterCadastroParaDto(funcionario));

		return ResponseEntity.ok(response);

	}

	/**
	 * Verifica se a empresa está cadastrada e se o funcionário não existe na base
	 * de dados.
	 * 
	 * @param cadastroPfDTO
	 * @param result
	 */
	private void validarDadosExistentes(@Valid CadastroPfDTO cadastroPfDTO, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPfDTO.getCnpj());

		if (!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não encontrada"));
		}

		this.funcionarioService.buscarPorCpf(cadastroPfDTO.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));

		this.funcionarioService.buscarPorEmail(cadastroPfDTO.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
	}

	/**
	 * Converte os dados do DTO para funcionário.
	 * 
	 * @param cadastroPfDTO
	 * @param result
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converterDtoParaFuncionario(@Valid CadastroPfDTO cadastroPfDTO, BindingResult result)
			throws NoSuchAlgorithmException {

		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPfDTO.getNome());
		funcionario.setEmail(cadastroPfDTO.getEmail());
		funcionario.setCpf(cadastroPfDTO.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPfDTO.getSenha()));

		cadastroPfDTO.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

		cadastroPfDTO.getQtdHorasTrabalhoDia().ifPresent(
				qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));

		cadastroPfDTO.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

		return funcionario;
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPfDTO
	 */
	private CadastroPfDTO converterCadastroParaDto(Funcionario funcionario) {

		CadastroPfDTO dto = new CadastroPfDTO();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setCpf(funcionario.getCpf());
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		funcionario.getQtdHorasAlmocoOpt()
				.ifPresent(qtdHorasAlmoco -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabalhoDia -> dto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
		funcionario.getValorHoraOpt().ifPresent(valorHora -> dto.setValorHora(Optional.of(valorHora.toString())));

		return dto;
	}
}

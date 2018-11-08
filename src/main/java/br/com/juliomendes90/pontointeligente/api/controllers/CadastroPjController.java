package br.com.juliomendes90.pontointeligente.api.controllers;

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

import br.com.juliomendes90.pontointeligente.api.dtos.CadastroPjDTO;
import br.com.juliomendes90.pontointeligente.api.entities.Empresa;
import br.com.juliomendes90.pontointeligente.api.entities.Funcionario;
import br.com.juliomendes90.pontointeligente.api.enums.PerfilEnum;
import br.com.juliomendes90.pontointeligente.api.response.Response;
import br.com.juliomendes90.pontointeligente.api.services.EmpresaService;
import br.com.juliomendes90.pontointeligente.api.services.FuncionarioService;
import br.com.juliomendes90.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPjController {

	public static final Logger log = LoggerFactory.getLogger(CadastroPjController.class);

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private EmpresaService empresaService;

	public CadastroPjController() {
	}

	/**
	 * Cadastro de uma pessoa jurídica no sistema.
	 * 
	 * @param cadastroPjDTO
	 * @param result
	 * @return ResponseEntity<Response<CadastroPjDTO>>
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPjDTO>> cadastrar(@Valid @RequestBody CadastroPjDTO cadastroPjDTO,
			BindingResult result) {

		log.info("Cadastrando PJ {}", cadastroPjDTO.toString());

		Response<CadastroPjDTO> response = new Response<CadastroPjDTO>();

		this.validarDadosExistentes(cadastroPjDTO, result);

		Empresa empresa = this.converterDtoParaEmpresa(cadastroPjDTO);

		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPjDTO, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadstro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.empresaService.persistir(empresa);

		funcionario.setEmpresa(empresa);

		this.funcionarioService.persistir(funcionario);

		response.setData(this.converterCadastroPjDTO(funcionario));
		
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa ou o funcionário já existem na base de dados.
	 * 
	 * @param cadastroPjDTO
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPjDTO cadastroPjDTO, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPjDTO.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));

		this.funcionarioService.buscarPorCpf(cadastroPjDTO.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.funcionarioService.buscarPorEmail(cadastroPjDTO.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}
	
	/**
	 * Converte os dados do DTO para empresa.
	 * 
	 * @param cadastroPjDTO
	 * @return Empresa
	 */
	private Empresa converterDtoParaEmpresa(CadastroPjDTO cadastroPjDTO) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPjDTO.getCnpj());
		empresa.setRazaoSocial(cadastroPjDTO.getRazaoSocial());
		
		return empresa;
	}

	/**
	 * Converte os dados do DTO para funcionario.
	 * 
	 * @param cadastroPjDTO
	 * @param result
	 * @return Funcionario
	 */
	private Funcionario converterDtoParaFuncionario(@Valid CadastroPjDTO cadastroPjDTO, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPjDTO.getNome());
		funcionario.setEmail(cadastroPjDTO.getEmail());
		funcionario.setCpf(cadastroPjDTO.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPjDTO.getSenha()));
		
		return funcionario;
	}

	/**
	 * Converte o DTO de cadastro com os dados do funcionario e da empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPjDTO
	 */
	private CadastroPjDTO converterCadastroPjDTO(Funcionario funcionario) {
		CadastroPjDTO dto = new CadastroPjDTO();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setCpf(funcionario.getCpf());
		dto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		
		return dto;
	}
}

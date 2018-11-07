package br.com.juliomendes90.pontointeligente.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.juliomendes90.pontointeligente.api.entities.Funcionario;
import br.com.juliomendes90.pontointeligente.api.repositories.FuncionarioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioServiceTest {

	@MockBean
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private FuncionarioService funcionarioService;

	@Before
	public void setUp() throws Exception {
		BDDMockito.given(this.funcionarioRepository.save(Mockito.any(Funcionario.class))).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findById(Mockito.anyLong())).willReturn(Optional.of(new Funcionario())); 
		BDDMockito.given(this.funcionarioRepository.findByEmail(Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findByCpf(Mockito.anyString())).willReturn(new Funcionario());
	}
	
	@Test
	public void testPersistirFuncionario() {
		Funcionario f = this.funcionarioService.persistir(new Funcionario());
		assertNotNull(f);
	}
	
	@Test
	public void testBuscarFuncionarioPorId() {
		Optional<Funcionario> f = this.funcionarioService.buscarPorId(1L);
		assertTrue(f.isPresent());
	}
	
	@Test
	public void testBuscarFuncionarioPorEmail() {
		Optional<Funcionario> f = this.funcionarioService.buscarPorEmail("email@email.com");
		assertTrue(f.isPresent());
	}
	
	@Test
	public void testBuscarFuncionarioPorCpf() {
		Optional<Funcionario> f = this.funcionarioService.buscarPorCpf("12345678977");
		assertTrue(f.isPresent());
	}
}

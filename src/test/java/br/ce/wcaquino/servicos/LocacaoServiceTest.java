package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;

	// A variável deixa de estar no escopo do test
	// por ser estatica, ela não entra no escopo do testes, e vai
	// para o escopo da classe, assim o junit não reinicia o valor
	private static int count = 0;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		service = new LocacaoService();
		count++;
		System.out.println(count);
	}

	@Test
	public void deveAlugarFIlme() throws Exception {
		
		assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		Locacao locacao;

		locacao = service.alugarFilme(usuario, filme);

		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}

	// Forma Elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {

		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 0, 5.0));

		service.alugarFilme(usuario, filme);
	}

	// Forma Robusta - Segura a exception e verifica a mensagem
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		try {
			service.alugarFilme(null, filme);
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário vazio"));
		}
	}

	// Forma Nova - Esta mandando ao junit fazer o tratamento, e
	// passa ao junit o que está esperando

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {

		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		service.alugarFilme(usuario, null);

	}
	
	@Test
	public void devePagar75PctNoFIlme3() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme1", 2, 4.0), new Filme("Filme2", 2, 4.0), new Filme("Filme3", 2, 4.0));
	
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		assertThat(resultado.getValor(), is(11.0));
	}
	
	@Test
	public void devePagar50PctNoFIlme4() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme1", 2, 4.0), 
				new Filme("Filme2", 2, 4.0), new Filme("Filme3", 2, 4.0), new Filme("Filme4", 2, 4.0));
	
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		assertThat(resultado.getValor(), is(13.0));
	}
	
	@Test
	public void devePagar25PctNoFIlme5() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme1", 2, 4.0), 
				new Filme("Filme2", 2, 4.0), new Filme("Filme3", 2, 4.0),
				new Filme("Filme4", 2, 4.0), new Filme("Filme5", 2, 4.0));
	
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		assertThat(resultado.getValor(), is(14.0));
	}
	
	@Test
	public void devePagar0PctNoFIlme6() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme1", 2, 4.0), 
				new Filme("Filme2", 2, 4.0), new Filme("Filme3", 2, 4.0),
				new Filme("Filme4", 2, 4.0), new Filme("Filme5", 2, 4.0), new Filme("Filme6", 2, 4.0));
	
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		assertThat(resultado.getValor(), is(14.0));
	}
	
	@Test
	public void DeveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		
		assumeTrue(DataUtils.verificarDiaSemana(new Date(),Calendar.SATURDAY));
		
		Usuario usuario = new Usuario("Usuario1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme1", 4, 2.0));
		
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		boolean eSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		
		assertTrue(eSegunda);
	}
}

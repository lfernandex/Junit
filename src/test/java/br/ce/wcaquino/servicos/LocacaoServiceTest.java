package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
	public void testeLocacao() throws Exception {
		// cenario

		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		// acao
		Locacao locacao;

		locacao = service.alugarFilme(usuario, filme);

		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}

	// Forma Elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacao_filmeSemEstoque() throws Exception {

		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 0, 5.0));

		service.alugarFilme(usuario, filme);
	}

	// Forma Robusta - Segura a exception e verifica a mensagem
	@Test
	public void testeLocacao_UsuarioVaio() throws FilmeSemEstoqueException {

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
	public void testeLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {

		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		service.alugarFilme(usuario, null);

	}
}

package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraTeste {

	private Calculadora calc;

	@Before
	public void setup() {
		calc = new Calculadora();
	}

	@Test
	public void deveSomarDoisValores() {
		int a = 5;
		int b = 3;

		int resultado = calc.somar(a, b);

		assertEquals(8, resultado);
	}

	@Test
	public void deveSubtrairDoisValores() {
		int a = 8;
		int b = 5;

		int resultado = calc.subtracao(a, b);

		assertEquals(3, resultado);
	}

	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		int a = 6;
		int b = 3;

		int resultado = calc.divide(a, b);
		assertEquals(2, resultado);
	}

	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		int a = 10;
		int b = 0;

		calc.divide(a, b);
	}

}

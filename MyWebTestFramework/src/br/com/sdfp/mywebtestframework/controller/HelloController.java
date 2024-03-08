package br.com.sdfp.mywebtestframework.controller;

import br.com.sdfp.mywebtestframework.Produto;
import br.com.sdfp.mywebtestframework.dto.ProdutoDto;
import br.com.sdfp.mywebtestframework.service.IService;
import br.com.sdfp.webframwork.annotations.WebFrameworkController;
import br.com.sdfp.webframwork.annotations.WebframeworkBody;
import br.com.sdfp.webframwork.annotations.WebframeworkDeleteMethod;
import br.com.sdfp.webframwork.annotations.WebframeworkGetMethod;
import br.com.sdfp.webframwork.annotations.WebframeworkInject;
import br.com.sdfp.webframwork.annotations.WebframeworkPathVariable;
import br.com.sdfp.webframwork.annotations.WebframeworkPostMethod;
import br.com.sdfp.webframwork.annotations.WebframeworkPutMethod;

@WebFrameworkController
public class HelloController {
	
	@WebframeworkInject
	private IService iService;

	@WebframeworkGetMethod("/hello")
	public String returnHelloword() {
		return "Hello world!!!";
	}
	
	@WebframeworkGetMethod("/produto")
	public Produto exibirProduto() {
		Produto p = new Produto(1, "Nome1", 2000.0,"teste.jpg");
		return p;
	}
	
	@WebframeworkPostMethod("/produto")
	public Produto cadastrarProduto(@WebframeworkBody Produto produtoNovo) {
		System.out.println(produtoNovo);
		return produtoNovo;
	}
	
	@WebframeworkGetMethod("/teste")
	public String teste() {
		return "Teste";
	}
	
	@WebframeworkGetMethod("/injected")
	public String chamadaCustom() {
		return iService.chamadaCustom("Hello injected");
	}
	
	@WebframeworkGetMethod("/retornavalor/{valor}")
	public String retornoValor(@WebframeworkPathVariable Double valor) {
		return "Retornando o valor de parametro: " + valor;
	}
	
	@WebframeworkPutMethod("/updateProduto/{valor}")
	public String updateValor(@WebframeworkPathVariable Double valor, @WebframeworkBody ProdutoDto dto) { //usei um dto para não passar o id no body
		//retornando so para saber se foi alterado
		if (iService.atualizarProduto(valor, dto)) {
			return "O produto " + valor + "foi atualizado";
		}
		return "O parametro " + valor + " passado nao corresponde a nenhum produto cadastrado";
		
	}
	
	@WebframeworkDeleteMethod("/deletaProduto/{valor}")
	public String deletarProduto(@WebframeworkPathVariable Double valor) {
		if(iService.deletarProduto(valor)){
			return "O produto " + valor + " foi deletado";
		}
		return "O parametro " + valor + " passado nao corresponde a nenhum produto cadastrado";
	}
}

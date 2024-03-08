package br.com.sdfp.mywebtestframework.service;

import br.com.sdfp.mywebtestframework.dto.ProdutoDto;

public interface IService {

	public String chamadaCustom(String mensagem);

	public boolean atualizarProduto(Double valor, ProdutoDto dto);
	
	public boolean deletarProduto(Double valor);
}

package br.com.sdfp.mywebtestframework.service;

import java.util.ArrayList;
import java.util.Iterator;

import br.com.sdfp.mywebtestframework.Produto;
import br.com.sdfp.mywebtestframework.dto.ProdutoDto;
import br.com.sdfp.webframwork.annotations.WebframeworkService;

@WebframeworkService
public class ServiceImplementation implements IService{

	
	ArrayList<Produto> produtos = new ArrayList<Produto>();
	
	
	
	public ServiceImplementation() {
		carregarOsProdutos();
	}

	@Override
	public String chamadaCustom(String mensagem) {
		// TODO Auto-generated method stub
		return "Teste: " + mensagem;
	}

	@Override
	public boolean atualizarProduto(Double valor, ProdutoDto dto) {
		boolean alterado = false;
		//percorrendo o array de produtos para pesquisar qual corresponde ao parametro passado
		for(Produto it : this.produtos) {
			if (it.getId() == valor) {
				it.setNome(dto.getNome());
				it.setValor(dto.getValor());
				it.setLinkDafoto(dto.getLinkDafoto());
				alterado = true;
				System.out.println("   alterado produto de id " + valor);
			}
		}
		for (Produto produto: this.produtos) {
			System.out.println(produto);
		}
		return alterado;
	}

	private void carregarOsProdutos() {
		//array com alguns produtos para serem alterados
		Produto p2 = new Produto(2, "Nome2", 2002.0,"teste.jpg");
		Produto p3 = new Produto(3, "Nome3", 2003.0,"teste.jpg");
		
		this.produtos.add(p2);
		this.produtos.add(p3);
		
	}

	@Override
	public boolean deletarProduto(Double valor) {
		boolean apagado = false;
		Produto produtoParaRemover = null;
		
		for(Produto it : this.produtos) {
			if (it.getId() == valor) {
				produtoParaRemover = it;
				apagado = true;
				System.out.println("   deletando produto de id " + valor);
			}
		}
		
		if (produtoParaRemover != null) this.produtos.remove(produtoParaRemover);
		
		for (Produto produto: this.produtos) {
			System.out.println(produto);
		}
		
		return apagado;
		
	}

}

package br.com.sdfp.mywebtestframework.dto;

public class ProdutoDto {

	private String nome;
	private double Valor;
	private String linkDafoto;
	public ProdutoDto(String nome, double valor, String linkDafoto) {
		super();
		this.nome = nome;
		Valor = valor;
		this.linkDafoto = linkDafoto;
	}
	public ProdutoDto() {
		super();
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public double getValor() {
		return Valor;
	}
	public void setValor(double valor) {
		Valor = valor;
	}
	public String getLinkDafoto() {
		return linkDafoto;
	}
	public void setLinkDafoto(String linkDafoto) {
		this.linkDafoto = linkDafoto;
	}
	
	
}

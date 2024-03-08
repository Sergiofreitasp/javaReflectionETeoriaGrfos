package br.com.sdfp.mywebtestframework;

public class Produto {

	private int id;
	private String nome;
	private double Valor;
	private String linkDafoto;
	public Produto(int id, String nome, double valor, String linkDafoto) {
		super();
		this.id = id;
		this.nome = nome;
		Valor = valor;
		this.linkDafoto = linkDafoto;
	}
	public Produto() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "Produto [id=" + this.id + ", nome=" + this.nome + ", Valor=" + this.Valor + ", linkDafoto=" + this.linkDafoto + "]";
	}
	
	
}

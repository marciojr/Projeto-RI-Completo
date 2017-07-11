package processor;

public class Game {

	private String titulo;
	private String preco;
	private String genero;
	private String plataforma;
	private String desenvolvedor;
	private String url;

	public Game(int id, String titulo, String preco, String genero, String plataforma, String desenvolvedor,
			String url) {
		this.titulo = titulo;
		this.preco = preco;
		this.genero = genero;
		this.plataforma = plataforma;
		this.desenvolvedor = desenvolvedor;
		this.url = url;
	}

	public Game() {

	}

	public String getDesenvolvedor() {
		return desenvolvedor;
	}

	public String getGenero() {
		return genero;
	}

	public String getPlataforma() {
		return plataforma;
	}

	public String getPreco() {
		return preco;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getUrl() {
		return url;
	}

	public void setDesenvolvedor(String desenvolvedor) {
		this.desenvolvedor = desenvolvedor;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString(){
		return this.titulo + " " + this.preco + " " + this.plataforma + " " + this.genero + " " + this.desenvolvedor; 
	}

}

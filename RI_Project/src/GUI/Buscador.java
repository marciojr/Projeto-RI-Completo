package GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processor.DataProcessor;

public class Buscador {
	
	private String titulo;
	private String preco;
	private String plataforma;
	private String desenvolvedor;
	private String genero;
	public static ArrayList<String> keys;  // termos com a label. ex: titulo.call , titulo.duty
	public static List<String> terms;   //  termos sem label. ex: call, duty
	
	public Buscador(String titulo, String plataforma, String preco, String desenvolvedor, String genero){
		keys = new ArrayList();
		terms = new ArrayList<>();
		this.titulo = titulo;
		this.plataforma = plataforma;
		this.preco = preco;
		this.desenvolvedor = desenvolvedor;
		this.genero = genero;
	}
	
	public void limpaBusca(String titulo, String plataforma, String preco, String desenvolvedor, String genero){
		DataProcessor proc = new DataProcessor();
		
		if((titulo!=null) && (!this.titulo.isEmpty())){
			this.titulo = proc.standardPreprocess(this.titulo);
			keys.addAll(changeToTerms("titulo",this.titulo));
		}
		
		if((plataforma!=null) && (!this.plataforma.isEmpty())){
			this.plataforma = proc.standardPreprocess(this.plataforma);
			keys.addAll(changeToTerms("plataforma",this.plataforma));
		}
		
		if((preco!=null) && (!this.preco.isEmpty())){
			keys.addAll(changeToTerms("preco",this.preco));
		}
		
		if((desenvolvedor!=null) && (!this.desenvolvedor.isEmpty())){
			this.desenvolvedor = proc.standardPreprocess(this.desenvolvedor);
			keys.addAll(changeToTerms("desenvolvedor",this.desenvolvedor));
		}
		
		if((genero!=null) && (!this.genero.isEmpty())){
			this.genero = proc.standardPreprocess(this.genero);
			keys.addAll(changeToTerms("genero",this.genero));
		}
	}
	
	public ArrayList<String> changeToTerms(String field, String words) {
		ArrayList<String> resultado = new ArrayList<>();
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(words.split(" ")));

		for (String word : list)
			resultado.add(field + "." + word);

		return resultado;
	}
	
	public ArrayList<String> getKeys(){
		return this.keys;
	}
	
	public void setTerms(){
		for (String term : keys) {
			terms.add(term.substring(term.indexOf('.') + 1));
		}
	}
	
	public List<String> getTerms(){
		return this.terms;
	}
}

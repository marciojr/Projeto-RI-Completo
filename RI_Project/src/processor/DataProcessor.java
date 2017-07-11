package processor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.compress.utils.Charsets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Integracao.Application;
import processor.utils.Constants;
import processor.utils.CosineSimilarity;
import processor.utils.TfIdf;
import wrapper.Wrapper;

public class DataProcessor {

	public JSONArray postings;
	private Set<String> stopWordSet;
	public List<double[]> tfidfDocsVector = new ArrayList<>();
	
	private String docTermsBySemiColon = "";
	
 	private Map<Integer, Game> allGames;

	private Map<Integer, Double> scoreMap;
	
	private Map<String, Double> termsTfIdf = new HashMap<>();

	private ArrayList<String> queryTest = new ArrayList<String>();

	public HashMap<Integer, Game> jogosInfo;

	public Boolean useTfIdf = true;
	
	private Wrapper wrapper;
	
	private double[] documentsRank;

	public DataProcessor() {
		// REMOVER ANTES DE INTEGRAR
		
		queryTest.add("titulo.call");
		queryTest.add("titulo.duty");

		this.scoreMap = new HashMap<Integer, Double>();
		this.jogosInfo = new HashMap<>();
		Constants cons = new Constants();
		this.stopWordSet = new HashSet<String>(Arrays.asList(cons.STOP_WORDS_LIST));
		this.wrapper = new Wrapper();
		this.postings = wrapper.InvertedIndex(Application.domain);
		this.allGames = preProcessOutput(this.wrapper.allInputs);
		documentsRank =  new double[this.allGames.size()]; 
		getRankedGames(queryTest);
//		System.out.println(getTermFrequencyByLabel("titulo", "fifa"));

	}

	public List<Game> getRankedGames(ArrayList<String> queryTerms) {
		List<Game> rankedGames = new ArrayList<>();
		this.setDocumentsScore(queryTerms);
		
		if (useTfIdf) {
			double[] queryTfiDfVector = getQueryTfIdf(queryTerms);
			
			String[] docs = docTermsBySemiColon.split(";");
			Iterator it = scoreMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry pair = (Map.Entry) it.next();
				int docId = (int) pair.getKey();
				double[] documentsTfiDfVector = getDocumentTfIdf(docId, queryTerms);
				
				double score = getCosineSimilarity(queryTfiDfVector, documentsTfiDfVector);
//				System.out.println(score);
				scoreMap.put(docId, score);
//				System.out.println(it.next());
			}

			
		} 
		
		rankedGames = mapToGamesList(entriesSortedByValues(scoreMap));
		
//		System.out.println(scoreMap);

		// System.out.println(entriesSortedByValues(scoreMap));
		return rankedGames;

	}

	public List<Game> mapToGamesList(SortedSet<Entry<Integer, Double>> gamesMap) {
		List<Game> games = new ArrayList<>();
		Iterator it = gamesMap.iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Game jogo = allGames.get(pair.getKey());
			games.add(jogo);
		}
		return games;
	}

	public void setDocumentsScore(ArrayList<String> queryTerms) {

		JSONObject tituloTerms = (JSONObject) this.postings.get(0);
		JSONObject precoTerms = (JSONObject) this.postings.get(1);
		JSONObject generoTerms = (JSONObject) this.postings.get(2);
		JSONObject plataformaTerms = (JSONObject) this.postings.get(3);
		JSONObject desenvolvedorTerms = (JSONObject) this.postings.get(4);

		for (String query : queryTerms) {
			String[] infos = query.split("\\.");
			String label = infos[0];
			String queryTerm = infos[1];
			if ("titulo".equals(label)) {
				scoreByTermAndLabel(queryTerm, (JSONArray) tituloTerms.get("titulo"));
			} else if ("preco".equals(label)) {
				scoreByTermAndLabel(queryTerm, (JSONArray) precoTerms.get("preco"));
			} else if ("genero".equals(label)) {
				scoreByTermAndLabel(queryTerm, (JSONArray) generoTerms.get("genero"));
			} else if ("plataforma".equals(label)) {
				scoreByTermAndLabel(queryTerm, (JSONArray) plataformaTerms.get("plataforma"));
			} else {
				scoreByTermAndLabel(queryTerm, (JSONArray) desenvolvedorTerms.get("desenvolvedor"));
			}

		}

	}

	// TERM-AT-A-TIME
	public void scoreByTermAndLabel(String queryTerm, JSONArray terms) {
		for (Object term : terms) {
			JSONObject termoObj = (JSONObject) term;
			if (termoObj.containsKey(queryTerm)) {
				ArrayList documents = (ArrayList) termoObj.get(queryTerm);
				for (Object document : documents) {
					Integer docId = (Integer) document;
					if (scoreMap.containsKey(docId)) {
						double oldScore = scoreMap.get(docId);
						double newScore = oldScore + 1;
						scoreMap.put(docId, newScore);
					} else {
						scoreMap.put(docId, 1.0);
					}
				}
			}

		}
	}
	
	public void processTdIdf(){
		
	}
	
	public double[] getQueryTfIdf(ArrayList<String> queryTerms){
		double[] tfidfVector = new double[queryTerms.size()]; 
		TfIdf calc = new TfIdf();
		String[] docs = docTermsBySemiColon.split(";");
		int i = 0;
		for (String query : queryTerms) {
			String[] infos = query.split("\\.");
			String label = infos[0];
			String queryTerm = infos[1];
			int n = getTermFrequencyByLabel(label, queryTerm);
			double tf = calc.tfCalculator(docs, queryTerm);
			double idf = calc.idfCalculator(docs, queryTerm);
			tfidfVector[i++] = tf*idf;
			

		}
		
		return tfidfVector;
	}
	
	public double[] getDocumentTfIdf(int docId, ArrayList<String> queryTerms){
		double[] tfidfVector = new double[queryTerms.size()];
		TfIdf calc = new TfIdf();
		String[] docs = docTermsBySemiColon.split(";");
		String [] docsToCount = { docs[docId] };		
		
		int i = 0;
		for (String query : queryTerms) {
			String[] infos = query.split("\\.");
			String label = infos[0];
			String queryTerm = infos[1];
//			int n = getDocTermFrequencyByLabel(docId, label, queryTerm);
			double tf = calc.tfCalculator(docsToCount, queryTerm);
			double idf = calc.idfCalculator(docsToCount, queryTerm);
			
			tfidfVector[i++] = tf*idf;
			

		}
		
		return tfidfVector;
	}

	
	
	public int getDocTermFrequencyByLabel(int docId, String label, String term){
		
		int counter = 0;
		
		String[] docs = docTermsBySemiColon.split(";");
		
		String doc = docs[docId];
		
		String[] docTerms = doc.split(" ");
		for (int i = 0; i < docTerms.length; i++) {
			if(docTerms[i].trim() == term){
				counter ++;
			}
		}
		
		return counter;
		
	}
	
	public int getTermFrequencyByLabel(String label, String term){
		int counter = 0;
		JSONObject terms;
		if ("titulo".equals(label)) {
			terms = (JSONObject) this.postings.get(0);
		} else if ("preco".equals(label)) {
			terms = (JSONObject) this.postings.get(1);
		} else if ("genero".equals(label)) {
			terms = (JSONObject) this.postings.get(2);
		} else if ("plataforma".equals(label)) {
			terms = (JSONObject) this.postings.get(3);
		} else {
			terms = (JSONObject) this.postings.get(4);
		}
		
		
		JSONArray termsList = (JSONArray) terms.get(label);
		
		for (Object termO : termsList) {
			JSONObject termoObj = (JSONObject) termO;
			
			if (termoObj.containsKey(term)) {
				ArrayList documents = (ArrayList) termoObj.get(term);
				return documents.size();
			}

		}
		
		
		
		return 0;
	}
	
	public int oocorrenciasTermoDocumento(String termo, String documento){
		String[] docTerms = documento.split(" ");
		int counter = 0;
		for (String docTerm : docTerms) {
			if( docTerm.contains(termo)){
				counter ++;
			}
		}
		
		return counter;
	}
	
	public Map<Integer, Game> preProcessOutput(ArrayList<String> allInputs) {
		 
        Map<Integer, Game> jogos = new HashMap<>();
 
        String line = "";
        String[] aux = null;
        int pos = 0;
        for (int i = 0; i < allInputs.size(); i++) {
            Game jogo = new Game();
            line = allInputs.get(i);
            aux = line.split("\\[");
            line = aux[1];
            String[] infs = line.split(";");
            String keyAux ="";
            for (String inf : infs) {
                String key = "";
 
                if (!inf.contains("Sem Dados")) {
                    if (!inf.equals(" ")) {
                        key = inf.split(":")[1];
                        if(key.contains("http")){
                            keyAux = inf.split(":")[2];
                        }
                    }
                    key = key.toLowerCase();
 
                    if (inf.contains("Titulo:")) {
                        jogo.setTitulo(key);
                    } else if (inf.contains("Preco:")) {
 
                        jogo.setPreco(key);
 
                    } else if (inf.contains("plataforma:") || inf.contains("plataform:")) {
                        key.replace(" ", "");
                        jogo.setPlataforma(key);
 
                    } else if (inf.contains("desenvolvedor:") || inf.contains("developer:")) {
                        key.replace(" ", "");
                        jogo.setDesenvolvedor(key);
 
                    } else if (inf.contains("genero:") || inf.contains("categoria:") || inf.contains("category:")) {
                        jogo.setGenero(key);
                    }else if (inf.contains("Url:")) {
                        key = key+":"+keyAux;
                        jogo.setUrl(key);
                    }
                   
                   
                }
            }
            docTermsBySemiColon += jogo.toString() + ";";
            jogos.put(i, jogo);
 
        }
 
        return jogos;
    }

	public double getCosineSimilarity(double[] vector1, double[] vector2) {
		CosineSimilarity cs = new CosineSimilarity();
		double[] temp;
		int i = 0;

		if (vector1.length > vector2.length) {
			temp = new double[vector1.length];

			for (; i < vector2.length; i++)
				temp[i] = vector2[i];

			for (; i < temp.length; i++)
				temp[i] = 0;

			vector2 = temp;
		} else {
			temp = new double[vector2.length];

			for (; i < vector1.length; i++)
				temp[i] = vector1[i];

			for (; i < temp.length; i++)
				temp[i] = 0;

			vector1 = temp;
		}

		return cs.cosineSimilarity(vector1, vector2);
	}

	public String standardPreprocess(String value) {
		if (value == null)
			return "";
		return removeStopWords(removeSpecialCharacters(value).toLowerCase());
	}

	public String removeSpecialCharacters(String value) {
		return value.replaceAll("[^\\w\\s]+", "?").replaceAll("[^a-zA-Z\\s]", "");
	}

	public boolean isStopword(String word) {
		if (word.length() < 2)
			return true;
		if (word.charAt(0) >= '0' && word.charAt(0) <= '9')
			return true;
		if (this.stopWordSet.contains(word))
			return true;
		else
			return false;
	}

	public String removeStopWords(String string) {
		String result = "";
		String[] words = string.split("\\s");

		for (String word : words) {
			if (word.isEmpty())
				continue;
			if (isStopword(word))
				continue;

			result += (word + " ");
		}

		return result;
	}

	public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

}

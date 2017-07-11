package wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import org.apache.commons.compress.utils.Charsets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class Wrapper {
	public static String ARTIFACT_PATH = "..\\RI_Project\\documentos";
	public float[] recall;
	public float[] precision;
	public float[] fMeasure;

	float N = 3;
	float E = 0;
	float C = 0;
	float countArqs = 0;

	public JSONArray InvertedIndex(String [] dominios){

		String path = ARTIFACT_PATH + "\\output_wrapper\\";
		ArrayList<String> allInputs = new ArrayList<String>();
		String actPath = "";
		for (String dom : dominios) {
			if(!dom.equalsIgnoreCase("magazineluiza")&& !dom.equalsIgnoreCase("submarino")){

				actPath = path+dom+"\\"+dom+".txt"; 
				File allIn = new File(actPath);

				BufferedReader allInputB;
				try {
					allInputB = new BufferedReader(new InputStreamReader(new FileInputStream(allIn), Charsets.UTF_8));

					String in = "";

					while((in = allInputB.readLine())!= null){
						int indOf = in.indexOf("[");

						in = in.substring(indOf);
						allInputs.add(in);
					}

					allInputB.close();				
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		ArrayList<Info> titulos = new ArrayList<Info>();
		ArrayList<Info> precos = new ArrayList<Info>();
		ArrayList<Info> generos = new ArrayList<Info>();
		ArrayList<Info> plataformas = new ArrayList<Info>();
		ArrayList<Info> devs = new ArrayList<Info>();

		String line ="";
		String[] aux = null;
		int[] frequency = new int[allInputs.size()];
		
		for(int i = 0;i< allInputs.size(); i++){

			line = allInputs.get(i);
			aux = line.split("\\[");
			line = aux[1];
			String[] infs = line.split(";");				


			for (String inf : infs) {
				String key = "";
				System.out.println("INF: "+inf);

				if(!inf.contains("Sem Dados")){
					if(!inf.equals(" ")){
						key = inf.split(":")[1];
					}
					key = key.toLowerCase();

					if(inf.contains("Titulo:")){

						System.out.println("Titulo "+key);
						String[] termos = key.split(" ");

						for (String t : termos) {
							if(t.length()>1){
								//addInfo(titulos, t, i);
								addCompressed(titulos, t, i);
								frequency[i] ++;
							}
						}

					}else if(inf.contains("Preco:")){

						key = key.split(" ")[1];
						key = key.replace(".", "");
						key = key.replace(",", ".");
						double keyVal = 0;


						keyVal  = Double.parseDouble(key);


						if(keyVal <= 40){
							key = "_40";
						}else if(keyVal <= 80){
							key = "40_80";
						}else if(keyVal <= 120){
							key = "80_120";
						}else if(keyVal <= 160){
							key = "120_160";
						}else{
							key = "160_";
						}
						//addInfo(precos, key, i);
						addCompressed(precos, key, i);
						frequency[i] ++;
					}else if(inf.contains("plataforma:") || inf.contains("plataform:")){
						key.replace(" ", "");
						System.out.println(key);

						String[] termos = key.split(" ");

						for (String t : termos) {
							if(t.length()>1){
								//addInfo(plataformas, t, i);
								addCompressed(plataformas, t, i);
								frequency[i] ++;
							}
						}


					}else if(inf.contains("desenvolvedor:") || inf.contains("developer:")){
						key.replace(" ", "");
						System.out.println(inf);
						
						//addInfo(devs, key, i);
						addCompressed(devs, key, i);
						frequency[i] ++;
						
					}else if(inf.contains("genero:")||inf.contains("categoria:")||inf.contains("category:")){
						System.out.println(inf+ " KEY "+key);
						//key = key.split(" ")[1];
						//System.out.println(inf+ "KEY "+key);

						if(key.contains(",")){
							aux = key.split(",");
						}else{
							aux = new String[1];
							aux[0] = key;
						}

						for (String k : aux) {
							//addInfo(generos, k, i);
							addCompressed(generos, k, i);
							frequency[i] ++;
							System.out.println("K=> "+k);
						}
					}
				}

			}

		}


		JSONObject titulo = new JSONObject();
		titulo.put("titulo",makeJSON(titulos) );

		JSONObject genero = new JSONObject();
		genero.put("genero",makeJSON(generos) );

		JSONObject preco = new JSONObject();
		preco.put("preco",makeJSON(precos) );

		JSONObject plat = new JSONObject();
		plat.put("plataforma",makeJSON(plataformas) );

		JSONObject dev = new JSONObject();
		dev.put("desenvolvedor",makeJSON(devs) );

		JSONArray InvertedIndex = new JSONArray();

		InvertedIndex.add(titulo);
		InvertedIndex.add(preco);
		InvertedIndex.add(genero);
		InvertedIndex.add(plat);
		InvertedIndex.add(dev);

		System.out.println("AjAUShAUOIS "+ InvertedIndex.toString());


		try {
			FileWriter file = new FileWriter("final_jsonCompressed.json");
			file.write(InvertedIndex.toJSONString());
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Successfully Copied JSON Object to File...");

		return InvertedIndex;
	}
	public JSONArray makeJSON(ArrayList<Info> pTerms){
		JSONArray terms = new JSONArray();

		for (Info titulo : pTerms){
			JSONObject atual = new JSONObject();
			atual.put(titulo.key, titulo.documents);
			terms.add(atual);
		}

		return terms;
	}
	
	public void addCompressed(ArrayList<Info> list, String key, int docAtual){
		int pos = existeInfo(list, key);

		if(pos == -1){
			list.add(new Info(key, docAtual));
		}else{
			int last = list.get(pos).getLast();
			last = docAtual - last;
			list.get(pos).documents.add(last);
		}
	}
	
	public void addInfo(ArrayList<Info> list, String key, int docAtual){
		int pos = existeInfo(list, key);

		if(pos == -1){
			list.add(new Info(key, docAtual));
		}else{
			list.get(pos).documents.add(docAtual);
		}
	}

	public int existeInfo(ArrayList<Info> info, String key){
		int count = 0;
		for (Info inf : info) {
			if(inf.key.equalsIgnoreCase(key)){
				return count;
			}
			count++;
		}

		return -1;
	}

	public void Start(String [] dominios, String crawlerMethod){

		recall = new float[dominios.length];
		precision = new float[dominios.length];
		fMeasure = new float[dominios.length];
		String actDomain = "";

		try {
			for (int i = 5; i < dominios.length; i++) {
				String path = ARTIFACT_PATH + "\\" +  crawlerMethod + "\\"+ dominios[i] + "\\positives\\";
				File inputFile = new File(path + "posLinks.txt");

				String productInfoPath = ARTIFACT_PATH + "\\" + "output_wrapper\\" + dominios[i] + "\\";

				if(!new File(productInfoPath).exists()){
					new File(productInfoPath).mkdirs();
				}

				ArrayList<String> fileNames = new ArrayList<String>(Arrays.asList(new File(path).list()));

				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Charsets.UTF_8));
				File outputFile = new File(productInfoPath + "\\" + dominios[i] +".txt");
				if (!outputFile.exists()) {
					outputFile.createNewFile();
					outputFile.mkdirs();
				}

				PrintWriter pw = new PrintWriter(outputFile);

				//fileNames = ordenaDocs(fileNames);
				for (String name : fileNames) {
					actDomain = input.readLine();

					if(actDomain != null){

						pegarDados(pw, path + name, actDomain);

					}

				}
				recall[i] = (C/(N*countArqs));
				precision[i] = (C/E);
				fMeasure[i] = ((2*recall[i]*precision[i])/(recall[i]+precision[i]));

				pw.close();
				System.out.println("Wrapper para " + dominios[i] + " terminado. >>"+recall[i]+" "+precision[i]+" "+fMeasure[i]);
				C = 0;
				E = 0;
				countArqs = 0;
				input.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> ordenaDocs(ArrayList<String> lista){
		ArrayList<String> novaLista = (ArrayList<String>) lista.clone();
		String nome = "";
		int index = -1;

		for (int i = 0; i < lista.size()-1; i++) {
			nome = lista.get(i).split(".h")[0];
			nome = nome.substring(3);
			System.out.println(lista.get(i));
			index = Integer.parseInt(nome);

			novaLista.add(index, lista.get(i));
		}

		return novaLista;
	}

	public void pegarDados(PrintWriter pw, String path, String actDomain) throws IOException {
		File file = new File(path);
		Document doc = Jsoup.parse(file, "UTF-8");

		PageInfo info = new PageInfo();

		if (actDomain.contains("www.saraiva.com.br")) {
			info = getInfoSaraiva(doc);
		} else if (actDomain.contains("www.walmart.com.br")) {
			info = getInfoWalmart(doc);
		} else if (actDomain.contains("www.magazineluiza.com.br")) {
			info = getInfoMagazine(doc);
		} else if (actDomain.contains("www.americanas.com.br")) {
			info = getInfoAmericanas(doc);
		} else if (actDomain.contains("www.livrariacultura.com.br")) {
			info = getInfoCultura(doc); 
		} else if (actDomain.contains("www.submarino.com.br")) {
			info = getInfoSubmarino(doc);
		} else if (actDomain.contains("www.gamestop.com")) {
			info = getInfoGameStop(doc);
		} else if (actDomain.contains("store.steampowered.com")) {
			info = getInfoSteam(doc);
		} else if (actDomain.contains("www.fastgames.com.br")) {
			info = getInfoFastGames(doc);
			//info.setTitulo("Sem Titulo");
		} else if (actDomain.contains("store.playstation.com")) {
			info = getInfoPlayStation(doc);
		}
		String[] sPath = path.split("\\\\");
		String docName = sPath[sPath.length-1];
		docName = docName.substring(3,6);
		String titulo = info.getTitulo();
		countArqs++;
		E++;
		if(!(titulo.equalsIgnoreCase("Sem titulo") && !titulo.equalsIgnoreCase(""))){
			C++; E++;
			String preco = info.getPreco();
			if(!(preco.equalsIgnoreCase("Sem preco"))&& preco.length() >=3){
				C++;
			}
			String[] outrosDados = info.getDados().split(" > ");
			if(outrosDados.length > 1){
				C++; E++;
			}
			synchronized (pw) {
				String allData = countArqs+" [Titulo:"+ titulo+"; Preco:" +preco+"; ";
				for (int i = 0; i < outrosDados.length; i++) {
					allData += outrosDados[i]+"; ";
				}
				allData += "Url:"+actDomain+"; Doc:"+docName+" ]";



				pw.println(allData);
				pw.flush();
			}
		}
		synchronized (pw) {
			pw.flush();
		}
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Americanas~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoAmericanas(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = "";
		String preco = "";
		String dados ="";
		Elements elem = (doc.getElementsByAttributeValue("class", "product-name"));

		//Recupera o Titulo
		String fullTitle = "";
		if (elem != null && !elem.toString().equals("") && !elem.text().contains("Console")){
			fullTitle = elem.text();
			if(fullTitle.contains("Game")){
				fullTitle = fullTitle.replace("Game", "");
			}
			String[] aux = fullTitle.split("-");
			if(aux[0].equals(" ")){

				titulo = aux[1];
			}else{
				titulo = aux[0];
			}


		} else{
			titulo = "Sem titulo";
		}

		//Recupera o Preco
		elem = doc.getElementsByAttributeValue("class", "sales-price");

		if (elem != null){
			preco = elem.text();
		}else{
			preco = "Sem pre�o";
		}

		//Recupera os Dados
		elem = doc.getElementsByAttributeValue("class", "table table-striped");
		ArrayList<String[]> desc = new ArrayList<String[]>();
		StringBuffer sBuffer = new StringBuffer("");

		if (elem != null) {
			for (Element e : elem) {
				String texto[] = e.toString().toLowerCase().split("</tr>");

				// Formata��o da sa�da dos dados
				for (int i = 0; i < texto.length - 1; i++) {
					desc.add(texto[i].split("</td>"));
					String[] aux = desc.get(i)[0].split(">");
					String[] aux2 = desc.get(i)[1].split(">");

					desc.get(i)[0] = aux[aux.length-1];
					desc.get(i)[1] = aux2[aux2.length-1];

					if ((desc.get(i)[0].equals("g�nero")) || (desc.get(i)[0].equals("classifica��o indicativa"))
							|| (desc.get(i)[0].equals("desenvolvedor")) || (desc.get(i)[0].equals("�udio"))
							|| (desc.get(i)[0].equals("idiomas") || (desc.get(i)[0].equals("plataforma")))) {

						sBuffer.append(desc.get(i)[0] + ":" + desc.get(i)[1] + " > ");
					} else if (desc.get(i)[0].equalsIgnoreCase("Faixa Et�ria")) {
						sBuffer.append("Faixa Et�ria:" + desc.get(i)[1] + " > ");
					}
				}
			}

			dados = sBuffer.toString();
		} else {
			dados = "";
		}

		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);

		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Steam~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoSteam(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = "";
		String preco = "";
		String dados ="";
		Elements elem = doc.getElementsByAttributeValue("class", "details_block");
		String[] aux2 = new String[4];

		//Recupera Titulo
		if (elem != null && !elem.text().equals("")){
			String[] aux = elem.text().split("Release");
			aux[0] = aux[0].replace("Title:", "");
			aux[0] = aux[0].replace("Genre:", "");
			aux[0] = aux[0].replace("Developer:", "");
			aux[0] = aux[0].replace("Publisher:", "");

			aux2 = aux[0].split("  ");

			titulo = aux2[0];

		}else{
			titulo = "Sem titulo";
		}

		//Recupera Preco
		elem = doc.getElementsByAttributeValue("class", "game_purchase_price price");
		if(elem == null || elem.text().equals("")){
			elem = doc.getElementsByAttributeValue("class", "discount_final_price");
		}

		if (elem != null){
			if(elem.text().length() >=10) {
				String []aux = elem.text().split(" R");
				preco = aux[0];
			} else {
				preco = elem.text();
			}
		}

		//Recupera Dados

		StringBuffer sb = new StringBuffer("");
		elem = doc.getElementsByAttributeValue("class", "details_block");

		if (elem != null) {
			if(aux2.length > 3){
				sb.append("G�nero:"+aux2[1]+" > ");
				sb.append("Desenvolvedor:"+aux2[2]+" > ");
				sb.append("Distribuidora:"+aux2[3]+" > ");

				dados = sb.toString();
			}else{
				dados = "Sem Dados";
			}
		} else{
			dados = "Sem Dados";
		}

		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);
		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Saraiva~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoSaraiva(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = doc.title();
		String preco = "";
		String dados ="";

		//Recupera Titulo
		if (titulo.equals("") || titulo.contains("Console")) {
			titulo = "Sem Titulo";
		}else{
			String[] aux = titulo.split("-");
			titulo = aux[0];
		}

		if(titulo.contains("Sem T")){
			pi.setPreco("Sem Preco");
			pi.setTitulo(titulo);
			pi.setDados("Sem dados");
		}else{
			//Recupera Preco
			Elements elem = doc.getElementsByAttributeValue("class", "special-price");
			if (elem != null && elem.size() > 0) {
				preco = elem.toString().substring(elem.toString().indexOf("R$"),
						elem.toString().indexOf("</strong>"));
			} else {

				preco = "Sem pre�o";
			}

			//Recupera Dados
			elem = doc.getElementsByAttributeValue("itemprop", "title");
			String genero = elem.text();
			StringBuffer sb = new StringBuffer("");

			if(genero.contains("Jogos")){
				if(genero.length() > 0){
					sb.append("G�nero:" + genero.substring(genero.indexOf("Jogos")) + " > ");
				}
				ArrayList<Object> desc = new ArrayList<Object>();
				ArrayList<Object> carc = new ArrayList<Object>();
				ArrayList<Box> fim = new ArrayList<Box>();

				// Pegar os elementos da tabela TH
				for (Element th : doc.getElementsByAttributeValue("class", "label")) {
					desc.add(th.text());
				}

				// Pegar os elementos da tabela TD
				for (Element td : doc.getElementsByAttributeValue("class", "data last")) {
					carc.add(td.text());
				}

				if (desc.size() > 0) {
					int i = 0;
					while (i < desc.size() && i < carc.size()) {
						Box box = new Box(desc.get(i), carc.get(i));
						i++;
						fim.add(box);
					}
					i = 0;
					while (i < fim.size()) {
						if (fim.get(i).desc.toString().equalsIgnoreCase("Marca")
								|| fim.get(i).desc.toString().equalsIgnoreCase("Classifica��o Indicativa")
								|| fim.get(i).desc.toString().equalsIgnoreCase("Idioma do �udio")
								|| fim.get(i).desc.toString().equalsIgnoreCase("Plataforma")) {
							sb.append((fim.get(i).desc.toString() + ":" + fim.get(i).carac.toString()) + " > ");
						}
						i++;
					}

					dados = sb.toString();
				} else {
					dados = "";
				}
			}

			pi.setPreco(preco);
			pi.setTitulo(titulo);
			pi.setDados(dados);
		}


		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Cultura~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoCultura(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = doc.title();
		String preco = "";
		String dados ="";
		Elements elem = doc.getElementsByAttributeValue("class", "ribbons");

		//Recupera Titulo
		if (elem != null && elem.text().contains("onsole")) {
			titulo = "Sem titulo";
		} 

		//Recupera Preco
		elem = doc.getElementsByAttributeValue("class", "price");
		if (elem != null) {
			if (elem.text().length() > 40) {
				preco = elem.text().substring(elem.text().indexOf("R$"), elem.text().indexOf("pr"));				
			} else if (elem.text().length() >= 25 && elem.text().length() <= 40 || elem.text().contains(" R$ ")) {
				preco = elem.text().substring(elem.text().indexOf("R$"), elem.text().indexOf(" R$ "));
			} else if(elem.text().length() >= 11 && elem.text().length() <= 24) {
				preco = elem.text().substring(elem.text().indexOf("R$"), elem.text().indexOf("pr"));
			} else {
				preco = elem.text();
			}
		} else{
			preco = "Sem pre�o";
		}

		//Recupera Dados
		elem = doc.getElementsByAttributeValue("id", "product-list-detail");
		ArrayList<String[]> desc = new ArrayList<String[]>();
		StringBuffer sBuffer = new StringBuffer("");

		if (elem != null) {
			for (Element e : elem) {
				String texto[] = e.toString().toLowerCase().split("</li>");

				// Formata��o da sa�da dos dados
				for (int i = 0; i < texto.length - 1; i++) {
					desc.add(texto[i].split("</b>"));
					if(texto[i].contains("</b>")){

						if (desc.get(i)[0].length() > 8) {
							desc.get(i)[0] = desc.get(i)[0].substring(desc.get(i)[0].indexOf("<b>"),
									desc.get(i)[0].indexOf(":"));
							desc.get(i)[0] = desc.get(i)[0].substring(3, desc.get(i)[0].length());

							if ((desc.get(i)[0].equals("g�nero")) || (desc.get(i)[0].contains("classifica��o indicativa"))
									|| (desc.get(i)[0].equals("�udio")) || (desc.get(i)[0].equals("idiomas"))|| (desc.get(i)[0].equals("plataforma"))) {

								sBuffer.append(desc.get(i)[0] + ":" + desc.get(i)[1] + " > ");
							}else if((desc.get(i)[0].equals("desenvolvedor"))){
								String[] aux = desc.get(i)[1].split(">");
								desc.get(i)[1] = aux[1].split("<")[0];

								sBuffer.append(desc.get(i)[0] + ":" + desc.get(i)[1] + " > ");
							}else if((desc.get(i)[0].equals("categoria"))){
								String[] aux= desc.get(i)[1].split("</a>");
								desc.get(i)[1] = aux[aux.length-1].split(">")[1];

								sBuffer.append(desc.get(i)[0] + ":" + desc.get(i)[1] + " > ");
							}
						}
					}
				}
			}

			dados = sBuffer.toString();
		} else{
			dados = "";
		}

		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);

		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FastGames~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoFastGames(Document doc){
		PageInfo pi = new PageInfo();

		String titulo = "";
		String preco = "";
		String dados = "";

		Elements elem = doc.getElementsByAttributeValue("itemprop", "name");
		if (elem != null) {
			if(elem.text().contains("-")){
				titulo = elem.text().split("-")[0];

			} else {
				titulo = elem.text();

			}
		} else {
			titulo = "Sem t�tulo";
		}
		//Recupera Preco
		elem = doc.getElementsByAttributeValue("class", "preco-avista precoAvista");
		if (elem != null && !elem.toString().equals("")) {
			if(elem.text().length() < 10) {
				preco = elem.text();
			} else {
				preco = elem.text().split(" R")[0];
			}
		} else{
			preco = "Sem pre�o";
		}

		//Recupera Dados

		elem = doc.getElementsByAttributeValue("cellpadding", "0");
		//System.out.println(elem.toString());
		StringBuffer sb = new StringBuffer("");
		ArrayList<String[]> desc = new ArrayList<String[]>();

		if (elem != null && elem.text().contains("G�nero")) {

			String[] aux = elem.toString().split("</table>");
			for (int i = 0; i < aux.length; i++) {
				if(aux[i].contains("</strong>")){
					desc.add(aux[i].split("</strong>"));

					if(!desc.get(i)[0].endsWith("font>")){
						String[] aux2 = desc.get(i)[0].split(">");
						desc.get(i)[0] = aux2[aux2.length-1];
					}else{
						desc.get(i)[0] = desc.get(i)[0].replace("</font>", "");
						String[] aux3 = desc.get(i)[0].split(">");
						desc.get(i)[0] = aux3[aux3.length-1];
					}

					if(!desc.get(i)[1].endsWith("font>")){
						String[] aux2 = desc.get(i)[1].split(">");
						desc.get(i)[1] = aux2[aux2.length-1];
					}else{
						desc.get(i)[1] = desc.get(i)[1].replace("</font>", "");
						String[] aux3 = desc.get(i)[1].split(">");
						desc.get(i)[1] = aux3[aux3.length-1];
					}

					if(desc.get(i)[0].equalsIgnoreCase("g�nero") || desc.get(i)[0].equalsIgnoreCase("quantidade de jogadores") 
							|| desc.get(i)[0].equalsIgnoreCase("idade recomendada")|| desc.get(i)[0].equalsIgnoreCase("marca")){
						sb.append(desc.get(i)[0]+":"+desc.get(i)[1]+"\r\n");
					}
				}

			}
		} else{
			dados = "";
		}	
		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);

		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~MagazineLuiza~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoMagazine(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = doc.title();
		String preco = "";
		String dados ="";

		titulo = titulo.split(" - ")[0];

		if(titulo.contains("Console")){
			titulo = "Sem Titulo";
		}
		//Recupera Preco
		Elements elem = doc.getElementsByAttributeValue("class", "js-price-value");
		int aux = elem.text().length();
		if (elem != null  && elem.text().length() >=3){
			if(!elem.text().contains("XXX") ){
				preco = elem.text();
			}else{
				preco = "Sem pre�o";
			}

		}else{
			preco = "Sem pre�o";
		}
		//Recupera Dados
		boolean isConsole = false;
		elem = doc.getElementsByAttributeValue("class", "fs-row");
		StringBuffer sb = new StringBuffer("");

		if (elem != null) {
			for (Element e : elem) {
				if (e.text().contains("Informa��es")){
					sb.append(e.text().substring(e.text().indexOf("Marca"), e.text().indexOf("Refer�ncia")) + "\r\n");
				}
				if (e.text().contains("G�nero") || e.text().contains("Idioma")
						|| e.text().contains("Idade recomendada")){
					sb.append(e.text() + " > ");
				}
				if(e.text().contains("Console") ){
					isConsole = true;
				}
			}

			dados = sb.toString();
		} else{
			dados = "Sem Dados";
		}

		if(!isConsole){
			pi.setPreco(preco);
			pi.setTitulo(titulo);
			pi.setDados(dados);
		}else{
			pi.setTitulo("Sem titulo");
		}


		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~GameStop~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoGameStop(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = "";
		String preco = "";
		String dados ="";
		Elements elem = (doc.getElementsByAttributeValue("class", "grid_17 ats-prod-title"));

		//Recupera Titulo
		if (elem != null && !elem.toString().equals("")){
			if(elem.text().contains("by")){
				String[] aux = elem.text().split(" by ");
				titulo = aux[0];
			}else{
				titulo = elem.text();
			}
		}else{
			titulo = "Sem Titulo";
		}
		//Recupera Preco
		elem = (doc.getElementsByAttributeValue("class", "ats-prodBuy-price"));

		if(elem != null && !elem.toString().equals("")){
			String[] aux = elem.text().split(" ");
			preco = aux[0];
		}else{
			preco = "Sem Preco";
		}


		//Recupera Dados
		boolean isConsole = false;
		elem = (doc.getElementsByAttributeValue("class", "gameinfo nograd grid_15 ats-prodRating-gameInfo"));

		ArrayList<String[]> desc = new ArrayList<String[]>();
		StringBuffer sBuffer = new StringBuffer("");

		if (elem != null) {
			for (Element e : elem) {
				String texto[] = e.toString().toLowerCase().split("</li>");

				// Formata��o da sa�da dos dados
				for (int i = 0; i < texto.length - 1; i++) {
					desc.add(texto[i].split("<span>"));
					String[] aux = desc.get(i)[0].split(">");
					String[] aux2 = desc.get(i)[1].split("<");

					desc.get(i)[0] = aux[aux.length-1];
					desc.get(i)[1] = aux2[0];

					if (desc.get(i)[0].equals(" developer:") || desc.get(i)[0].equals(" category:")
							|| desc.get(i)[0].equals(" publisher:") || desc.get(i)[0].equals(" platform:")) {
						desc.get(i)[0] = desc.get(i)[0].replace(" ", "");
						sBuffer.append(desc.get(i)[0] +" "+ desc.get(i)[1] + " > ");
						//						if(desc.get(i)[1].equalsIgnoreCase("Systems")){
						//
						//							isConsole = true;
						//						}
					} 
				}
			}

			dados = sBuffer.toString();
		} else {
			dados = "Sem dados";
		}

		if(!isConsole){
			pi.setPreco(preco);
			pi.setTitulo(titulo);
			pi.setDados(dados);
		}else{
			pi.setTitulo("Sem titulo");
			pi.setPreco("Sem preco");
			pi.setDados("Sem dados");
		}



		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Pleisteixo~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoPlayStation(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = "";
		String preco = "";
		String dados ="";

		//Recupera Titulo

		//Recupera Preco

		//Recupera Dados

		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);
		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Submarino~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoSubmarino(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = "";
		String preco = "";
		String dados ="";

		//Recupera Titulo
		Elements elem = doc.getElementsByAttributeValue("class", "product-name");
		if (elem != null && !elem.text().contains("Console")) {
			titulo = elem.text();
		} else {
			titulo = "Sem t�tulo";
		}

		//Recupera Preco
		elem = doc.getElementsByAttributeValue("class", "sales-Price");
		if (elem != null) {
			preco = elem.text();
		} else {
			preco = "Sem pre�o";
		}
		//Recupera Dados
		elem = doc.getElementsByAttributeValue("class", "table table-striped");
		ArrayList<String[]> desc = new ArrayList<String[]>();
		StringBuffer sb = new StringBuffer("");

		if (elem != null) {
			for (Element e : elem) {
				String texto[] = e.toString().toLowerCase().split("</tr>");

				// Formata��o da sa�da dos dados
				for (int i = 0; i < texto.length - 1; i++) {
					desc.add(texto[i].split("</td>"));

					String[] aux = desc.get(i)[0].split(">");
					String[] aux2 = desc.get(i)[1].split(">");

					desc.get(i)[0] = aux[aux.length-1];
					desc.get(i)[1] = aux2[aux2.length-1];

					if ((desc.get(i)[0].equalsIgnoreCase("G�nero")) || (desc.get(i)[0].equalsIgnoreCase("Classifica��o indicativa"))
							|| (desc.get(i)[0].equalsIgnoreCase("Desenvolvedor")) || (desc.get(i)[0].equalsIgnoreCase("�udio"))
							|| (desc.get(i)[0].equalsIgnoreCase("idiomas"))|| (desc.get(i)[0].equalsIgnoreCase("marca"))|| (desc.get(i)[0].equalsIgnoreCase("plataforma"))) {

						sb.append(desc.get(i)[0] + ":" + desc.get(i)[1] + " > ");
					} else if (desc.get(i)[0].equalsIgnoreCase("Faixa Et�ria")) {
						sb.append("Faixa Et�ria:" + desc.get(i)[1] + " > ");
					}
				}
			}

			dados = sb.toString();
		} else {
			dados = "";
		}


		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);
		return pi;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Walmart~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public PageInfo getInfoWalmart(Document doc){
		PageInfo pi = new PageInfo();
		String titulo = "";
		String preco = "";
		String dados ="";

		//Recupera Titulo
		Elements elem = doc.getElementsByAttributeValue("class", "product-title");

		if(elem != null && !elem.text().contains("Console")){
			titulo = elem.text().split(" - ")[0];	
		}else{
			titulo = "Sem Titulo";
		}

		//Recupera Preco

		elem = doc.getElementsByAttributeValue("class", "product-price-sell");

		if (elem != null) {
			if(elem.text().length()>10){
				preco= elem.text().split(" R")[0];
			}else{
				preco = elem.text();
			}

		} else {
			preco = "Sem pre�o";
		}

		//Recupera Dados
		StringBuffer sb = new StringBuffer("");
		ArrayList<String[]> desc = new ArrayList<String[]>();
		elem = doc.getElementsByAttributeValue("class", "characteristics table-striped");


		if (elem != null) {
			for (Element e : elem) {
				String texto[] = e.toString().toLowerCase().split("</tr>");

				// Formata��o da sa�da dos dados
				for (int i = 0; i < texto.length - 1; i++) {

					desc.add(texto[i].split("</"));


					if(desc.get(i)[1].contains("tfoot")){
						String[] aux = desc.get(i)[1].split(">");
						String[] aux2 = desc.get(i)[2].split(">");

						desc.get(i)[0] = aux[aux.length-1];
						desc.get(i)[1] = aux2[aux2.length-1];
					}else{
						String[] aux = desc.get(i)[0].split(">");
						String[] aux2 = desc.get(i)[1].split(">");

						desc.get(i)[0] = aux[aux.length-1];
						desc.get(i)[1] = aux2[aux2.length-1];
					}




					if ((desc.get(i)[0].equalsIgnoreCase("G�nero")) || (desc.get(i)[0].equalsIgnoreCase("Classifica��o indicativa"))
							|| (desc.get(i)[0].equalsIgnoreCase("Desenvolvedor")) || (desc.get(i)[0].equalsIgnoreCase("idiomas �udio"))
							|| (desc.get(i)[0].equalsIgnoreCase("marca"))|| (desc.get(i)[0].equalsIgnoreCase("plataforma"))) {

						sb.append(desc.get(i)[0] + ":" + desc.get(i)[1] + " > ");
					} else if (desc.get(i)[0].equalsIgnoreCase("Faixa Et�ria")) {
						sb.append("Faixa Et�ria:" + desc.get(i)[1] + " > ");
					}
				}
			}

			dados = sb.toString();
		} else {
			dados = "";
		}

		dados = sb.toString();
		pi.setPreco(preco);
		pi.setTitulo(titulo);
		pi.setDados(dados);

		return pi;
	}



}

class Info{
	String key;
	ArrayList<Integer> documents;

	public Info(String key, int doc){
		this.key = key;
		this.documents = new ArrayList<Integer>();
		this.documents.add(doc);
	}
	
	public int getLast(){
		int last = 0;
		for (Integer integer : documents) {
			last += integer;
		}
		
		return last;
	}
}
class Box {
	Object desc;
	Object carac;

	public Box(Object desc, Object carac) {
		this.desc = desc;
		this.carac = carac;
	}
}
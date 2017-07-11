package processor;

import java.util.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import org.json.simple.parser.ParseException;

public class Test {
	
	public static void main(String[] args) {
		DataProcessor dp = new DataProcessor();
	}


	public static List<Game> getRankedDocuments() {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(5);
		System.out.println(randomInt);
		List<Game> documents = new ArrayList<Game>();

		String titulo = "PlayStation 4 1TB Limited Edition Gold System";
		String preco = "$ 249.99";
		String platform = "playstation 4";
		String publisher = "sony computer entertainment";
		String developer = "sony computer entertainment";
		String category = "systems";
		String url = "http://www.gamestop.com/ps4/consoles/playstation-4-1tb-limited-edition-gold-system/148840";
		int id = 449;

		String titulo1 = "Ultra Street Fighter II: The Final Challengers";
		String preco1 = "$ 39.99";
		String platform1 = "nintendo switch";
		String publisher1 = "capcom";
		String developer1 = "capcom";
		String category1 = "fighting";
		String url1 = "http://www.gamestop.com/nintendo-switch/games/ultra-street-fighter-ii-the-final-challengers/143459";
		int id1 = 458;

		String titulo2 = "Fire Emblem Echoes: Shadows of Valentia";
		String preco2 = "$ 39.99";
		String platform2 = "nintendo 3ds";
		String publisher2 = "nintendo";
		String developer2 = "nintendo";
		String category2 = "role-playing";
		String url2 = "http://www.gamestop.com/nintendo-3ds/games/fire-emblem-echoes-shadows-of-valentia/142065";
		int id2 = 459;

		String titulo3 = "Ever Oasis";
		String preco3 = "$ 39.99";
		String platform3 = "nintendo 3ds";
		String publisher3 = "nintendo";
		String developer3 = "grezzo";
		String category3 = "action , adventure";
		String url3 = "http://www.gamestop.com/nintendo-3ds/games/ever-oasis/146854";
		int id3 = 466;

		String titulo4 = "Crash Bandicoot N. Sane Trilogy";
		String preco4 = "$ 39.99";
		String platform4 = "playstation 4";
		String publisher4 = "activision";
		String developer4 = "activision";
		String category4 = "action";
		String url4 = "http://www.gamestop.com/ps4/games/crash-bandicoot-n-sane-trilogy/139553";
		int id4 = 468;

		String titulo5 = "Final Fantasy XII: The Zodiac Age Limited Steelbook Edition";
		String preco5 = "$ 49.99";
		String platform5 = "playstation 4";
		String publisher5 = "square enix";
		String developer5 = "square enix";
		String category5 = "role-playing";
		String url5 = "http://www.gamestop.com/ps4/games/final-fantasy-xii-the-zodiac-age-limited-steelbook-edition/131897";
		int id5 = 469;
		
		
		Game game1 = new Game(id, titulo, preco, category, platform, developer,url);
		Game game2 = new Game(id2, titulo2, preco2, category2, platform2, developer2,url2);
		Game game3 = new Game(id3, titulo3, preco3, category3, platform3, developer3,url3);
		Game game4 = new Game(id4, titulo4, preco4, category4, platform4, developer4,url4);
		Game game5 = new Game(id5, titulo5, preco5, category5, platform5, developer5,url5);
		
		documents.add(game1);
		documents.add(game2);
		documents.add(game3);
		documents.add(game4);
		documents.add(game5);
		
		documents = documents.subList(0, randomInt);
		
		Collections.shuffle(documents);
		
		
		return documents;
		
	}
}

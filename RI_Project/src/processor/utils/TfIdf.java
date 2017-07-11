package processor.utils;

import java.util.List;

public class TfIdf {

	
	public double tfCalculator(String[] docs, String termToCheck) {
		double count = 0;
		
		for (String s : docs){
			if (s.contains(termToCheck)){
				count++;
			}
				
		}
				

		return 1 + Math.log(count);
	}

	
	public double idfCalculator(String[] termsDocsArray, String termToCheck) {
		double count = 0;
		for (String ss : termsDocsArray){
			if (ss.contains(termToCheck)) {
				count++;
			}

		}
					
		return 1 + Math.log(termsDocsArray.length / count);
	}
}
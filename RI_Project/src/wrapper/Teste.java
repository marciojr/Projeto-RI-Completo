package wrapper;

public class Teste {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String a = "1.0 [lalala]";
		String b = "128.0 [kkk]";
		
		int c = a.indexOf("[");
		
		System.out.println(c);
		a = a.substring(c);
		c = b.indexOf("[");
		
		System.out.println(c);
		
		
		System.out.println(a);
	}

}

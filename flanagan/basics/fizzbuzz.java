package flanagan.basics;
public class fizzbuzz {
	public static void main(String arg[]) {
		for (int i=1; i<=100; i++ ) {
			if (((i % 5) == 0) && ((i % 7) == 0)) System.out.println("fizzbuzz");
			else if ((i % 5) == 0) System.out.println("fizz");
			else if ((i % 7) == 0) System.out.println("buzz");
			else System.out.println(i);
		}
	}
}

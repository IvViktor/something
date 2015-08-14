package flanagan.basics;
public class substring{
	public static void main(String arg[]) {
			int fir = Integer.parseInt(arg[1]);
			int sec = Integer.parseInt(arg[2]);
		for (int i=fir; i<(sec+fir); i++){
			System.out.print(arg[0].charAt(i));
		}
		System.out.println();
	}
}

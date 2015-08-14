package flanagan.basics;
import java.io.*;
public class backstrings{
		public static void main(String args[]) throws IOException{
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			for (;;){
			System.out.print(">>");
			String line = input.readLine();
			if (line.equals("tiuq")) break;
			for (int k=line.length()-1; k>=0; k--){
			System.out.print(line.charAt(k));	
			}
			System.out.println();
			}	
		}
}

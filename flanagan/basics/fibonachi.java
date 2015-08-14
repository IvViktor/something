package flanagan.basics;
public class fibonachi {
	public static void main(String ax[]){
		int n0=1, n1=1, n2;
		System.out.print(n0 + " " + n1 + " ");
		for (int i=0; i<=20; i++){
			n2=n1+n0;
			System.out.print(n2 + " ");
			n0=n1;
			n1=n2;
		} 
		System.out.println();
	}
}

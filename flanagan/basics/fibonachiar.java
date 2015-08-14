package flanagan.basics;
public class fibonachiar {
	public static void main(String ax[]){
		int[] symbols = new int[20];
		symbols[0]=1;
		symbols[1]=1;
		int n,k;
		System.out.print(symbols[0] + " " + symbols[1] + " ");
		for (int i=2; i<=19; i++){
			n=i-2;
			k=i-1;
			symbols[i]=(symbols[n] + symbols[k]);
			System.out.print(symbols[i] + " ");
		} 
		System.out.println();
	}
}

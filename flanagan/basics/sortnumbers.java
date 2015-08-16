package flanagan.basics;
import java.io.*;
public class sortnumbers{
	public static void sort(double[] nums){
		for (int i=0; i<nums.length; i++){
			int min=i;
			double tmp;
			for (int j=i; j<nums.length; j++){
				if  (nums[min] > nums[j]) min=j;
			}	
			tmp=nums[i];
			nums[i]=nums[min];
			nums[min]=tmp;
		}
	}
	public static void findnum(double[] arr, String line){
		int tmp=arr.length/2;
		int kar=0;
		int ostat=arr.length/2;
		double num=(double) Integer.parseInt(line);
		while (ostat != 0){
			ostat=ostat/2;
			if (arr[tmp] < num){
				tmp+=ostat;
				kar=(-1);
				continue;
			}
			if (arr[tmp] > num){
				tmp-=ostat;
				kar=1;
				continue;
			}
			if (arr[tmp] == num){ kar=0;  break;}
		}
		if (kar == (-1)){
		System.out.println("Minimal relative number: " + arr[tmp]);
		System.out.println("Maximum relative number: " + arr[tmp+1]); }
		if (kar == 1){
		System.out.println("Minimal relative number: " + arr[tmp-1]);
		System.out.println("Maximum relative number: " + arr[tmp]);}
		if (kar == 0){
		System.out.println("Minimal relative number: " + arr[tmp-1]);
		System.out.println("Maximum relative number: " + arr[tmp+1]);}
		}
	public static void main (String args[]) throws IOException{
		double[] nums = new double[100];
		for(int i=0; i<nums.length; i++) nums[i]=Math.random()*100;
		sort(nums);
		for(int i=0; i<nums.length; i++) System.out.println(nums[i]);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Type a number:>");
		String line = in.readLine();
		findnum(nums,line);
			}
}

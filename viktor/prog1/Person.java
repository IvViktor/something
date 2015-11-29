package viktor.prog1;
import java.io.*;
import java.util.*;
public class Person extends java.util.Vector<String>{
	public static final int NAMESORT=0;
	public static final int NUMSORT=1;
	public static final int DATESORT=2;
	public static final int EXPIRESORT=3;
	protected Person(String name,String num,String date,String expire){
		super();
		this.add(name);
		this.add(num);
		this.add(date);
		this.add(expire);
	}
       protected static Vector<Person> getPersonsList(String fileName){// throws /*IllegalArgumentException,*/IOException{
		Vector<Person> list=new Vector<Person>();
		try(BufferedReader in=new BufferedReader(new FileReader(fileName));){
			String line,name,num,date,expire;
			while((line=in.readLine())!=null){
			StringTokenizer st = new StringTokenizer(line,":");
		/*if(!st.hasMoreTokens()) throw new IllegalArgumentException("Неправильный формат"+
							" документа: "+fileName);*/
			name=st.nextToken();
			num=st.nextToken();
			date=st.nextToken();
			expire=st.nextToken();
			list.add(new Person(name,num,date,expire));
			}
		} catch (IOException e){System.out.println("Cannot read file");}
		return list;
	}
	protected static final Comparator<Person> nameOrder = new Comparator<Person>(){
		public int compare(Person a,Person b){
			int char_num;
			String str1=a.get(0); String str2=b.get(0);
			if(str1.length()>str2.length()) char_num=str2.length();
			else char_num=str1.length();
			str1=str1.toLowerCase();str2=str2.toLowerCase();
			for(int i=0;i<char_num;i++){
				if(str1.charAt(i)<str2.charAt(i)) return -1;
				else if(str1.charAt(i)>str2.charAt(i)) return 1;
			}
			return 0;
		}
	};
	protected static final Comparator<Person> numOrder = new Comparator<Person>(){
		public int compare(Person a,Person b){
			int char_num;
			String str1=a.get(1); String str2=b.get(1);
			int intA=decodeString(str1);
			int intB=decodeString(str2);
			if(intA<intB) return -1;
			else if(intA>intB) return 1;
			return 0;
		}
	};
	protected static final Comparator<Person> dateOrder = new Comparator<Person>(){
		public int compare(Person a,Person b){
		String strA = a.get(2); String strB=b.get(2);
		StringTokenizer sta=new StringTokenizer(strA,".");
		StringTokenizer stb=new StringTokenizer(strB,".");
		String[] buffB = new String[3];
		String[] buffA = new String[3];
		for(int i=2;i>=0;i--) buffA[i]=sta.nextToken();
		for(int j=2;j>=0;j--) buffB[j]=stb.nextToken();
		strA=strB="";
		for(int i=0;i<buffA.length;i++) strA+=buffA[i];
		for(int j=0;j<buffB.length;j++) strB+=buffB[j];
		long dateA=Long.parseLong(strA);
		long dateB=Long.parseLong(strB);
		if(dateA<dateB) return -1;
		else if(dateA>dateB) return 1;
		return 0;
		}
	};
	protected static final Comparator<Person> expireOrder = new Comparator<Person>(){
		public int compare(Person a,Person b){
		String strA = a.get(3); String strB=b.get(3);
		StringTokenizer sta=new StringTokenizer(strA,".");
		StringTokenizer stb=new StringTokenizer(strB,".");
		String[] buffB = new String[3];
		String[] buffA = new String[3];
		for(int i=2;i>=0;i--) buffA[i]=sta.nextToken();
		for(int j=2;j>=0;j--) buffB[j]=stb.nextToken();
		strA=strB="";
		for(int i=0;i<buffA.length;i++) strA+=buffA[i];
		for(int j=0;j<buffB.length;j++) strB+=buffB[j];
		long dateA=Long.parseLong(strA);
		long dateB=Long.parseLong(strB);
		if(dateA<dateB) return -1;
		else if(dateA>dateB) return 1;
		return 0;
		}
	};
	protected static Vector<Person> sort(Vector<Person> list,int mode){
		if(mode==0) Collections.sort(list,nameOrder);
		else if (mode==1) Collections.sort(list,numOrder);
		else if(mode==2) Collections.sort(list,dateOrder);
		else if(mode==3) Collections.sort(list,expireOrder);
		return list;
	}
	public String toString(){
	String msg=this.get(0)+" допуск №"+this.get(1)+" від "+this.get(2)+". Дійсний до "+this.get(3);
	return msg;
	}
	protected static Vector<Person> search(Vector<Person> input,String pattern,int mode){
		Vector<Person> list = new Vector<Person>();
		for(int i=0;i<input.size();i++){
			Person person=input.get(i);
			String strA=person.get(mode).toLowerCase();
			String strB=pattern.toLowerCase();
			if(strA.startsWith(strB)) list.add(person);
		}
		return list;
	}
	private static int decodeString(String s){
		String dec=new String();
		for(int i=0;i<s.length();i++){
			char c=s.charAt(i);
			if((c>='0')&&(c<='9')) dec+=c;
			else continue;
		}
		return Integer.parseInt(dec);
	}
	protected static void addUser(Vector<Person> list,String name,String num,String date_from,String expire){
		 list.add(new Person(name,num,date_from,expire));
	}
	protected static void delUser(Vector<Person> list,int index){
		list.removeElementAt(index);
	}
	protected static void writeList(Vector<Person> list,String filename){
		try(PrintWriter out=new PrintWriter(new FileWriter(filename));){
			for(int i=0;i<list.size();i++){
				for(int j=0;j<4;j++) out.print(list.get(i).get(j)+":");
				out.println();
			}
			out.flush();
		} catch (IOException e){System.out.println("Cannot write file");}
	}
	protected static final Comparator<String> nameOrderTab = new Comparator<String>(){
		public int compare(String a,String b){
			int char_num;
			String str1=a; String str2=b;
			if(str1.length()>str2.length()) char_num=str2.length();
			else char_num=str1.length();
			str1=str1.toLowerCase();str2=str2.toLowerCase();
			for(int i=0;i<char_num;i++){
				if(str1.charAt(i)<str2.charAt(i)) return -1;
				else if(str1.charAt(i)>str2.charAt(i)) return 1;
			}
			return 0;
		}
	};
	protected static final Comparator<String> numOrderTab = new Comparator<String>(){
		public int compare(String a,String b){
			int char_num;
			String str1=a; String str2=b;
			int intA=decodeString(str1);
			int intB=decodeString(str2);
			if(intA<intB) return -1;
			else if(intA>intB) return 1;
			return 0;
		}
	};
	protected static final Comparator<String> dateOrderTab = new Comparator<String>(){
		public int compare(String a,String b){
		String strA = a; String strB=b;
		StringTokenizer sta=new StringTokenizer(strA,".");
		StringTokenizer stb=new StringTokenizer(strB,".");
		String[] buffB = new String[3];
		String[] buffA = new String[3];
		for(int i=2;i>=0;i--) buffA[i]=sta.nextToken();
		for(int j=2;j>=0;j--) buffB[j]=stb.nextToken();
		strA=strB="";
		for(int i=0;i<buffA.length;i++) strA+=buffA[i];
		for(int j=0;j<buffB.length;j++) strB+=buffB[j];
		long dateA=Long.parseLong(strA);
		long dateB=Long.parseLong(strB);
		if(dateA<dateB) return -1;
		else if(dateA>dateB) return 1;
		return 0;
		}
	};
	protected static final Comparator<String> expireOrderTab = new Comparator<String>(){
		public int compare(String a,String b){
		String strA = a; String strB=b;
		StringTokenizer sta=new StringTokenizer(strA,".");
		StringTokenizer stb=new StringTokenizer(strB,".");
		String[] buffB = new String[3];
		String[] buffA = new String[3];
		for(int i=2;i>=0;i--) buffA[i]=sta.nextToken();
		for(int j=2;j>=0;j--) buffB[j]=stb.nextToken();
		strA=strB="";
		for(int i=0;i<buffA.length;i++) strA+=buffA[i];
		for(int j=0;j<buffB.length;j++) strB+=buffB[j];
		long dateA=Long.parseLong(strA);
		long dateB=Long.parseLong(strB);
		if(dateA<dateB) return -1;
		else if(dateA>dateB) return 1;
		return 0;
		}
	};

}

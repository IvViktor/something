package viktor.prog3;

public class Person{
	private String name;
	private String rank;
	private int duty_type;
	protected Person(String name,String rank,int type){
		this.name=name;
		this.rank=rank;
		this.duty_type=type;
	}
	protected int  getDuty(){
		return duty_type;
	}
	public String toString(){
		String str=rank+" "+name;
		return str;
		
	}
}

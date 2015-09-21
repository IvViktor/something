package flanagan.security;
import java.io.*;
import java.util.*;
import java.security.*;
public class PasswordManager{
	static final File dataFile = new File("/home/viktor/scripts-dir/userpassfile.sha");
	static final File tempFile = new File("/tmp/tempuserpass.tmp");
	static final String digestAlg="SHA-256";
	public static boolean changeFlag=false;
	public static void main(String args[]){
		String usage="SYNTAX ERROR. USAGE: java PasswordManager [-cr|-rm|-res] <username>";
		if((args.length<1)||(args.length>2)){System.out.println(usage);System.exit(1);}
		try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter output = new PrintWriter(System.out);){
		if(args[0].equals("-cr")) {addUser(args[1],input,output);}
		else if(args[0].equals("-rm")) {delUser(args[1],input,output);}
		else if(args[0].equals("-res")) {resetPass(args[1],input,output);}
		else authUser(args[0],input,output);
		if(changeFlag) updateFile();
		}catch (IOException e){System.out.println("Unable to open read/write streams");}
	}
	public static void addUser(String user,BufferedReader in,PrintWriter output){
		try(//BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		    Writer f = new FileWriter(tempFile);Reader rf = new FileReader(dataFile);){
			int j=0;
			Properties passfile = new Properties();
			passfile.load(rf);
	if(passfile.getProperty(user)!=null) throw new Exception("User "+user+" is already exist.");
			String line1;
			while(true){
			output.print("Enter new user password>");output.flush();
			line1=in.readLine();
			System.out.print("Retype your password>");
			String line2 = in.readLine();
			if(line1.equals(line2)) break;
			else if(j==1)throw new Exception("Try to add new user next time");
			else{ j++; output.println("Try again...");output.flush();continue;}
			}
			byte[] message = new byte[line1.length()*2];
			message=line1.getBytes();
			MessageDigest md =MessageDigest.getInstance(digestAlg);
			byte[] digestmessage = new byte[32];
			digestmessage=md.digest(message);
			passfile.setProperty(user,hexEncode(digestmessage));
			passfile.store(f,"Password data for "+user);
			changeFlag=true;
			output.println(user+" added");output.flush();
		} catch (Exception e){output.println(e);output.flush();}
	}
	public static void delUser(String user,BufferedReader in,PrintWriter output){
		try(//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    Reader f = new FileReader(dataFile);Writer fwr=new FileWriter(tempFile);){
			Properties passfile = new Properties();
			passfile.load(f);
		if(passfile.getProperty(user)==null) throw new Exception("User "+user+" doesn't exist");
			output.print("Enter user(administrator) password>");output.flush();
			String line=in.readLine();
			byte[] message = new byte[line.length()*2];
			message=line.getBytes();
			MessageDigest md = MessageDigest.getInstance(digestAlg);
			byte[] digestmessage = new byte[32];
			digestmessage=md.digest(message);
			line=hexEncode(digestmessage);
			String line1=passfile.getProperty(user);
			String line2=passfile.getProperty("administrator");
		if((line.equals(line1))||(line.equals(line2))){ passfile.remove(user);
			passfile.store(fwr,"");
			changeFlag=true;
			output.println(user+" removed.");output.flush();}
			else throw new Exception("Invalid password");
			} catch (Exception e){output.println(e);output.flush();}
	}
	public static void resetPass(String user,BufferedReader in,PrintWriter output){
			try(//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    Reader f = new FileReader(dataFile);Writer fwr=new FileWriter(tempFile);){
			Properties passfile = new Properties();
			passfile.load(f);
		if(passfile.getProperty(user)==null)throw new Exception("User "+user+" doesn't exist");
			output.print("Enter user(administrator) password>");output.flush();
			String line=in.readLine();
			byte[] message = new byte[line.length()*2];
			message=line.getBytes();
			MessageDigest md = MessageDigest.getInstance(digestAlg);
			byte[] digestmessage = new byte[32];
			digestmessage=md.digest(message);
			line=hexEncode(digestmessage);
			md.reset();
			String line1=passfile.getProperty(user);
			String line2=passfile.getProperty("administrator");
	if((line.equals(line1))||(line.equals(line2))){
			output.print("Enter new password>");output.flush();
			line=in.readLine();
			byte[] newpass = new byte[line.length()*2];
			newpass=line.getBytes();
			digestmessage=md.digest(newpass);
			//passfile.remove(user);
			passfile.setProperty(user,hexEncode(digestmessage));
			passfile.store(fwr,"Updated password for "+user);
			changeFlag=true;
			output.println("Password chanched.");output.flush();}
			else throw new Exception("Invalid password");
			} catch (Exception e){output.println(e);output.flush();}
	}
	public static void authUser(String user,BufferedReader in,PrintWriter output){
		try(//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    Reader f = new FileReader(dataFile);){
			Properties passfile = new Properties();
			passfile.load(f);
			String line1=passfile.getProperty(user);
			if(line1==null)throw new Exception("User "+user+" doesn't exist");
			int k=0;
			while(k<2){
			output.print("Enter your password>");output.flush();
			String line = in.readLine();
			byte[] message = new byte[line.length()*2];
			message=line.getBytes();
			MessageDigest md = MessageDigest.getInstance(digestAlg);
			byte[] digestmessage = new byte[32];
			digestmessage=md.digest(message);
			line=hexEncode(digestmessage);
			if(line.equals(line1)) {output.println("Hello "+user+"!! Current time is "+new Date()); output.flush(); return;}//throw new Exception("Password verified. OK.");}
			else{ k++;output.println("Invalid password");output.flush();continue;}
			}
			output.println("Invalid password, try to remind it...");output.flush();
		} catch (Exception e){output.println(e);output.flush();}
	}
	static String hexEncode(byte[] bytes){
		char[] digits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		StringBuffer s = new StringBuffer(bytes.length*2);
		for(int i=0;i<bytes.length;i++){
			byte b = bytes[i];
			s.append(digits[(b & 0xf0) >> 4]);
			s.append(digits[(b & 0x0f)]);
		}
		return s.toString();
	}
	public static void updateFile(){
		try(OutputStream out = new FileOutputStream(dataFile);
		    InputStream in = new FileInputStream(tempFile);){
			byte[] buffer = new byte[4096];
			int bytes_read=0;
			while((bytes_read=in.read(buffer))!=-1) out.write(buffer,0,bytes_read);
		} catch (IOException e){System.out.println("Unable to read/write tempfiles");}
	}
}

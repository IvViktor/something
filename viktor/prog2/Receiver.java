package viktor.prog2;
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
public class Receiver{
	private static boolean INITFLAG,MSGINFOFLAG,SENDFLAG,EXITFLAG;
	private static final int FRAGSIZE=750;
	private static final byte INFBYTE=100;
	private static final byte SERVBYTE=10;
	private static final String INITMSG="INIT";
	private static final String INITACK="INIT ACK";
	private static final String SENDMSG="SEND";
	private static final String SENDACK="SEND ACK";
	private static final String RECOK="REC READY";
	private static final String EXIT="EXIT";
	private static MessageDigest md;
	private static int DIGESTLENGTH;
	private static boolean[] FILESTAT;
	private static SocketAddress SENDER;
	private static File outFile;
	private static byte[] INPUTDATA;



	private static void sendServMsg(String msg,DatagramSocket s,SocketAddress remotehost){
		try{
		byte[] msgBytes=msg.getBytes();
		byte[] buff=new byte[msgBytes.length+1];
		buff[0]=SERVBYTE;
		for(int i=0;i<msgBytes.length;i++) buff[i+1]=msgBytes[i];
		DatagramPacket p = new DatagramPacket(buff,buff.length,remotehost);
		s.send(p);
		} catch	(IOException e){System.out.println("Unable to send "+msg);}
	}
	private static void recMsg(DatagramSocket s) throws IOException{
		byte[] buff = new byte[1500];
		DatagramPacket p = new DatagramPacket(buff,buff.length);
		s.receive(p);
		byte[] data=p.getData();
		int dataLength=p.getLength();
		if(data[0]==SERVBYTE){
			String msg=new String(data,1,dataLength);
			if(msg.startsWith(INITMSG)){
				SENDER=p.getSocketAddress();
				INITFLAG=true;
				System.out.println("INIT OK");
				sendServMsg(INITACK,s,SENDER);
			}
			else if(msg.startsWith("MSGINFO")){
				byte[] digest=new byte[DIGESTLENGTH];
				byte[] msgBytes=new byte[dataLength-DIGESTLENGTH-1];
				for(int i=0,j=1;i<msgBytes.length;i++,j++) msgBytes[i]=data[j];
				msg=new String(msgBytes);
				for(int i=0,j=dataLength-DIGESTLENGTH;i<DIGESTLENGTH;i++,j++){
					digest[i]=data[j];
				}
				md.reset();md.update(msgBytes);byte[] msgdigest=md.digest();
				if(Arrays.equals(digest,msgdigest)){
					MSGINFOFLAG=true;
					StringTokenizer st=new StringTokenizer(msg);
					st.nextToken();
					String fN=st.nextToken().substring(3);
					int fS=Integer.parseInt(st.nextToken().substring(3).trim());
					int bN=Integer.parseInt(st.nextToken().substring(3).trim());
					int bS=Integer.parseInt(st.nextToken().substring(3).trim());
					outFile=new File(System.getProperty("user.dir"),fN);
					FILESTAT=new boolean[bN];
					INPUTDATA=new byte[fS];
					System.out.println("MSGINFO OK");
				}
				else{
					 MSGINFOFLAG=false;
					 System.out.println("MSGINFO ERROR ");
				}
			}
			else if(msg.startsWith(SENDMSG)){
				SENDFLAG=true;
			}
			else if(msg.startsWith(EXIT)) EXITFLAG=true;
		}
		else if(data[0]==INFBYTE){
			byte[] digest=new byte[DIGESTLENGTH];
			byte[] msgbuff=new byte[32];
			byte[] databuff=new byte[dataLength-DIGESTLENGTH-32-1];
			for(int i=0,j=1;i<DIGESTLENGTH;i++,j++) digest[i]=data[j];
			for(int i=0,j=DIGESTLENGTH+1;i<32;i++,j++) msgbuff[i]=data[j];
			for(int i=0,j=DIGESTLENGTH+32+1;i<databuff.length;i++,j++) databuff[i]=data[j];
			md.reset();md.update(databuff);byte[] datadigest=md.digest();
			String msg=new String(msgbuff,0,32);
			int blockNum=Integer.parseInt(msg.substring(3).trim());
			if(Arrays.equals(digest,datadigest)){
				FILESTAT[blockNum]=true;
				System.out.println("Block #"+blockNum+" OK");
			for(int i=0,j=blockNum*FRAGSIZE;(i<databuff.length)&&(j<INPUTDATA.length);j++,i++) INPUTDATA[j]=databuff[i];
			}
			else{
				FILESTAT[blockNum]=false;
				System.out.println("Error in block # "+blockNum);
			}
		}
	}
	private static void checkRecieveFile(DatagramSocket s,SocketAddress addr){
		int count=0;
		for(int i=0;i<FILESTAT.length;i++){
			if(FILESTAT[i]!=true){ sendServMsg("ERR BN="+i,s,addr);count++;}
		}
		if(count==0){ 
			writeFile();
			sendServMsg(SENDACK,s,addr);
		}
	}
	private static void writeFile(){
		try(FileOutputStream out=new FileOutputStream(outFile)){
			out.write(INPUTDATA);
		} catch (IOException e){System.out.println("Cannot write data to file");}
	}
	public Receiver(int port){
		try(DatagramSocket s=new DatagramSocket(port)){
			md=MessageDigest.getInstance("MD5");
			DIGESTLENGTH=md.getDigestLength();
				while(!INITFLAG){
				recMsg(s);
				}
				while(!MSGINFOFLAG){
				recMsg(s);
				}
				sendServMsg(RECOK,s,SENDER);
				while(!SENDFLAG){
				recMsg(s);
				}
				while(!EXITFLAG){
				checkRecieveFile(s,SENDER);
				recMsg(s);
				}
		} catch (IOException e){System.out.println("Cannot open socket on port "+port);}
		catch (NoSuchAlgorithmException e){System.out.println("Unknown algorithm");}
	}
	public static void main(String args[]){
		if(args.length!=1){System.out.println("SYNTAX ERROR. Usage: Receiver <port number>");
					System.exit(1);}
		int port=Integer.parseInt(args[0]);
		new Receiver(port);
	}
}

package viktor.prog2;
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
public class Sender{
	private static boolean INITFLAG,MSGINFOFLAG,SENDFLAG;
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



	private static void sendServMsg(String msg,DatagramSocket s,InetAddress addr,int port){
		try{
		byte[] msgBytes=msg.getBytes();
		byte[] buff=new byte[msgBytes.length+1];
		buff[0]=SERVBYTE;
		for(int i=0;i<msgBytes.length;i++) buff[i+1]=msgBytes[i];
		DatagramPacket p = new DatagramPacket(buff,buff.length,addr,port);
		s.send(p);
		} catch	(IOException e){System.out.println("Unable to send "+msg);}
	}
	private static void sendMsgInfoMsg(String filename,int filesize,DatagramSocket s,InetAddress addr,int port){
		int blockNum;
		try{
		if((filesize%FRAGSIZE)>0) blockNum=(filesize/FRAGSIZE)+1;
		else blockNum=filesize/FRAGSIZE;
		String msg="MSGINFO FN="+filename+" FS="+filesize+" BN="+blockNum+" BS="+FRAGSIZE;
		byte[] msgBytes=msg.getBytes();
		byte[] buff=new byte[msgBytes.length+DIGESTLENGTH+1];
		buff[0]=SERVBYTE;
		md.reset(); md.update(msgBytes);byte[] digest=md.digest();
		for(int i=0;i<msgBytes.length;i++) buff[i+1]=msgBytes[i];
		for(int i=0;i<DIGESTLENGTH;i++) buff[i+1+msgBytes.length]=digest[i];
		DatagramPacket p = new DatagramPacket(buff,buff.length,addr,port);
		s.send(p);
	//	System.out.println("digest--  "+new String(digest));
		} catch (IOException e){System.out.println("Unable to send MSGINFO");}
	}
	private static int recMsg(DatagramSocket s,int timeout) throws SocketTimeoutException,IOException{
		byte[] buff = new byte[1500];
		DatagramPacket p = new DatagramPacket(buff,buff.length);
		s.setSoTimeout(timeout);
		s.receive(p);
	byte[] data=p.getData();
	if(data[0]==SERVBYTE){
			String msg=new String(data,1,data.length-1);
			if(msg.startsWith(INITACK)) INITFLAG=true;
			if(msg.startsWith(RECOK)) MSGINFOFLAG=true;
			if(msg.startsWith(SENDACK)) SENDFLAG=true;
			if(msg.startsWith("ERR MSGINFO")) MSGINFOFLAG=false;
			if(msg.startsWith("ERR BN=")){
				StringTokenizer st=new StringTokenizer(msg,"=");
				msg=st.nextToken();msg=st.nextToken();
				return Integer.parseInt(msg.trim());
			}
		}
		return -1;
	}
	private static void sendInfoMsg(DatagramSocket s,InetAddress addr,int port,byte[] data,int bN){
		byte[] buffer=new byte[(1+DIGESTLENGTH+data.length+32)];
		md.reset();md.update(data);byte[] digest=md.digest();
		byte[] msgbuff=new byte[32];
		String infomsg="BN="+bN;
		byte[] infomsgBytes=infomsg.getBytes();
		for(int i=0;i<infomsgBytes.length;i++) msgbuff[i]=infomsgBytes[i];
		buffer[0]=INFBYTE;
		for(int i=0;i<DIGESTLENGTH;i++) buffer[i+1]=digest[i];
		for(int i=0;i<msgbuff.length;i++) buffer[i+1+DIGESTLENGTH]=msgbuff[i];
		for(int i=0;i<data.length;i++) buffer[i+1+DIGESTLENGTH+msgbuff.length]=data[i];
		try{
			DatagramPacket p=new DatagramPacket(buffer,buffer.length,addr,port);
			s.send(p);
			System.out.println("Sending block # "+bN);
		}catch (IOException e){System.out.println("Unable to send BN="+bN);}
	}
	private static void fragmenter(DatagramSocket s,InetAddress addr,int port,byte[] data) throws IOException{
		byte[] buffer=new byte[FRAGSIZE];
		int bN;
		if((data.length%FRAGSIZE)>0) bN=data.length/FRAGSIZE+1;
		else bN=data.length/FRAGSIZE;
		for(int k=0;k<bN;k++){
			for(int i=k*FRAGSIZE,j=0;(i<(k+1)*FRAGSIZE)&&(i<data.length);i++,j++){
				 buffer[j]=data[i];
			}
			sendInfoMsg(s,addr,port,buffer,k);
		}
		sendServMsg(SENDMSG,s,addr,port);
		int retrNum=0;
		while(!SENDFLAG){
			try{
			int errBl=recMsg(s,1000);
			if(errBl>(-1)){
			System.out.println("Error in block # "+errBl);
			for(int i=errBl*FRAGSIZE,j=0;(i<(errBl+1)*FRAGSIZE)&&(i<data.length);i++,j++){
				buffer[j]=data[i];
			}
			sendInfoMsg(s,addr,port,buffer,errBl);
			}
			} catch (SocketTimeoutException e){ INITFLAG=false;MSGINFOFLAG=false;break;}
		}
	}
	public Sender(String path,String address,String portS){
		File opFile=new File(path);
		String fileName=opFile.getName();
		int fileSize=(int) opFile.length();
		StringTokenizer st=new StringTokenizer(address,".");
		byte[] addrBytes=new byte[4];
		addrBytes[0]=Byte.parseByte(st.nextToken());
		addrBytes[1]=Byte.parseByte(st.nextToken());
		addrBytes[2]=Byte.parseByte(st.nextToken());
		addrBytes[3]=Byte.parseByte(st.nextToken());
		int port =Integer.parseInt(portS);
		try(FileInputStream in=new FileInputStream(opFile);
			DatagramSocket s=new DatagramSocket();){
			InetAddress addr=InetAddress.getByAddress(addrBytes);
			md=MessageDigest.getInstance("MD5");
			DIGESTLENGTH=md.getDigestLength();
			byte[] data=new byte[fileSize];
			in.read(data);
			int retr=0;
			while(true){
			while(!INITFLAG){
				try{
				sendServMsg(INITMSG,s,addr,port);
				recMsg(s,1000);
				} catch (SocketTimeoutException e){if(retr==3){
				System.out.println("Cannot connect to host "+address);System.exit(1);}
				retr++;continue;
				}
			}
			retr=0;
			while(INITFLAG&&!MSGINFOFLAG){
			try{
			sendMsgInfoMsg(fileName,fileSize,s,addr,port);
			recMsg(s,2000);
			}catch (SocketTimeoutException e){if(retr==2){
				INITFLAG=false;break;}
				retr++;continue;}
			}
			if(MSGINFOFLAG) fragmenter(s,addr,port,data);
		if(SENDFLAG) {System.out.println("File send.");sendServMsg(EXIT,s,addr,port);break;}
			}
			System.out.println("Program closing...");
		} catch (IOException e){System.out.println("Unable to read file.");}
		//catch (UnknownHostException e){System.out.println("Unknown host");System.exit(5);}
       	catch (NoSuchAlgorithmException e){System.out.println("Unknown algorithm");System.exit(4);}
	}
	public static void main(String args[]){
		if(args.length!=3){System.out.println("SYNTAX ERROR.Usage: Sender <filename> <host address> <port>\nExample:Sender testfile.test 127.0.0.1 10000");System.exit(2);}
		new Sender(args[0],args[1],args[2]);
	}
}

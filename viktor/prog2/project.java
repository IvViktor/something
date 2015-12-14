package viktor.prog2;
import java.io.*;
import java.net.*;
import java.util.*;
import java.security;
/*в даном коде реализована передающая часть моего протокола для надежной передачи файлов постредством протокола UDP.Програма передает данные отправителю с любого доступного порта, служебные сообщения а также подтверждения програма получает на другом UDP порте. В програме реализован один поток исполнения.
*/
public class sender{
	//создаем три флага для своевременного выхода из разных циклов работы протокола 
	private static boolean INITFLAG, MSGINFOFLAG, SENDFLAG;
	private static final int[] FRAGSIZE={1400,750,400,200};
	private static final String INITMSG = "INIT";
	private static final String INITACK = "INIT ACK";
	private static final String SENTMSG = "SEND";
	private static final String EXITMSG = "OK EXIT";
	private static final String RECREADY = "REC READY";
	private static final String RECOK = "REC OK";
	private static final String ERRMSG = "ERR";
	private static MessageDigest md = MessageDigest.getInstanse("MD5");
	private static final int DIGESTLENGTH=md.getDigestLength();
	private static final byte servByte = 0b11110000;
	private static final byte infByte = 0b00001111;
	public sender(String filename, byte[] ipaddr, int port){
		InetAddress recipient = InetAddress.getByName(ipaddr);
		File sendfile = new File(filename);
		if((!sendfile.exists()) && (!sendfile.isFile())){
			 System.out.println("File <"+filename+"> doesn't exist.");
		}
		String fN = sendfile.getName();
		int fSize = (int) sendfile.length();
		byte[] filedata = new byte[fSize];
		try(FileOutputStream fileout = new FileOutputStream(filename);){
			fileout.read(filedata);
		} catch IOException(){
			System.out.println("Unable to read data from <"+filename+">");
		}
		try(DatagramSocket s = new DatagramSocket()){
			int r=0;int e=0;int ind=1;
			while(!INITFLAG){
				try{
				sendServMsg(INITMSG,recipient,port,s);
				e=recMsg(s,3000);
				}catch SocketTimeoutException(){
					r++;
					if(r==3){
						System.out.println("Unable to connect to host");
						System.exit(1);
					}
					continue;
				}
			}
			while(INITFLAG && !MSGINFOFLAG){
				try{
				sendMsgInfoMsg(s,recipient,port,fN,fSize,ind);
				e=recMsg(s,1000);
				} catch SocketTimeoutException(){continue;}
			}
			while(INITFLAG && MSGINFOFLAG && !SENDFLAG){
				fragmenter(filedata,s,recipient,port,fSize,ind);
			}
			if(SENDFLAG) sendServMsg(EXITMSG,recipient,port,s);
			
		} catch IOException(){System.out.println("Unable to open socket");}
	}
	private static void sendServMsg(String msg,InetAddress addr,int port,DatagramSocket s){
		byte[] stringarr = msg.getBytes();
		byte[] buff = new byte[(stringarr.length + 1)];
		buff[0]=servByte;
		for(int i=1;i<buff.length;i++) buff[i]=stringarr[i-1];
		DatagramPacket packet=new DatagramPacket(buff,buff.length,addr,port);
		try{s.send(packet);} catch IOException(){System.out.println("Unable to send "+msg);}
	}
	private static void sendInfoMsg(byte[] data,InetAddress addr,int port,DatagramSocket s,int bN){
		byte[] bInfo=new byte[32];
		byte[] packdata = new byte[(data.length+DIGESTLENGTH+1+bInfo.length)];
		String blockNum = "BLOCK="+bN;
		byte[] stringByte=blockNum.getBytes();
		for(int i=0;i<stringByte.length;i++) bInfo[i]=stringByte[i];
		packdata[0]=infByte;
		md.reset();md.update(data);
		byte[] datadigest=md.digest();
		for(int k=0;k<bInfo.length;k++) packdata[k+1]=bInfo[k];
		for(int i=0;i<datadigest.length;i++) packdata[i+bInfo.length+1]=datadigest[i];
		for(int j=0;j<data.length;j++) packdata[j+DIGESTLENGTH+bInfo.length+1]=data[j];
		DatagramPacket packet=new DatagramPacket(packdata,packdata.length,addr,port);
		try{s.send(packet);} catch IOException(){System.out.println("Unable to send block #"+bN);}
	}
	private static int recMsg(DatagramSocket s,int timeout) throws SocketTimeoutException{
		byte[] buffer=new byte[1500];
		DatagramPacket p = new DatagramPacket(buffer,buffer.length);
		s.setSoTimeout(timeout);
		s.receive(p);
		byte[] data=p.getData();
		String msg = new String(data,1,data.length-1);
		if(msg.startsWith(INITACK)) {INITFLAG=true;return -1}
		else if(msg.startsWith(RECREADY)){ MSGINFOFLAG=true;return -1}
		else if(msg.startsWith(RECOK)){ SENDFLAG=true;return -1}
		else if(msg.startsWith("ERR")){
			StringTokenizer st = new StringTokenizer(msg);
			String token = st.nextToken();
			token=st.nextToken();
			if(token.startsWith("MSGINFO")) {MSGINFOFLAG=false;return -1}
			else if(token.startsWith("BLOCK")){
				int bN=Integer.parseInt(token.substring(6));
				return bN;
			}
		}
		return -1;
	}
	private static void sendMsgInfoMsg(DatagramSocket s,InetAddress addr,int port,String fN,int fSize, int index){
		int blockNum;
		if((fSize%FRAGSIZE[index])>0) blockNum=(fSize/FRAGSIZE[index])+1;
		else blockNum=fSize/FRAGSIZE[index];
		String msgInfo="MSGINFO FN="+fN+" FS="+fSize+" BN="+blockNum+" BS="+FRAGSIZE[index];
		byte[] data=msgInfo.getBytes();
		md.reset();md.update(data);byte[] digest = md.digest();
		byte[] packdata=new byte[data.length+DIGESTLENGTH+1];
		packdata[0]=servByte;
		for(int i=0;i<digest.length;i++) packdata[i+1]=digest[i];
		for(int j=0;j<data.length;j++) packdata[j+DIGESTLENGTH+1]=data[j];
		DatagramPacket p = new DatagramPacket(packdata,packdata.length,addr,port);
		try{s.send(p);} catch IOException(){System.out.println("Unable to send MSGINFO");}
    	}
	private static void fragmenter(byte[] data, DatagramSocket s,InetAddress addr,int port,int fSize,int index){
		int bNum;
		int k=0;
		int size=FRAGSIZE[index];
		if((fSize%size)>0) blockNum=(fSize/size)+1;
		else bNum=fSize/size;
		byte[] buff=new byte[size];
		while(k<=bNUm){
			for(int i=k*size,int j=0;i<(k+1)*size;i++,j++){ 
				if(i>fSize) break; buff[j]=data[i];
			}
			sendInfoMsg(buff,addr,port,s,k);
		}
		while(!SENDFLAG){
			try{
				int q=recMsg(s,10000);
				if(q>=0){
					for(int i=q*size,int j=0;i<(q+1)*size;i++,j++){
						if(i>fSize) break;
						buff[j]=data[i];
					}
				sendInfoMsg(buff,addr,port,s,q);
				}
			} catch SocketTimeoutException(){sendServMsg(SENTMSG,addr,port,s);continue;}
		}
	}
	/*private static void sendFragInfoMsg(DatagramSocket s,InetAddress addr,int port,int bNumber,int bSize, int index){
		int blockNum;
		int size=FRAGSIZE[index];
		if((bSize%FRAGSIZE[index])>0) blockNum=(bSize/FRAGSIZE[index])+1;
		else blockNum=fSize/FRAGSIZE[index];
		String msgInfo="FRAGINFO FRN="+bNumber+" FRS="+bSize+" BN="+blockNum+" BS="+size;
		byte[] data=msgInfo.getBytes();
		md.reset();md.update(data);byte[] digest = md.digest();
		byte[] packdata=new byte[data.length+DIGESTLENGTH+1];
		packdata[0]=servByte;
		for(int i=0;i<digest.length;i++) packdata[i+1]=digest[i];
		for(int j=0;j<data.length;j++) packdata[j+DIGESTLENGTH+1]=data[j];
		DatagramPacket p = new DatagramPacket(packdata,packdata.length,addr,port);
		try{s.send(p);} catch IOException(){
			System.out.println("Unable to send FRAGINFO for "+bNumber+" fragment");}
    	}*/

}

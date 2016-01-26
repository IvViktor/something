package viktor.prog3;
import java.io.*;
import javax.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.*;
import org.docx4j.openpackaging.parts.WordprocessingML.*;
import org.docx4j.wml.Body;
import java.util.*;
public class TestDoc{
	public static void main(String args[]){
		String usage="SYNTAX ERROR.USAGE TestDoc <filename> [<row num> <col num> <text>]";
		int row=0,col=0;
		if(args.length<1){System.out.println(usage);System.exit(1);}
		if(args.length>=3){
			row=Integer.parseInt(args[1])-1;
			col=Integer.parseInt(args[2])-1;
		}
		try{
			WordprocessingMLPackage wmlPack=WordprocessingMLPackage.load(new File(args[0]));
			org.docx4j.wml.Tbl table1=getTable(wmlPack);
	//		org.docx4j.wml.Tbl table1=(org.docx4j.wml.Tbl)((JAXBElement) body.getContent().get(0)).getValue();
/*			org.docx4j.wml.Tr tableRow=(org.docx4j.wml.Tr) table1.getContent().get(7);
			System.out.println("tr OK");
			org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement) tableRow.getContent().get(0)).getValue();
			System.out.println("tc OK");
			org.docx4j.wml.P p=(org.docx4j.wml.P) tC.getContent().get(0);
			System.out.println("par OK");
			org.docx4j.wml.R r=(org.docx4j.wml.R) p.getContent().get(0);
			System.out.println("run OK");
			org.docx4j.wml.Text text=(org.docx4j.wml.Text)((JAXBElement) r.getContent().get(0)).getValue();
			System.out.println("text OK");
			String line=text.getValue();*/
			if(args.length==4){
			setTextInTable(table1,row,col,args[3]);}
			System.out.println(getTextFromTable(table1,row,col));
			wmlPack.save(new File(args[0]));
			wmlPack.save(new File("probe.docx"));
			System.out.println("File saved");
			System.out.println("Program finished");
		} catch (Exception e){System.out.println("Error while reading");e.printStackTrace();}
	}
	protected static String getTextFromTable(Object o,int row,int col) throws Exception{
		if(o instanceof org.docx4j.wml.Tbl){
			Object trow=((org.docx4j.wml.Tbl) o).getContent().get(row);
			return getTextFromTable(trow,row,col);
		}
		else if(o instanceof org.docx4j.wml.Tr){
			Object tcell=((org.docx4j.wml.Tr) o).getContent().get(col);
			return getTextFromTable(tcell,row,col);
		}
		else if(o instanceof org.docx4j.wml.P){
			String line=new String();
			for(Object o1 : ((org.docx4j.wml.P) o).getContent()){
				line+=getTextFromTable(o1,row,col);
			}
			return line;
		}
		else if(o instanceof org.docx4j.wml.R){
			for(Object o1 :((org.docx4j.wml.R)o).getContent()){
				return getTextFromTable(o1,row,col);
			}
		}
		else if(o instanceof javax.xml.bind.JAXBElement){
			if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Tc")){
			org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement)o).getValue();
				for(Object o1 : tC.getContent()){
					return getTextFromTable(o1,row,col);
				}
			}
		else if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
			org.docx4j.wml.Text text=(org.docx4j.wml.Text) ((JAXBElement)o).getValue();
			 return text.getValue();
		}
		}
		else throw new Exception("Unknown type of data");
		return null;
	}
	protected static void setTextInTable(Object o,int row,int col,String text) throws Exception{
		if(o instanceof org.docx4j.wml.Tbl){
			Object trow=((org.docx4j.wml.Tbl) o).getContent().get(row);
			setTextInTable(trow,row,col,text);
		}
		else if(o instanceof org.docx4j.wml.Tr){
			Object tcell=((org.docx4j.wml.Tr) o).getContent().get(col);
			setTextInTable(tcell,row,col,text);
		}
		else if(o instanceof org.docx4j.wml.P){
		/*	org.docx4j.wml.P par=(org.docx4j.wml.P) o;
			Object runNode=par.getContent().get(0);
			setTextInTable(runNode,row,col,text);
			par.getContent().clear();
			par.getContent().add(runNode);*/
			for(Object o1 : ((org.docx4j.wml.P) o).getContent()){
				setTextInTable(o1,row,col,text);
			}
		}
		else if(o instanceof org.docx4j.wml.R){
			for(Object o1 : ((org.docx4j.wml.R) o).getContent()){
				setTextInTable(o1,row,col,text);
			}
		}
		else if(o instanceof javax.xml.bind.JAXBElement){
			if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Tc")){
			org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement)o).getValue();
				for(Object o1 : tC.getContent()){
					setTextInTable(o1,row,col,text);
				}
			}
		else if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
			org.docx4j.wml.Text t=(org.docx4j.wml.Text) ((JAXBElement)o).getValue();
			 t.setValue(text);
		}
		}
		else throw new Exception("Unknown type of data");
	}
	protected static org.docx4j.wml.Tbl getTable(WordprocessingMLPackage pack){
		return (org.docx4j.wml.Tbl)((JAXBElement) pack.getMainDocumentPart().getContent().get(0)).getValue();
	}
}

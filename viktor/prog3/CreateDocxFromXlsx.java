package viktor.prog3;
import java.util.Vector;
import java.util.List;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.CTSst;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import javax.xml.bind.JAXBElement;
import java.util.Properties;
import java.io.*;

public class CreateDocxFromXlsx{
	private static Properties p;
	public static void main(String args[]){
		try(Reader inProp=new FileReader("XlsxToDocx.prop")){
		p=new Properties();
		p.load(inProp);
		String xlsxFile=p.getProperty("xlsxFile","graphik.xlsx");
		String docxTemplate=p.getProperty("templateFile","nakaz-example.docx");
		String raportTemplate=p.getProperty("raportTemplate","raport-example.docx");
		String monthNum=p.getProperty("monthNum");
		String yearNum=p.getProperty("yearNum");
		String monthName=p.getProperty("monthName");
		int XcolS=getVal("colFirst");
		int XcolL=getVal("colLast");
		SpreadsheetMLPackage pack=XLSParser.getXLPac(xlsxFile);
		List<Row> list=XLSParser.getSheetData(pack,0);
		CTSst strings=XLSParser.getStringContent(pack);
     Vector<Vector> bp010=getPerson(list,strings,getVal("bp010rowSXL"),getVal("bp010rowEXL"),XcolS,XcolL);
     Vector<Vector> bp120=getPerson(list,strings,getVal("bp120rowSXL"),getVal("bp120rowEXL"),XcolS,XcolL);
     Vector<Vector> bp130=getPerson(list,strings,getVal("bp130rowSXL"),getVal("bp130rowEXL"),XcolS,XcolL);
     Vector<Vector> bp180=getPerson(list,strings,getVal("bp180rowSXL"),getVal("bp180rowEXL"),XcolS,XcolL);
     Vector<Vector> bp181=getPerson(list,strings,getVal("bp181rowSXL"),getVal("bp181rowEXL"),XcolS,XcolL);
     Vector<Vector> bp380=getPerson(list,strings,getVal("bp380rowSXL"),getVal("bp380rowEXL"),XcolS,XcolL);
     Vector<Vector> bp230=getPerson(list,strings,getVal("bp230rowSXL"),getVal("bp230rowEXL"),XcolS,XcolL);
     Vector<Vector> bp330=getPerson(list,strings,getVal("bp330rowSXL"),getVal("bp330rowEXL"),XcolS,XcolL);
     Vector<Vector> bp350=getPerson(list,strings,getVal("bp350rowSXL"),getVal("bp350rowEXL"),XcolS,XcolL);
     Vector<Vector> bp310=getPerson(list,strings,getVal("bp310rowSXL"),getVal("bp310rowEXL"),XcolS,XcolL);
     Vector<Vector> bp450=getPerson(list,strings,getVal("bp450rowSXL"),getVal("bp450rowEXL"),XcolS,XcolL);
		WordprocessingMLPackage wordpack=WordprocessingMLPackage.load(new File(docxTemplate));
		WordprocessingMLPackage raportPack=WordprocessingMLPackage.load(new File(raportTemplate));
		Tbl docxTable=TestDoc.getTable(wordpack);
		int colNumber=getVal("colNum")-1;
		int bp010rD=getVal("bp010rowD")-1;
		int bp120rD=getVal("bp120rowD")-1;
		int bp130rD=getVal("bp130rowD")-1;
		int bp180rD=getVal("bp180rowD")-1;
		int bp181rD=getVal("bp181rowD")-1;
		int bp380rD=getVal("bp380rowD")-1;
		int bp230rD=getVal("bp230rowD")-1;
		int bp330rD=getVal("bp330rowD")-1;
		int bp310rD=getVal("bp310rowD")-1;
		int bp350rD=getVal("bp350rowD")-1;
		int bp450rD=getVal("bp450rowD")-1;
		int bp910rD=getVal("bp910rowD")-1;
		Vector<Person> bplist;
		File monthDir=new File(monthName);
		monthDir.mkdir();
		for(int i=0,j=1;i<(XcolL-XcolS);i++,j++){
			String date=j+"."+monthNum+"."+yearNum;
			TestDoc.setTextInTable(docxTable,0,0,date);
			bplist=bp010.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,bp010rD,colNumber,bplist.get(k).toString());
			}
			bplist=bp120.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,bp120rD,colNumber,bplist.get(k).toString());
			}
			bplist=bp130.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,bp130rD+k,colNumber,bplist.get(k).toString());
			}
			bplist=bp180.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,bp180rD,colNumber,bplist.get(k).toString());
			}
			bplist=bp181.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,bp181rD,colNumber,bplist.get(k).toString());
			}
			bplist=bp380.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,(bp380rD+k),colNumber,bplist.get(k).toString());
			}
			bplist=bp230.get(i);
			for(int k=0;k<bplist.size();k++){
			TestDoc.setTextInTable(docxTable,(bp230rD+k),colNumber,bplist.get(k).toString());
			}
			bplist=bp330.get(i);
			for(int k=0;k<bplist.size();k++){
//			int q=0;
//			if(bplist.get(k).getDuty()==2) q=1;
   			TestDoc.setTextInTable(docxTable,bp330rD+k,colNumber,bplist.get(k).toString());
			}
			bplist=bp310.get(i);
			for(int k=0;k<bplist.size();k++){
			int q=0;
			if(bplist.get(k).getDuty()==2) q=1;
			TestDoc.setTextInTable(docxTable,bp310rD,colNumber+q,bplist.get(k).toString());
			}
			bplist=bp350.get(i);
			for(int k=0;k<bplist.size();k++){
			int q=0;
			if(bplist.get(k).getDuty()==2) q=1;
			TestDoc.setTextInTable(docxTable,bp350rD,colNumber+q,bplist.get(k).toString());
			}
			bplist=bp450.get(i);
			for(int k=0;k<bplist.size();k++){
			int q=0;
			if(bplist.get(k).getDuty()==2) q=1;
			TestDoc.setTextInTable(docxTable,bp450rD,colNumber+q,bplist.get(k).toString());
			TestDoc.setTextInTable(docxTable,bp910rD,colNumber+q,bplist.get(k).toString());
			}
			replaceTable(docxTable,raportPack);
			File newDir=new File(monthDir,date);
			newDir.mkdir();
			System.out.println("Saving file наказ_"+date);
			wordpack.save(new File(newDir,("наказ_"+date+".docx")));
			System.out.println("Saving file рапорт_"+date);
			raportPack.save(new File(newDir,("рапорт_"+date+".docx")));

		}
		System.out.println("Файлы успешно созданы");
		} catch(Exception e){System.out.println("Error while reading xlsx document");
					e.printStackTrace();System.exit(1);}
	}
	protected static Vector<Vector> getPerson(List<Row> rowList,CTSst strings,int rowS,int rowE,int cellS,int cellE){
		Vector<Vector> biglist=new Vector<Vector>();
		try{
		for(int i=(cellS-1);i<cellE;i++){
		Vector<Person> smalList=new Vector<Person>();
			for(int j=(rowS-1);j<rowE;j++){
				int ind=XLSParser.getStringIndex(rowList,j,i);
				if((ind==1)||(ind==2)||((ind>=80)&&(ind<=90))){
					String name,rank;
					int sInd=XLSParser.getStringIndex(rowList,j,cellS-2);
					if(sInd==0) continue;
					else name=XLSParser.getShareString(strings,sInd);
					sInd=XLSParser.getStringIndex(rowList,j,cellS-3);
					if(sInd==0) rank="прац.ЗСУ";
					else rank=XLSParser.getShareString(strings,sInd);
					smalList.add(new Person(name,rank,ind));
				}
			}
			biglist.add(smalList);
		}
		}catch (Docx4JException e){System.out.println("Error while reading bloc "+
						(rowE-rowS)+"X"+(cellE-cellS));}
		return biglist;
	}
	private static int getVal(String name){
		String str=p.getProperty(name);
		if(str==null){System.out.println("Property file syntax error."+
						" Not defined all properties");System.exit(10);}
		return Integer.parseInt(str);
	}
	private static void replaceTable(Tbl table,WordprocessingMLPackage target){
		int k=-1;
		List<Object> list=target.getMainDocumentPart().getContent();
		for(Object o : list){
			k++;
			if(o instanceof JAXBElement){
			if(((JAXBElement) o).getDeclaredType().getName().equals("org.docx4j.wml.Tbl")){
				list.set(k,table);
				break;
			}
			else continue;
			}
			else continue;
		}
	}
}

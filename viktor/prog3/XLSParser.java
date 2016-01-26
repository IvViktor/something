package viktor.prog3;
import java.io.File;
import org.xlsx4j.sml.CTSst;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.Worksheet;
import org.xlsx4j.sml.SheetData;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.xlsx4j.exceptions.Xlsx4jException;
import java.util.List;
public class XLSParser{
	protected static SpreadsheetMLPackage getXLPac(String file) throws Docx4JException{
		SpreadsheetMLPackage pack;
		pack=SpreadsheetMLPackage.load(new File(file));
		return pack;
	}
  protected static List<Row> getSheetData(SpreadsheetMLPackage pack,int sheetNum) throws Docx4JException,Xlsx4jException{
	return pack.getWorkbookPart().getWorksheet(sheetNum).getContents().getSheetData().getRow();
	}
	protected static CTSst getStringContent(SpreadsheetMLPackage pack) throws Docx4JException{
		return pack.getWorkbookPart().getSharedStrings().getContents();
	}
     protected static int getStringIndex(List<Row> list,int rowNum,int colNum) throws Docx4JException{
		Cell cell=list.get(rowNum).getC().get(colNum);
		String line=cell.getV();
		if(line!=null) return Integer.parseInt(line);
		return 0;
	}
	protected static String getShareString(CTSst strTab,int val){
		return strTab.getSi().get(val).getT().getValue();
	}
	public static void main(String args[]){
		if(args.length!=1){System.out.println("SYNTRAX ERROR");System.exit(1);}
		try{
			SpreadsheetMLPackage pack=getXLPac(args[0]);
			List<Row> list=getSheetData(pack,0);
			CTSst strings=getStringContent(pack);
			int a=getStringIndex(list,9,2);
			 System.out.println(getShareString(strings,a));
		}catch (Exception e){e.printStackTrace();}
	}
}

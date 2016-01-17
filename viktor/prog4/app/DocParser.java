package viktor.prog4.app;

import viktor.prog4.api.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.*;

import javax.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.BooleanDefaultTrue;

import org.docx4j.wml.Tbl;

public class DocParser{
	private List<LineItem> itemList;
	private StringBuilder shareLines;

	public DocParser(){
		this.itemList=new LinkedList<>();
		this.shareLines=new StringBuilder();
	}

	public void addItem(LineItem item){ itemList.add(item);}


	public List<LineItem> parseContent(List<Object> body){
		int contentIndex=-1;
		for(Object o : body){
			contentIndex++;
			if(o instanceof JAXBElement){
			if(((JAXBElement)o).getDeclaredType().getName().equals("org.docx4j.wml.Tbl")){
				Tbl table=(org.docx4j.wml.Tbl)((JAXBElement) o).getValue();
				parseWMLTable(table, contentIndex);	
			}
			}
			else if(o instanceof org.docx4j.wml.P){
				parseParagraph((P) o, contentIndex);
			} 
		}
		return this.itemList;
	}

	public List<Object> getFileContent(java.io.File file) throws Docx4JException{
	//	try{
		WordprocessingMLPackage wmlPack=WordprocessingMLPackage.load(file);
		return wmlPack.getMainDocumentPart().getContent();	
	//	}catch (Docx4JException e){System.out.println("Cannot load docx file "+file.getName());
	//									 e.printStackTrace();}
	}	

	public void parseWMLTable(org.docx4j.wml.Tbl table,int upperId){
		StringBuilder tableText=new StringBuilder();
		for(Object o : table.getContent()){
		  if(o instanceof org.docx4j.wml.Tr){
			for(Object o1: ((org.docx4j.wml.Tr) o).getContent()){
			  if(o1 instanceof JAXBElement){
			   if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Tc")){
			     org.docx4j.wml.Tc tC=(org.docx4j.wml.Tc)((JAXBElement)o1).getValue();
				for(Object o2 : tC.getContent()){
				  if(o2 instanceof org.docx4j.wml.P){
				   for(Object o3 : ((org.docx4j.wml.P) o2).getContent()){
					if(o3 instanceof org.docx4j.wml.R){
					  for(Object o4 :((org.docx4j.wml.R)o3).getContent()){
						if(((JAXBElement)o4).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
						  org.docx4j.wml.Text text=(org.docx4j.wml.Text) ((JAXBElement)o4).getValue();
			 			  tableText.append(text.getValue());
						  tableText.append(" ");
						}
					  }
					}
				   }
				  }
				}
			   }
			  }
			}
		  }
		}
		if(tableText.length()>0){
			StringBuilder idStr=new StringBuilder();
			idStr.append(upperId);
			addItem(new TableText(tableText.toString(),idStr.toString()));
		} 
	}

	public void parseParagraph(org.docx4j.wml.P par,int upperId){
		StringBuilder idStr=new StringBuilder();
		StringBuilder items=shareLines;
		//boolean isPCenter=isCenterAlign(par);
		boolean isPoint=false;///
		List<String> stringList=new LinkedList<>();
		for(Object o : par.getContent()){
		  if(o instanceof org.docx4j.wml.R){
			org.docx4j.wml.R run=(org.docx4j.wml.R)o;//((JAXBElement)o).getValue();
			//boolean isRBold=isRunBold(run);
			for(Object o1 : run.getContent()){
			 if(o1 instanceof JAXBElement){
			  if(((JAXBElement)o1).getDeclaredType().getName().equals("org.docx4j.wml.Text")){
			  org.docx4j.wml.Text text=(org.docx4j.wml.Text) ((JAXBElement)o1).getValue();
				String textContent=text.getValue().trim();
				textAnalyser(stringList,textContent);
			  }
			 }
			}
		  // }
		  }
		}
		if(!stringList.isEmpty()){
		  boolean alreadyHasItem=false;
		  int subInd=0;
		  int stringListSize=stringList.size();
		  for(String str : stringList){
			subInd++;
			if(isPoint){
				////
			}
			else if(str.matches("^\\p{javaUpperCase}.*")){
				if(str.matches(".+\\.$")){
				  if(items.length()>0){
				    items.append(str+" ");
				    addItem(new Sentence(items.toString(),upperId+"."+subInd,false));
				    alreadyHasItem=true;
				    items.delete(0,items.length());
				    continue;
				  }
				  if((!alreadyHasItem)&&(subInd==stringListSize)){
					addItem(new Title(str,upperId+"."+subInd));
					continue;
				  }
				  else{
					addItem(new Sentence(str,upperId+"."+subInd,false));
					alreadyHasItem=true;
					continue;
				  }
				}
				else if((str.matches(".*[:]$"))&&subInd==stringListSize){
			           addItem(new Bullet(upperId+"."+subInd+".BB"));
 				addItem(new BulletHeader(str,upperId+"."+subInd+".BH"));
				   continue;
				}
				else{
					if((!alreadyHasItem)&&(subInd==stringListSize)){
						addItem(new Title(str,upperId+"."+subInd));
						continue;
					}
					else{
						items.append(str+" ");
						continue;
					}
				}
			}
			else{
				if(str.matches(".*\\.$")){
				    items.append(str+" ");
  				    addItem(new Sentence(items.toString(),upperId+"."+subInd,false));
				    alreadyHasItem=true;
				    items.delete(0,items.length());
				    continue;
				}
				else if((str.matches(".*[:]$"))&&(subInd==stringListSize)){
				    items.append(str);
				    addItem(new Bullet(upperId+"."+subInd+".BB"));
 				addItem(new BulletHeader(items.toString(),upperId+"."+subInd+".BH"));
				    items.delete(0,items.length());
				    continue;
				}
				else{
					items.append(str+" ");
					continue;
				}
			}
		  }
		}
	}
	
	private boolean isCenterAlign(org.docx4j.wml.P par){
		org.docx4j.wml.PPr parProp=par.getPPr();
		org.docx4j.wml.Jc parAlign=parProp.getJc();
		return (parAlign.getVal().equals(JcEnumeration.CENTER));
	}
	
	private boolean isRBold(org.docx4j.wml.R run){
		org.docx4j.wml.RPr runPr=run.getRPr();
		return runPr.getB().isVal();
	}

	private void textAnalyser(List<String> list,String text){
		Pattern regex=Pattern.compile("\\b.{3,}?(?:[.?;!]|(?:[:]\\s*$)|$)(?=\\s|$)");
	//	Pattern regex=Pattern.compile(".*");
		Matcher regexMatcher=regex.matcher(text);
		while(regexMatcher.find()){
			String completeSentence=regexMatcher.group().trim();
			if(completeSentence.length()>0) list.add(completeSentence);
		}
	}
	public static void main(String args[])throws Docx4JException{
		DocParser dp=new DocParser();
		List<Object> obList=dp.getFileContent(new java.io.File(args[0]));
		List<LineItem> list=dp.parseContent(obList);
		for(LineItem li : list){
			System.out.println(li.getName()+"    "+li.getId()+"    "+li.getValue());
		}
	}
}

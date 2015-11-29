package viktor.prog1;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;
public class MainFrame extends JFrame implements ActionListener{
	//Checkbox sort,search;
	JTextField name,sname,fname,num;
	JComboBox<String> fdateDay,fdateMon,tdateDay,tdateMon;
	JComboBox fdateYear,tdateYear;
	java.util.Vector<Person> datalist;
	TableModel model;
	String[] days={"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	String[] month={"01","02","03","04","05","06","07","08","09","10","11","12"};
	int[] years=new int[30];
	//Choice chmenu;
	//Panel searchPan;
	//java.util.Vector<Person> list;
	//String fileName="testfile.test";
	//ErrorDialog errD;
	/*public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){createGUI();}
		});
	}
	private static void createGUI(){
		MainFrame frame = new MainFrame();
		frame.pack();
		frame.setTitle("Учетник 2015");
		frame.setVisible(true);
	}*/
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("exit")) this.dispose();
		/*if(ae.getActionCommand().equals("browse")){
		FileDialog fd = new FileDialog(this,"Выберите файл с информацией",FileDialog.LOAD);
		fd.setVisible(true);
	if((fd.getDirectory()!=null)&&(fd.getFile()!=null)){
		fileName=fd.getDirectory()+fd.getFile();
		try{
			list=Person.getPersonsList(fileName);
		} catch (IOException e){
			String msg="Ошибка чтения файла: "+fileName;
			errD=new ErrorDialog(this,"Ошибка ввода/вывода",true,msg);}
		 catch (IllegalArgumentException e){
			String msg="Неправильный формат данных в файле: "+fileName;
			errD=new ErrorDialog(this,"Ошибка чтения данных!!!",true,msg);}
		}
		}*/
		if(ae.getActionCommand().equals("start")){
			if((sname.getText().length()>0)&&(num.getText().length()>0)){
			String fullName=sname.getText()+" "+name.getText()+" "+fname.getText();
			String from_date=fdateDay.getSelectedItem()+"."+fdateMon.getSelectedItem()+"."+fdateYear.getSelectedItem();
			String expire=tdateDay.getSelectedItem()+"."+fdateMon.getSelectedItem()+"."+fdateYear.getSelectedItem();
			Person.addUser(datalist,fullName,num.getText(),from_date,expire);
			this.dispose();
			}
		else new ErrorDialog(this,"Ошыбка",true,"ВНИМАНИЕ! Обязательные поля не заполнены!");
		}
/*			if(fileName==null){
			String msg="Файл не найден, выберите файл с помощью кнопки <Выберите файл>";
			errD = new ErrorDialog(this,"ОШИБКА!",true,msg);
			}
			else{
		try{
			list=Person.getPersonsList(fileName);
		} catch (IOException e){
			String msg="Ошибка чтения файла: "+fileName;
			errD=new ErrorDialog(this,"Ошибка ввода/вывода",true,msg);}

			if(sort.getState()){
				 list=Person.sort(list,chmenu.getSelectedIndex());
				TableFrame table = new TableFrame();//"Список с допусками",list,getHeaders());
			}
			else if(search.getState()){
				String pattern = searchText.getText();
				if(pattern.length()==0){ 
				errD=new ErrorDialog(this,"Ошыбка поиска",true,"Введите поисковый"+
				" запрос в поле <Поиск>");
				}
				else{
			Vector<Person> slist=Person.search(list,pattern,chmenu.getSelectedIndex());
			TableFrame table = new TableFrame();//"Список с допусками",slist,getHeaders());
				}
			}
			//for(int i=0;i<list.size();i++) System.out.println(list.get(i));
			}
		}*/
	}
	/*public void itemStateChanged(ItemEvent ie){
		if(sort.getState()) searchPan.setVisible(false);
		else if(search.getState()) searchPan.setVisible(true);
	}*/
	protected MainFrame(Vector<Person> datalist,TableModel model){
		super("Добавление записи");
		this.datalist=datalist;
		this.model=model;
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){ }//his.dispose();}
		});
		GridBagLayout gbag = new GridBagLayout();
		setLayout(gbag);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.ipadx=10;gbc.ipady=10;
		gbc.insets=new Insets(5,5,5,5);
		JLabel snameLab=new JLabel("Фамилия*");
		gbc.gridx=1;gbc.gridy=1;gbc.gridwidth=1;
		gbag.setConstraints(snameLab,gbc);
		sname=new JTextField(30);
		gbc.gridx=2;gbc.gridwidth=3;
		gbag.setConstraints(sname,gbc);
		JLabel nameLab=new JLabel("Имя");
		gbc.gridx=1;gbc.gridy=2;gbc.gridwidth=1;
		gbag.setConstraints(nameLab,gbc);
		name=new JTextField(30);
		gbc.gridx=2;gbc.gridwidth=3;
		gbag.setConstraints(name,gbc);
		JLabel fnameLab=new JLabel("Отчество");
		gbc.gridx=1;gbc.gridy=3;gbc.gridwidth=1;
		gbag.setConstraints(fnameLab,gbc);
		fname=new JTextField(30);
		gbc.gridx=2;gbc.gridwidth=3;
		gbag.setConstraints(fname,gbc);
		JLabel numLab=new JLabel("Номер допуска*");
		gbc.gridx=1;gbc.gridy=4;gbc.gridwidth=1;
		gbag.setConstraints(numLab,gbc);
		num=new JTextField(10);
		gbc.gridx=2;gbag.setConstraints(num,gbc);
		JLabel fdateLab=new JLabel("Дата получения*");
		gbc.gridx=1;gbc.gridy=5;gbag.setConstraints(fdateLab,gbc);
		for(int i=0;i<years.length;i++) years[i]=2005+i;
		fdateDay=new JComboBox<String>();
		for(int i=0;i<days.length;i++) fdateDay.addItem(days[i]);
		fdateDay.setSelectedIndex(0);
		gbc.gridx=2;gbag.setConstraints(fdateDay,gbc);
		fdateMon=new JComboBox<String>();
		for(int i=0;i<month.length;i++) fdateMon.addItem(month[i]);
		fdateMon.setSelectedIndex(0);
		gbc.gridx=3;gbag.setConstraints(fdateMon,gbc);
		fdateYear=new JComboBox();
		for(int i=0;i<years.length;i++) fdateYear.addItem(years[i]);
		fdateYear.setSelectedIndex(10);
		gbc.gridx=4;gbag.setConstraints(fdateYear,gbc);
		JLabel tdateLab=new JLabel("Дата истечения*");
		gbc.gridx=1;gbc.gridy=6;gbag.setConstraints(tdateLab,gbc);
		tdateDay=new JComboBox<String>();
		for(int i=0;i<days.length;i++) tdateDay.addItem(days[i]);
		tdateDay.setSelectedIndex(0);
		gbc.gridx=2;gbag.setConstraints(tdateDay,gbc);
		tdateMon=new JComboBox<String>();
		for(int i=0;i<month.length;i++) tdateMon.addItem(month[i]);
		tdateMon.setSelectedIndex(0);
		gbc.gridx=3;gbag.setConstraints(tdateMon,gbc);
		tdateYear=new JComboBox();
		for(int i=0;i<years.length;i++) tdateYear.addItem(years[i]);
		tdateYear.setSelectedIndex(10);
		gbc.gridx=4;gbag.setConstraints(tdateYear,gbc);
		JButton start = new JButton("OK");
		start.setActionCommand("start");
		gbc.gridx=2;gbc.gridy=7;gbc.gridwidth=1;
		gbag.setConstraints(start,gbc);
		JButton close = new JButton("Отмена");
		close.setActionCommand("exit");
		gbc.gridx=3;
		gbag.setConstraints(close,gbc);
		this.add(start);this.add(close);this.add(fnameLab);this.add(fname);this.add(nameLab);
		this.add(name);this.add(snameLab);this.add(sname);this.add(numLab);this.add(num);
		this.add(fdateLab);this.add(fdateDay);this.add(fdateMon);this.add(fdateYear);
		this.add(tdateLab);this.add(tdateDay);this.add(tdateMon);this.add(tdateYear);
		close.addActionListener(this);
		start.addActionListener(this);
		this.pack();
		this.setVisible(true);
		
	}
	protected static Vector<String> getHeaders(){
		Vector<String> head = new Vector<String>();
		head.add("Фамилия Имя Отчество");
		head.add("Номер допуска");
		head.add("Дата получения");
		head.add("Действителен до");
		return head;
	}
}

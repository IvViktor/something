package viktor.prog1;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class MainFrame extends Frame implements ActionListener,ItemListener{
	Checkbox sort,search;
	Panel searchPan;
	String fileName=null;
	public static void main(String args[]){
		MainFrame frame = new MainFrame();
		frame.setSize(new Dimension(400,350));
		frame.setTitle("Учетник 2015");
		frame.setVisible(true);
	}
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("exit")) System.exit(0);
		if(ae.getActionCommand().equals("browse")){
		FileDialog fd = new FileDialog(this,"Выберите файл с информацией",FileDialog.LOAD);
		fd.setVisible(true);
		if(fd.getDirectory()!=null) fileName=fd.getDirectory()+fd.getFile();
		else fileName = fd.getFile();
		}
		if(ae.getActionCommand().equals("start")){
			if(fileName==null){
			String msg="Файл не найден, выберите файл с помощью кнопки <Выберите файл>";
			ErrorDialog errD = new ErrorDialog(this,"ОШИБКА!",true,msg);
			}
			else{
			ErrorDialog errD = new ErrorDialog(this,"Путь у файлу",true,fileName);
			}
		}
	}
	public void itemStateChanged(ItemEvent ie){
		if(sort.getState()) searchPan.setVisible(false);
		else if(search.getState()) searchPan.setVisible(true);
	}
	protected MainFrame(){
		super();
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we) {System.exit(0);}
		});
		GridBagLayout gbag = new GridBagLayout();
		setLayout(gbag);
		GridBagConstraints gbc = new GridBagConstraints();
		Label head = new Label("Начните свою работу:",Label.CENTER);
		gbc.insets= new Insets(5,5,5,5);
		//gbc.fill=GridBagConstraints.BOTH;
		//gbc.anchor=GridBagConstraints.PAGE_START;
		gbc.ipadx=10;
		gbc.ipady=5;
		gbc.weightx=0.5;gbc.weighty=0.5;
		gbc.gridx=1;gbc.gridwidth=3;gbc.gridy=0;
		gbag.setConstraints(head,gbc);
		CheckboxGroup grp = new CheckboxGroup();
		sort = new Checkbox("Сортировка",grp,false);
		gbc.gridy=1;gbc.gridwidth=1;
		gbag.setConstraints(sort,gbc);
		search= new Checkbox("Поиск",grp,true);
		gbc.gridx=3;
		gbag.setConstraints(search,gbc);
		Choice chmenu= new Choice();
		chmenu.add("по фамилии");chmenu.add("по номеру");
		chmenu.add("по дате получения");chmenu.add("по дате истечения");
		chmenu.select(3);
		gbc.gridx=1;gbc.gridy=3;gbc.gridwidth=3;
		gbag.setConstraints(chmenu,gbc);
		Button start = new Button("Начать");
		start.setActionCommand("start");
		gbc.gridx=0;gbc.gridy=5;gbc.gridwidth=1;
		gbag.setConstraints(start,gbc);
		Button selectFile= new Button("Выбрать файл");
		selectFile.setActionCommand("browse");
		gbc.gridx=1;gbc.gridy=5;gbc.gridwidth=1;
		gbag.setConstraints(selectFile,gbc);
		Button close = new Button("Закрыть");
		close.setActionCommand("exit");
		gbc.gridx=4;
		gbag.setConstraints(close,gbc);
		this.add(head);this.add(sort);this.add(search);this.add(chmenu);this.add(start);
		this.add(close);this.add(selectFile);
		close.addActionListener(this);
		start.addActionListener(this);
		selectFile.addActionListener(this);
		searchPan = new Panel();
		Label searchLab= new Label("Поиск:",Label.CENTER);
		searchPan.add(searchLab);
		TextField searchText = new TextField(25);
		searchPan.add(searchText);
		gbc.gridx=0;gbc.gridy=2;gbc.gridwidth=4;
		gbag.setConstraints(searchPan,gbc);
		this.add(searchPan);
		sort.addItemListener(this);
		search.addItemListener(this);
	}
}

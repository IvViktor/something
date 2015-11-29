package viktor.prog1;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;
public class TableFrame extends JFrame implements ActionListener{
	Vector<Person> data=Person.sort(Person.getPersonsList(fileName),0);
	static final String fileName="testfile.test";
	JTextField searchText;TableModel tableModel;
	JComboBox<String> chmenu;
	TableRowSorter<MyTableModel> tableSorter;
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("close")){
			Person.writeList(data,fileName);
		 	this.dispose();
		}
		if(ae.getActionCommand().equals("add")){
			new MainFrame(data,tableModel);
		}
	}
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){createGUI();}
		});
	}
	static void createGUI(){
		new TableFrame();
	}
	protected TableFrame(){
		super("Учетник 2015");
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				Person.writeList(data,fileName);
				we.getWindow().dispose();
			}
		});
		GridBagLayout gbag = new GridBagLayout();
		this.setLayout(gbag);
		GridBagConstraints c = new GridBagConstraints();
		tableModel = new MyTableModel();
		JTable table=new JTable(tableModel);
		tableSorter=new TableRowSorter(tableModel);
		tableSorter.setComparator(0,Person.nameOrderTab);
		tableSorter.setComparator(1,Person.numOrderTab);
		tableSorter.setComparator(2,Person.dateOrderTab);
		tableSorter.setComparator(3,Person.expireOrderTab);
		table.setRowSorter(tableSorter);
		table.setRowSelectionAllowed(true);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(350);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.setPreferredScrollableViewportSize(new Dimension(800,300));
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(table);
		c.gridx=0;c.gridy=0;c.gridwidth=20;
		c.insets=new Insets(5,5,5,5);c.fill=GridBagConstraints.BOTH;
		c.weightx=1.0;
		gbag.setConstraints(jsp,c);
		this.add(jsp);
		JButton addUser=new JButton("Добавить");
		addUser.setActionCommand("add");
		addUser.addActionListener(this);
		c.ipadx=10;c.ipady=5;c.gridx=2;c.gridy=2;
		c.gridwidth=1;c.fill=GridBagConstraints.NONE;
		gbag.setConstraints(addUser,c);
		JButton delUser=new JButton("Удалить");
		delUser.setActionCommand("delUser");
		delUser.addActionListener(this);
		c.gridx=3;c.gridy=2;
		gbag.setConstraints(delUser,c);
		JButton close=new JButton("Закрыть");
		close.setActionCommand("close");
		close.addActionListener(this);
		c.gridx=9;c.gridy=2;
		gbag.setConstraints(close,c);
		JLabel label = new JLabel("Фильтровать");
		c.gridx=2;c.gridy=1;
		gbag.setConstraints(label,c);
		chmenu=new JComboBox<String>();
		chmenu.addItem("по фамилии");chmenu.addItem("по номеру допуска");
		chmenu.addItem("по дате получения");chmenu.addItem("по дате истечения");
		chmenu.setSelectedIndex(0);
		c.gridx=3;c.gridy=1;
		gbag.setConstraints(chmenu,c);
		searchText = new JTextField(40);
		c.gridx=4;c.gridy=1;
		gbag.setConstraints(searchText,c);
		searchText.getDocument().addDocumentListener(
			new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				newFilter();
			}
			public void insertUpdate(DocumentEvent e){
				newFilter();
			}
			public void removeUpdate(DocumentEvent e){
				newFilter();
			}
		});
		this.add(close);
		this.add(addUser);this.add(delUser);this.add(chmenu);
		this.add(searchText);this.add(label);
		this.pack();
		this.setVisible(true);
	}
	private void newFilter(){
		RowFilter<MyTableModel,Integer> rf=null;
		int a=chmenu.getSelectedIndex();
		try{
			rf=RowFilter.regexFilter(searchText.getText(),a);
		} catch (java.util.regex.PatternSyntaxException e) {return;}
		tableSorter.setRowFilter(rf);
	}
	class MyTableModel extends AbstractTableModel{
		Vector<String> header=MainFrame.getHeaders();
		public int getColumnCount(){
			return header.size();
		}
		public int getRowCount(){
			return data.size();
		}
		public String getColumnName(int col){
			return header.get(col);
		}
		public Object getValueAt(int row,int col){
			return data.get(row).get(col);
		}
		public Class getColumnClass(int col){
			return getValueAt(0,col).getClass();
		}
		
	}
}

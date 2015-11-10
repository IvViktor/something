package viktor.prog1;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;
public class TableFrame extends JFrame implements ActionListener{
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("close")) this.dispose();
	}
	protected TableFrame(String title, Vector<Person> data, Vector<String> headers){
		super(title);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				we.getWindow().dispose();
			}
		});
		GridBagLayout gbag = new GridBagLayout();
		this.setLayout(gbag);
		GridBagConstraints c = new GridBagConstraints();
		JTable table=new JTable(data,headers);
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
		JButton close=new JButton("Закрыть");
		close.setActionCommand("close");
		close.addActionListener(this);
		c.ipadx=10;c.ipady=5;c.gridx=9;c.gridy=1;
		c.gridwidth=1;c.fill=GridBagConstraints.NONE;
		gbag.setConstraints(close,c);
		this.add(close);
		this.pack();
		this.setVisible(true);
	}
}

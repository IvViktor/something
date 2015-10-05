package viktor.prog1;
import java.awt.*;
import java.awt.event.*;
public class ErrorDialog extends Dialog implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getActionCommand().equals("OK")) this.dispose();
		}
	protected ErrorDialog(Frame f,String title,boolean mode,String msg){
		super(f,title,mode);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){we.getWindow().dispose();}
		});
		GridBagLayout gbag = new GridBagLayout();
		this.setLayout(gbag);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridy=0;c.gridx=0;c.gridwidth=3;
		Label text = new Label(msg,Label.CENTER);
		gbag.setConstraints(text,c);
		this.add(text);
		c.gridx=2;c.gridy=1;c.ipadx=20;c.ipady=7;c.gridwidth=1;
		Button okBut=new Button("OK");
		gbag.setConstraints(okBut,c);
		this.add(okBut);
		okBut.addActionListener(this);
		this.pack();
		this.setVisible(true);
	}
}

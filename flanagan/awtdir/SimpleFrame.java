//package flanagan.awtdir;
import java.awt.*;
import java.awt.event.*;
public class SimpleFrame extends Frame{
	String keymsg=new String();
	String mmsg=new String();
	String msg=new String();
	int mouseX=10;
	int mouseY=20;
	public SimpleFrame(){
		addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent ke){
				keymsg+=ke.getKeyChar();
				msg="Char typed";
				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me){
				mouseX=me.getX();
				mouseY=me.getY();
				mmsg="Mouse pressed on "+mouseX+","+mouseY;
				msg="Mouse pressed at ";
				repaint();
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
	}
	public void paint(Graphics g){
		g.drawString(mmsg,mouseX,mouseY);
		g.drawString(keymsg,10,40);
	}
	public static void main(String args[]){
		SimpleFrame fr=new SimpleFrame();
		fr.setSize(300,200);
		fr.setTitle("SIMPLE_FRAME");
		fr.setVisible(true);
	}
}

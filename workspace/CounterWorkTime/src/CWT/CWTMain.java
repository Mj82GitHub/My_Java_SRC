//Главный класс, запускающий приложение
package CWT;

import javax.swing.SwingUtilities;

public class CWTMain {
	static public void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new CWTFrame();
			}
		});
	}
}

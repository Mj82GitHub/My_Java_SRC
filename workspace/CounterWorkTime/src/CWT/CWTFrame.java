//Класс, отвечающий за настройки фрейма приложения
package CWT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

@SuppressWarnings({ "serial", "unused" })
public class CWTFrame extends JFrame {
	public CWTFrame(){
		super("Счетчик часов работы оборудования");
		
		CWTComponents CWTComps=new CWTComponents();
		
		Toolkit tKit=Toolkit.getDefaultToolkit();
		Image img=tKit.getImage("CWTIcon.jpg"); // Установка пиктограммы в заголовке
		setIconImage(img);
		
		Dimension screenSize=tKit.getScreenSize();
		int screenWidth=screenSize.width;
		int screenHeight=screenSize.height;
		
		setSize(370, 200);
		setLocation((screenWidth/2)-185, (screenHeight/2)-100);
		
		GridBagLayout gbag=new GridBagLayout();
		GridBagConstraints gbc=new GridBagConstraints();
		
		setLayout(gbag);
		
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gbc.insets=new Insets(0, 5, 5, 5);
		gbc.anchor=GridBagConstraints.EAST;
		
		gbc.gridx=0;
		gbc.gridy=1;
		gbag.setConstraints(CWTComps.jlabStart, gbc);
		
		gbc.gridx=0;
		gbc.gridy=2;
		gbag.setConstraints(CWTComps.jlabEnd, gbc);
		
		gbc.insets=new Insets(5, 5, 0, 5);
		gbc.anchor=GridBagConstraints.CENTER;
		
		gbc.gridx=1;
		gbc.gridy=0;
		gbag.setConstraints(CWTComps.jlabDay, gbc);
		
		gbc.gridx=2;
		gbc.gridy=0;
		gbag.setConstraints(CWTComps.jlabMonth, gbc);
		
		gbc.gridx=3;
		gbc.gridy=0;
		gbag.setConstraints(CWTComps.jlabYear, gbc);
		
		gbc.insets=new Insets(5, 5, 5, 5);
		
		gbc.gridx=1;
		gbc.gridy=1;
		gbag.setConstraints(CWTComps.jcbStartDay, gbc);
		
		gbc.gridx=2;
		gbc.gridy=1;
		gbag.setConstraints(CWTComps.jcbStartMonth, gbc);
		
		gbc.gridx=3;
		gbc.gridy=1;
		gbag.setConstraints(CWTComps.jcbStartYear, gbc);
		
		gbc.gridx=1;
		gbc.gridy=2;
		gbag.setConstraints(CWTComps.jcbEndDay, gbc);
		
		gbc.gridx=2;
		gbc.gridy=2;
		gbag.setConstraints(CWTComps.jcbEndMonth, gbc);
		
		gbc.gridx=3;
		gbc.gridy=2;
		gbag.setConstraints(CWTComps.jcbEndYear, gbc);
		
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.insets=new Insets(20, 0, 0, 0);
		
		gbc.gridx=0;
		gbc.gridy=3;
		gbag.setConstraints(CWTComps.jlabTotal, gbc);
		
		add(CWTComps.jlabStart);
		add(CWTComps.jlabEnd);
		
		add(CWTComps.jlabDay);
		add(CWTComps.jlabMonth);
		add(CWTComps.jlabYear);
		
		add(CWTComps.jcbStartDay);
		add(CWTComps.jcbStartMonth);
		add(CWTComps.jcbStartYear);
		
		
		add(CWTComps.jcbEndDay);
		add(CWTComps.jcbEndMonth);
		add(CWTComps.jcbEndYear);
		
		add(CWTComps.jlabTotal);
		
		setVisible(true);	
	}
}

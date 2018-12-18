//Класс, работающий с компонентами во фрейме
package CWT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings({"unused","rawtypes"})
public class CWTComponents {
	JLabel jlabStart; // метка начало работ
	JLabel jlabEnd; //метка конец работ
	JLabel jlabTotal; //кол-во часов и дней наработки
	JLabel jlabDay; //метка день
	JLabel jlabMonth; //метка месяц
	JLabel jlabYear; //метка год
		
	JComboBox jcbStartDay; //список дня начала работ
	JComboBox jcbStartMonth; //список месяца начала работ
	JComboBox jcbStartYear; //список года начала работ
	
	JComboBox jcbEndDay; //список дня конца работ
	JComboBox jcbEndMonth; //список месяца конца работ
	JComboBox jcbEndYear; //список года конца работ
	
	
	
	CWTDate startDate; //объект отвечающий за данные начала работ
	CWTDate endDate; //объект отвечающий за данные конца работ
	
	CWTFunction func; //подключает вспом функции
	
	@SuppressWarnings("unchecked")
	public CWTComponents(){
		startDate=new CWTDate();
		endDate=new CWTDate();
		
		func=new CWTFunction();
		
		jlabStart=new JLabel("Начало работы:");
		jlabEnd=new JLabel("Конец работы:");
		jlabTotal=new JLabel("<html>Кол-во часов работы: 24<br>"+"Кол-во дней работы: 1<br>");
		
		jlabDay=new JLabel("день");				
		jlabMonth=new JLabel("месяц");				
		jlabYear=new JLabel("год");
		
		//настройки списков и обработка событий списков
		jcbStartDay=new JComboBox(startDate.getDaysOfMonth());
		jcbStartDay.setBackground(Color.WHITE);
		jcbStartDay.setSelectedIndex((startDate.getDay())-1);
		jcbStartDay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				startDate.setDay(jcbStartDay.getSelectedIndex());
				
				func.resultCWT(jlabTotal, startDate, endDate);
			}
		});
		
		jcbStartMonth=new JComboBox(startDate.getNamesOfMonth());
		jcbStartMonth.setBackground(Color.WHITE);
		jcbStartMonth.setSelectedIndex(startDate.getMonth());
		jcbStartMonth.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int tmpIdx=jcbStartDay.getSelectedIndex();
				
				startDate.setMonth(jcbStartMonth.getSelectedIndex());
				
				func.repaintDays(tmpIdx, jcbStartDay, startDate);
				
				func.resultCWT(jlabTotal, startDate, endDate);
			}
		});
		
		jcbStartYear=new JComboBox(startDate.getYears());
		jcbStartYear.setBackground(Color.WHITE);
		jcbStartYear.setSelectedIndex(startDate.getPresentYear());
		jcbStartYear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int tmpIdx=jcbStartDay.getSelectedIndex();
				
				startDate.setMonth(jcbStartMonth.getSelectedIndex());
				startDate.setYear(jcbStartYear.getSelectedIndex());
								
				func.repaintDays(tmpIdx, jcbStartDay, startDate);
				
				func.resultCWT(jlabTotal, startDate, endDate);
			}
		});
		
		jcbEndDay=new JComboBox(endDate.getDaysOfMonth());
		jcbEndDay.setBackground(Color.WHITE);
		jcbEndDay.setSelectedIndex((endDate.getDay())-1);
		jcbEndDay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				endDate.setDay(jcbEndDay.getSelectedIndex());
				
				func.resultCWT(jlabTotal, startDate, endDate);
			}
		});
		
		jcbEndMonth=new JComboBox(endDate.getNamesOfMonth());
		jcbEndMonth.setBackground(Color.WHITE);
		jcbEndMonth.setSelectedIndex(endDate.getMonth());
		jcbEndMonth.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int tmpIdx=jcbEndDay.getSelectedIndex();
				
				endDate.setMonth(jcbEndMonth.getSelectedIndex());
				
				func.repaintDays(tmpIdx, jcbEndDay, endDate);
				
				func.resultCWT(jlabTotal, startDate, endDate);
			}
		});
		
		jcbEndYear=new JComboBox(endDate.getYears());
		jcbEndYear.setBackground(Color.WHITE);
		jcbEndYear.setSelectedIndex(endDate.getPresentYear());
		jcbEndYear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int tmpIdx=jcbEndDay.getSelectedIndex();
				
				endDate.setMonth(jcbEndMonth.getSelectedIndex());
				endDate.setYear(jcbEndYear.getSelectedIndex());
								
				func.repaintDays(tmpIdx, jcbEndDay, endDate);
				
				func.resultCWT(jlabTotal, startDate, endDate);
			}
		});
	}	
}

//�����, ���������� � ������������ �� ������
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
	JLabel jlabStart; // ����� ������ �����
	JLabel jlabEnd; //����� ����� �����
	JLabel jlabTotal; //���-�� ����� � ���� ���������
	JLabel jlabDay; //����� ����
	JLabel jlabMonth; //����� �����
	JLabel jlabYear; //����� ���
		
	JComboBox jcbStartDay; //������ ��� ������ �����
	JComboBox jcbStartMonth; //������ ������ ������ �����
	JComboBox jcbStartYear; //������ ���� ������ �����
	
	JComboBox jcbEndDay; //������ ��� ����� �����
	JComboBox jcbEndMonth; //������ ������ ����� �����
	JComboBox jcbEndYear; //������ ���� ����� �����
	
	
	
	CWTDate startDate; //������ ���������� �� ������ ������ �����
	CWTDate endDate; //������ ���������� �� ������ ����� �����
	
	CWTFunction func; //���������� ����� �������
	
	@SuppressWarnings("unchecked")
	public CWTComponents(){
		startDate=new CWTDate();
		endDate=new CWTDate();
		
		func=new CWTFunction();
		
		jlabStart=new JLabel("������ ������:");
		jlabEnd=new JLabel("����� ������:");
		jlabTotal=new JLabel("<html>���-�� ����� ������: 24<br>"+"���-�� ���� ������: 1<br>");
		
		jlabDay=new JLabel("����");				
		jlabMonth=new JLabel("�����");				
		jlabYear=new JLabel("���");
		
		//��������� ������� � ��������� ������� �������
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

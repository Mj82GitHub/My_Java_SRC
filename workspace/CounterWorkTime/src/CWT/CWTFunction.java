//����� ��������������� �������
package CWT;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class CWTFunction {
	@SuppressWarnings({"unchecked", "rawtypes"})
	//������� ���������������� ������ ���� ��� ������� ������ � ����������� ����� ������ ���
	void repaintDays(int tmp, JComboBox jcbDay, CWTDate date){
		jcbDay.removeAllItems();
		
		date.setDaysOfMonth();
		
		for(int i=0; i<date.getNumbersDaysOfMonth(); i++)
			jcbDay.addItem(date.getDataDay(i));
		
		if(jcbDay.getItemCount()<=tmp)
			jcbDay.setSelectedIndex(0);
		else
			jcbDay.setSelectedIndex(tmp);
	}
	
	//������� ��������� ���-�� ����� � ���� ���������, ��������� ����� ����� �� ��� ������ ������
	void resultCWT(JLabel jlab, CWTDate dStart, CWTDate dEnd){			
		if(dEnd.getmSec()<dStart.getmSec())
			jlab.setText("<html>���� ����� ������ �� �����!<br><br>");
		else{
			long resH=((((dEnd.getmSec()-dStart.getmSec())/1000)/60)/60);
			long resD=(((((dEnd.getmSec()-dStart.getmSec())/1000)/60)/60)/24);
			
			jlab.setText("<html>���-�� ����� ������: "+(resH+24)+"<br>���-�� ���� ������: "+(resD+1));
		}
	}
}

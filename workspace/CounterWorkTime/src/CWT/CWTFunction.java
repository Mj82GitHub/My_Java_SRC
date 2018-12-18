//Класс вспомогательных функций
package CWT;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class CWTFunction {
	@SuppressWarnings({"unchecked", "rawtypes"})
	//функция перерисовывающая список дней для каждого месяца и сохраняющая метку выбора дня
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
	
	//функция выводящая кол-во часов и дней наработки, проверяет чтобы коней не был меньше начала
	void resultCWT(JLabel jlab, CWTDate dStart, CWTDate dEnd){			
		if(dEnd.getmSec()<dStart.getmSec())
			jlab.setText("<html>Дата конца работы не верно!<br><br>");
		else{
			long resH=((((dEnd.getmSec()-dStart.getmSec())/1000)/60)/60);
			long resD=(((((dEnd.getmSec()-dStart.getmSec())/1000)/60)/60)/24);
			
			jlab.setText("<html>Кол-во часов работы: "+(resH+24)+"<br>Кол-во дней работы: "+(resD+1));
		}
	}
}

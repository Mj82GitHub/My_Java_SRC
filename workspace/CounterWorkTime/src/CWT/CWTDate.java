// �����, ���������� � ������.
package CWT;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressWarnings({ "unused", "serial" })
public class CWTDate extends GregorianCalendar {
	int startYear=2000; //��������� ���, � �������� ����� ������������ ������
	int endYear=2021; //�������� ���, ������� ����� ������������ � ������
	
	Integer[] days; //���-�� ���� � ������
	Integer[] years=new Integer[(endYear-startYear)]; //������ ��� ������
	
	long mSec; //���-�� �����������
	
	//������ � ������� ������� � ����
	String[] monthNames={
			"������", "�������", "����", "������",
			"���", "����", "����", "������", 
			"��������", "�������", "������", "�������"};
	
	//�����������, ������� ������������� ������� ����
	public CWTDate(){
		super();
				
		//��������� ������ ���, ������� ����� ������������ � ������
				for(int i=0; i<years.length; i++)
					years[i]=startYear+i;
	}

	//�����������, �������� ������ ������ ����
	public CWTDate(int y, int m, int d){
		super(y, m, d);
		
		//��������� ������ ���, ������� ����� ������������ � ������
		for(int i=0; i<years.length; i++)
			years[i]=startYear+i;
	}
	
	//��������� ���� ��� ��� ������� ������
	void setHourOfDay(int h){
		set(Calendar.HOUR_OF_DAY, h);
	}
	
	//������������� ������� ������ ����
	void setDay(int idxDay){
		if(Calendar.DAY_OF_MONTH==idxDay+1)
			return;
		else
			set(Calendar.DAY_OF_MONTH, idxDay+1);	
	}
	
	//������������� ������� ������ �����
	void setMonth(int idxMonth){
		if(Calendar.MONTH==idxMonth)
			return;
		else
			set(Calendar.MONTH, idxMonth+1);
	}
	
	//������������� ������� ������ ���
	void setYear(int idxYear){
		if(Calendar.YEAR==idxYear)
			return;
		else
			set(Calendar.YEAR, years[idxYear]);
	}
	
	//���������� ���-�� ����������� ������� ������
	long getmSec(){
		return getTimeInMillis();
	}
	
	//���������� ���-�� ����� ������������ �� �����������
		long getHours(long msec){
			return ((msec/1000)/60)/60;
		}
	
	//���������� ������ � ������� ��� ������� ������
	int getPresentYear(){
		int i=0;
		
		while(years[i]!=get(Calendar.YEAR))
			i++;
		
		return i;
	}
	
	//���������� ��� ������� ������
	int getYear(){
		return get(Calendar.YEAR);
	}
	
	//���������� ���� � ������
	Integer[] getYears(){
		return years;
	}
	
	//���������� ����� ������� ������
	String getMonthOfYear(){
		return monthNames[get(Calendar.MONTH)];
	}
	
	//���������� �������� ������� � ����
	String[] getNamesOfMonth(){
		return monthNames;
	}
	
	//���������� ���� ������� ������
	int getDay(){
		return get(Calendar.DAY_OF_MONTH);
	}
	
	//���������� ������ ���� � ������
	Integer[] getDays(){
		return days;
	}
	
	//���������� �������� ������ ������� ���� � ������
	Integer getDataDay(int i){
		return days[i];
	}
	
	//���������� ���-�� ���� � ������
	int getNumbersDaysOfMonth(){
		return days.length;
	}
	
	//���������� ����� ���� � ���� �����
		int getMonth(){
			return get(Calendar.MONTH);
		}
		
	//������������� ���-�� ���� � ������ � ����������� �� ������ � ����
		void setDaysOfMonth(){
			switch(monthNames[get(Calendar.MONTH)]){
			case "������":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "�������":
				if(isLeapYear(get(Calendar.YEAR))){
					days=new Integer[29];
					for(int i=0; i<days.length; i++){
						days[i]=i+1;
					}
					break;
				}
				else{
					days=new Integer[28];
					for(int i=0; i<days.length; i++){
						days[i]=i+1;
					}
					break;
				}
				
			case "����":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
			
			case "������":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "���":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "����":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "����":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "������":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "��������":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "�������":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "������":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "�������":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
			}
		}	
			
	//���������� ���-�� ���� � ������ � ����������� �� ������ � ����
	Integer[] getDaysOfMonth(){
		switch(monthNames[get(Calendar.MONTH)]){
		case "������":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "�������":
			if(isLeapYear(get(Calendar.YEAR))){
				days=new Integer[29];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
			}
			else{
				days=new Integer[28];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
			}
			
		case "����":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
		
		case "������":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "���":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "����":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "����":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "������":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "��������":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "�������":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "������":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "�������":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
		}		
		
		return days;
	}
}

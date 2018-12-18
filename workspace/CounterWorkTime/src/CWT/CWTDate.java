//  ласс, работающий с датами.
package CWT;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressWarnings({ "unused", "serial" })
public class CWTDate extends GregorianCalendar {
	int startYear=2000; //начальный год, с которого будет отображатьс€ список
	int endYear=2021; //конечный год, который будет отображатьс€ в списке
	
	Integer[] days; //кол-во дней в мес€це
	Integer[] years=new Integer[(endYear-startYear)]; //массив лет списка
	
	long mSec; //кол-во миллисекунд
	
	//массив с именами мес€цев в году
	String[] monthNames={
			"январь", "‘евраль", "ћарт", "јпрель",
			"ћай", "»юнь", "»юль", "јвгуст", 
			"—ент€брь", "ќкт€брь", "Ќо€брь", "ƒекабрь"};
	
	//конструктор, который устанавливает текущую дату
	public CWTDate(){
		super();
				
		//заполн€ем массив лет, которые будут отображатьс€ в списке
				for(int i=0; i<years.length; i++)
					years[i]=startYear+i;
	}

	//конструктор, которому задаем нужную дату
	public CWTDate(int y, int m, int d){
		super(y, m, d);
		
		//заполн€ем массив лет, которые будут отображатьс€ в списке
		for(int i=0; i<years.length; i++)
			years[i]=startYear+i;
	}
	
	//установка часа дн€ дл€ объекта класса
	void setHourOfDay(int h){
		set(Calendar.HOUR_OF_DAY, h);
	}
	
	//устанавливает объекту класса день
	void setDay(int idxDay){
		if(Calendar.DAY_OF_MONTH==idxDay+1)
			return;
		else
			set(Calendar.DAY_OF_MONTH, idxDay+1);	
	}
	
	//устанавливает объекту класса мес€ц
	void setMonth(int idxMonth){
		if(Calendar.MONTH==idxMonth)
			return;
		else
			set(Calendar.MONTH, idxMonth+1);
	}
	
	//устанавливает объекту класса год
	void setYear(int idxYear){
		if(Calendar.YEAR==idxYear)
			return;
		else
			set(Calendar.YEAR, years[idxYear]);
	}
	
	//возвращает кол-во миллисекунд объекта класса
	long getmSec(){
		return getTimeInMillis();
	}
	
	//возвращает кол-во часов перещитанное из миллисекунд
		long getHours(long msec){
			return ((msec/1000)/60)/60;
		}
	
	//возвращает индекс в массиве лет объекта класса
	int getPresentYear(){
		int i=0;
		
		while(years[i]!=get(Calendar.YEAR))
			i++;
		
		return i;
	}
	
	//возвращает год объекта класса
	int getYear(){
		return get(Calendar.YEAR);
	}
	
	//возвращает года в списке
	Integer[] getYears(){
		return years;
	}
	
	//возвращает мес€ц объекта класса
	String getMonthOfYear(){
		return monthNames[get(Calendar.MONTH)];
	}
	
	//возвращает название мес€цев в году
	String[] getNamesOfMonth(){
		return monthNames;
	}
	
	//возвращает день объекта класса
	int getDay(){
		return get(Calendar.DAY_OF_MONTH);
	}
	
	//возвращает массив дней в мес€це
	Integer[] getDays(){
		return days;
	}
	
	//возвращает значение €чейки массива дней в мес€це
	Integer getDataDay(int i){
		return days[i];
	}
	
	//возврвщает кол-во дней в мес€це
	int getNumbersDaysOfMonth(){
		return days.length;
	}
	
	//возвращает мес€ц года в виде цифры
		int getMonth(){
			return get(Calendar.MONTH);
		}
		
	//устанавливает кол-во дней в мес€це в зависимости от мес€ца в году
		void setDaysOfMonth(){
			switch(monthNames[get(Calendar.MONTH)]){
			case "январь":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "‘евраль":
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
				
			case "ћарт":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
			
			case "јпрель":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "ћай":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "»юнь":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "»юль":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "јвгуст":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "—ент€брь":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "ќкт€брь":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "Ќо€брь":
				days=new Integer[30];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
				
			case "ƒекабрь":
				days=new Integer[31];
				for(int i=0; i<days.length; i++){
					days[i]=i+1;
				}
				break;
			}
		}	
			
	//возвращает кол-во дней в мес€це в зависимости от мес€ца в году
	Integer[] getDaysOfMonth(){
		switch(monthNames[get(Calendar.MONTH)]){
		case "январь":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "‘евраль":
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
			
		case "ћарт":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
		
		case "јпрель":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "ћай":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "»юнь":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "»юль":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "јвгуст":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "—ент€брь":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "ќкт€брь":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "Ќо€брь":
			days=new Integer[30];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
			
		case "ƒекабрь":
			days=new Integer[31];
			for(int i=0; i<days.length; i++){
				days[i]=i+1;
			}
			break;
		}		
		
		return days;
	}
}

import java.util.Date;

// Класс для рассчета контрольной суммы CRC-16
public class crc_16 {
	// Ф-ция расчета контрольной суммы
	static byte[] resCRC16(byte[] d){
		int result=(int)0xFFFF;// Результат (контрольная сумма). В самом начале заполняем регистр этим числом
		int polynome=(int)0xA001;// Полином, который учавствует в рассчете CRC 16
		byte[] data=d;// Слово, для которого рассчитывается контрольная сумма
		byte[] crc16=new byte[2];// Контрольная сумма в виде byte[]
		String resStr;// Результат рассчетов result в виде строки
		String resLSB;// Младший байт result
		String resMSB;// Старший байт result
		Short LSB;// Младший байт result
		Short MSB;// Старший байт result
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		//Пошагово выполняются операции со всеми значениями массива данных
		for(int i=0; i<data.length; i++){
			result=result^((data[i]<<24)>>>24);
			
			for(int j=0; j<8; j++){
				if((result&(int)0x0001)==1){
					result>>>=1;
		            result^=polynome;
		            System.out.println("Result ["+j+"]: "+Integer.toHexString(result).toUpperCase());
				}
				else{
					result>>>=1;
					System.out.println("Result ["+j+"]: "+Integer.toHexString(result).toUpperCase());
				}
			}
			System.out.println();
		}

		resStr=Integer.toHexString(result);// Переводим результат рассчетов в строку
		
		// Если при переводе результата в строку получается нечетное кол-во символов, то делаем его четным добавляя ноль
		if(resStr.length()%2 != 0)
			resStr=0+resStr;
		
		resLSB=resStr.substring(resStr.length()-(resStr.length()/2), resStr.length());// Выделяем младший байт result
		resMSB=resStr.substring(resStr.length()-resStr.length(), resStr.length()-(resStr.length()/2));// Выделяем старший байт result
		
		LSB=Short.parseShort(resLSB, 16);// Переводим младший байт из текста в число
		MSB=Short.parseShort(resMSB, 16);// Переводим старший байт из текста в число
		
		// Заполняем массив контрольной суммы в обратном порядке
		crc16[0]=LSB.byteValue();
		crc16[1]=MSB.byteValue();
		
		System.out.println("Result (int): "+result);
		System.out.println("Result (String): "+resStr.toUpperCase());
		//System.out.println("MSB (short): "+resMSB.toUpperCase());
		//System.out.println("LSB (short): "+resLSB.toUpperCase());
		System.out.println("----------------------------------");
		System.out.println("CRC-16 Modbus (String): "+resLSB.toUpperCase()+" "+resMSB.toUpperCase());
		System.out.println("CRC-16 Modbus (byte): "+crc16[0]+" "+crc16[1]);
		
		return crc16;
	}
	
	public static void main(String[] args) {
		// Слово, для которого рассчитывается контрольная сумма
		byte[] word={(byte)0x05, (byte)0x03, (byte)0xAA};
		
		Date startTime = new Date();
		Long timeStart = startTime.getTime();
		String str = timeStart.toHexString(timeStart);
		byte[] b = str.getBytes();
		
		resCRC16(b);// Ф-ция расчета контрольной суммы
	}		
}

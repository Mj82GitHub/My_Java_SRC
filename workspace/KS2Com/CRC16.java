package KS2Com;

// Класс для рассчета контрольной суммы CRC-16

public class CRC16 {

    private int result;// Результат (контрольная сумма). В самом начале заполняем регистр этим числом
    private int polynome;// Полином, который учавствует в рассчете CRC 16
    private byte[] data;// Слово, для которого рассчитывается контрольная сумма
    private byte[] crc16=new byte[2];// Контрольная сумма в виде byte[]
    private String resStr;// Результат рассчетов result в виде строки
    private String resLSB;// Младший байт result
    private String resMSB;// Старший байт result
    private Short LSB;// Младший байт result
    private Short MSB;// Старший байт result

    // Ф-ция расчета контрольной суммы
    public byte[] resCRC16(byte[] d){
        result=(int)0xFFFF;
        polynome=(int)0xA001;
        data=d;
        //Пошагово выполняются операции со всеми значениями массива данных
        for(int i=0; i<data.length; i++){
            result=result^((data[i]<<24)>>>24);

            for(int j=0; j<8; j++){
                if((result&(int)0x0001)==1){
                    result>>>=1;
                    result^=polynome;
                }
                else{
                    result>>>=1;
                }
            }
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

        return crc16;
    }

    //Ф-ция возвращает старший байт контрольной суммы
    public byte getHihgByteCRC16(byte[] d) {
        byte[] tmpArray=resCRC16(d);

        return tmpArray[1];
    }

    //Ф-ция возвращает младший байт контрольной суммы
    public byte getLowByteCRC16(byte[] d) {
        byte[] tmpArray=resCRC16(d);

        return tmpArray[0];
    }

    public static String [] getResStr(byte [] crc) {
        String [] res = new String[2];

        res[0] = Integer.toHexString((crc[1] + 256) & 0xFF);
        res[1] = Integer.toHexString((crc[0] + 256) & 0xFF);

        return res;
    }
}


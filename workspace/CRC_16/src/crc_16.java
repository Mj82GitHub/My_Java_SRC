import java.util.Date;

// ����� ��� �������� ����������� ����� CRC-16
public class crc_16 {
	// �-��� ������� ����������� �����
	static byte[] resCRC16(byte[] d){
		int result=(int)0xFFFF;// ��������� (����������� �����). � ����� ������ ��������� ������� ���� ������
		int polynome=(int)0xA001;// �������, ������� ���������� � �������� CRC 16
		byte[] data=d;// �����, ��� �������� �������������� ����������� �����
		byte[] crc16=new byte[2];// ����������� ����� � ���� byte[]
		String resStr;// ��������� ��������� result � ���� ������
		String resLSB;// ������� ���� result
		String resMSB;// ������� ���� result
		Short LSB;// ������� ���� result
		Short MSB;// ������� ���� result
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		//�������� ����������� �������� �� ����� ���������� ������� ������
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

		resStr=Integer.toHexString(result);// ��������� ��������� ��������� � ������
		
		// ���� ��� �������� ���������� � ������ ���������� �������� ���-�� ��������, �� ������ ��� ������ �������� ����
		if(resStr.length()%2 != 0)
			resStr=0+resStr;
		
		resLSB=resStr.substring(resStr.length()-(resStr.length()/2), resStr.length());// �������� ������� ���� result
		resMSB=resStr.substring(resStr.length()-resStr.length(), resStr.length()-(resStr.length()/2));// �������� ������� ���� result
		
		LSB=Short.parseShort(resLSB, 16);// ��������� ������� ���� �� ������ � �����
		MSB=Short.parseShort(resMSB, 16);// ��������� ������� ���� �� ������ � �����
		
		// ��������� ������ ����������� ����� � �������� �������
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
		// �����, ��� �������� �������������� ����������� �����
		byte[] word={(byte)0x05, (byte)0x03, (byte)0xAA};
		
		Date startTime = new Date();
		Long timeStart = startTime.getTime();
		String str = timeStart.toHexString(timeStart);
		byte[] b = str.getBytes();
		
		resCRC16(b);// �-��� ������� ����������� �����
	}		
}

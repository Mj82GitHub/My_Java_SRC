package KS2Com;

import jssc.*;

public class Main {

    static SerialPort sPort;
    static byte [] request;

    public static void main(String [] args) {

        byte address = 0x10;
        byte func = 0x03;
        byte [] start_register = new byte[] {0x00, 0x0C};
        byte [] num_registers = new byte[] {0x00, 0x01};
        byte [] value = new byte[] {0x03, 0x09};

        CRC16 crc16 = new CRC16();
        byte [] res_crc = crc16.resCRC16(new byte[] { address,
                                                      func,
                                                      start_register[0],start_register[1],
                                                      num_registers[0], num_registers[1],
//                                                      value[0], value[1]
                                                      });

        String [] str = CRC16.getResStr(res_crc);
        System.out.println("CRC16: " + str[0] + " " + str[1]);

        request = new byte[] { address,
                               func,
                               start_register[0],start_register[1],
                               num_registers[0], num_registers[1],
//                               value[0], value[1],
                               res_crc[0], res_crc[1]};
/////
        String [] portNames = SerialPortList.getPortNames();

        if (portNames.length == 0)
            System.out.println("COM порты не обнаружены.");
        else
            System.out.print("Обнаружены COM порты: ");

        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
/////
        byte [] src = new byte[] {(byte) 0x21, (byte) 0x1e};
        HalfPrecisionFloat half = new HalfPrecisionFloat(src);
        System.out.println("FULL FLOAT: " + half.getFullFloat() + "\n");

        half.toHalfPrecision(0.01f);

//        System.out.println("   HALF FLOAT: " + half.getFullFloat());

        sPort = new SerialPort("COM4");

        try {
            sPort.openPort();
            sPort.setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
            sPort.setEventsMask(SerialPort.MASK_RXCHAR);
            sPort.addEventListener(new EventListener());

            System.out.println("Запрос отправлен ...");
            sPort.writeBytes(request);

            Thread.sleep(100);

            sPort.closePort();
        } catch (SerialPortException e) {
//            System.out.println("COM порты не обнаружены.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class EventListener implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte [] buffer = sPort.readBytes();

                    if (buffer == null) {
                        System.out.println("Нет ответа.");
                    } else {
                        System.out.print("Ответ:");
                        for (int i = 0; i < buffer.length; i++)
                            if (i >= 0)
                                System.out.print(" " + Integer.toHexString((buffer[i] + 256) & 0xFF));
                            else
                                System.out.print(" " + buffer[i]);
                        System.out.println();

                        System.out.println("   HALF FLOAT (BIN): " + getByteToString(buffer[3]) + " " +
                                getByteToString(buffer[4]));

                        byte [] src = new byte[] {buffer[3], buffer[4]};
                        HalfPrecisionFloat half = new HalfPrecisionFloat(src);

//                        System.out.println("   HALF FLOAT: " + half.getFullFloat());
                    }
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getByteToString(byte b) {
        String str = Integer.toBinaryString(b & 0xFF);

        // Если при переводе результата в строку получается нечетное кол-во символов, то делаем его четным добавляя ноль
        if(str.length()%2 != 0)
            str=0+str;

        return str;
    }
}

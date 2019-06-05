package PSTViewer;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class EventListener implements SerialPortEventListener {

    private SerialPort mPort;
    private WorkThread mWorkThread;

    private byte [] buffer;
    private byte [] pst_answer = new byte[0]; // собираем символы ответа с ПСТ в этот массив

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                buffer = mPort.readBytes();

                if (buffer == null) {
                    System.out.println("Нет ответа ...");
                } else {
 /*                  System.out.print("Ответ:");
                    for (int i = 0; i < buffer.length; i++)
                        System.out.print(" " + Integer.toHexString((buffer[i] + 256) & 0xFF));
                    System.out.println();*/

                    setAnswer(buffer);
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Собирает побайтово ответ с ПСТ
     * @param buffer массив байтов ответа с ПСТ
     */
    private void setAnswer(byte [] buffer) {
        if (buffer.length != 0 && buffer != null) {
            byte [] tmp = pst_answer;
            pst_answer = new byte[tmp.length + 1];
            System.arraycopy(tmp, 0, pst_answer, 0, tmp.length);
            pst_answer[pst_answer.length - 1] = buffer[0];
        }

        isEnd(buffer);
    }

    /**
     * Проверяет закончился ли прием данных с ПСТ.
     *
     * @return TRUE, если данные пришли полностью, иначе - FALSE
     */
    public boolean isEnd(byte [] buffer) {
        // символы завершения
        if (buffer.length != 0) {
            if (buffer[buffer.length - 1] == 0x0A) {
                mWorkThread.setStopSend(false);

                return true;
            }
        }

        return false;
    }

    /**
     * Возвращает строку составленную из символов полученных от ПСТ.
     *
     * @return строку составленную из символов полученных от ПСТ
     */
    public String getAnswer() {
        String str = "";

        for (int i = 0; i < pst_answer.length - 2; i++)
            str += (char) pst_answer[i];

        clearAnswer();

        return str;
    }

    /**
     * Устанавливает порт для доступа к его методам.
     * @param port порт
     */
    public void setPort(SerialPort port) {
        mPort = port;
    }

    /**
     * Добавляет поток запросов к ПСТ.
     * @param workThread поток запросов к ПСТ
     */
    public void setmWorkThread(WorkThread workThread) {
        mWorkThread = workThread;
    }

    /**
     * Очищает массив символов ответа от ПСТ.
     */
    private void clearAnswer() {
        pst_answer = new byte[0];
    }
}

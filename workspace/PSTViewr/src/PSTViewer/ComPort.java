package PSTViewer;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class ComPort {

    private SerialPort mPort;
    private EventListener mEventListener; // Слушатель порта
    private Controller mController;

    private WorkThread mWorkThread; // Поток запросов

    private String mComName; // Имя порта при обнаружении
    private boolean isFirstCheck = false; // При запуске была проведена проверка портов

    public ComPort(Controller controller) {
        mController = controller;
        mEventListener = new EventListener();
    }

    /**
     * До запуска опртса проверяет наличие активных портов.
     *
     * @return TRUE, если есть порты, иначе - FALSE
     */
    public boolean checkPortNames() {
        String [] portNames = SerialPortList.getPortNames();

        if (portNames.length == 0) {
            mController.setSplitMenu(portNames);
            System.out.println("COM порты не обнаружены.");
        } else {
            mController.setSplitMenu(portNames);

            System.out.println("Обнаружены COM порты: " + portNames[0]);

            return true;
        }

        return false;
    }

    public void initPort() {
        mWorkThread = new WorkThread(this, mEventListener, mController);

        mPort = new SerialPort(mComName);
        try {
            mPort.openPort();
            mPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            mPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            mPort.setEventsMask(SerialPort.MASK_RXCHAR);
            mPort.addEventListener(mEventListener);

            mWorkThread.startThread();

            mEventListener.setPort(mPort);
            mEventListener.setmWorkThread(mWorkThread);

//                mController.setWorkThread(mWorkThread);
        } catch (SerialPortException e) {
            System.out.println("Не удалось открыть порт.");
            e.printStackTrace();
        }
    }

    /**
     * Устанавливает имя рабочего порта.
     * @param name
     */
    public void setComName(String name) {
        mComName = name;
    }

    public String getComName() {
        return mComName;
    }

    /**
     * Отправляет символ в виде байта в ПСТ (запрос).
     *
     * @param word символ запроса в виде байта
     * @return TRUE, если запрос прошел успешно, иначе - FALSE
     */
    public boolean sendWord(byte word) {
        if (mPort.isOpened()) {
            try {
                return  mPort.writeByte(word);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Отанавливает поток запросов и закрывает СОМ порт.
     */
    public void closeComPort() {
        try {
            if (mWorkThread != null) {
                mWorkThread.stopThread();
                mWorkThread = null;
            }

            if (mPort!= null) {
                if (mPort.isOpened()) {
                    mPort.closePort();
                    mPort = null;
                }
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public SerialPort getPort() {
        return mPort;
    }

    public boolean isFirstCheck() {
        return isFirstCheck;
    }

    public void setFirstCheck(boolean firstCheck) {
        isFirstCheck = firstCheck;
    }
}

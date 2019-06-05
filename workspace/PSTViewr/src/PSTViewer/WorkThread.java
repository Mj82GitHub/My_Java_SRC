package PSTViewer;

import javafx.application.Platform;

import java.util.Timer;

public class WorkThread extends Thread implements Words {

    private ComPort mPort;
    private Controller mController;
    private EventListener mEventListener; // Слушатель порта

    private boolean isStop = false; // Останавливает поток
    private boolean isStopSend = true; // Приостановка запросов

    private String [] allAnswers = new String[rx_chars.length];

    public WorkThread(ComPort port, EventListener eventListener, Controller controller) {
        super("PSTWorkThread");

        mPort = port;
        mEventListener = eventListener;
        mController = controller;
    }

    /**
     * Работа потока.
     */
    @Override
    synchronized public void run() {
        while (!isStop) {
            try {
                for (int i = 0; i < rx_chars.length; i++) {
                    if (mPort.getPort().isOpened()) {
                        System.out.println("Запрос отправлен ...");

                        mPort.sendWord(rx_chars[i]);

                        while (isStopSend) {
                            if (isStop)
                                break;

                            wait();

//                            System.out.println("TIMER CANCEL");
                        }

//                        Thread.sleep(1000);

                        allAnswers[i] = mEventListener.getAnswer();
//                        System.out.println("ANSWER: " + mEventListener.getAnswer());

                        if (!isStop)
                            isStopSend = true;
                        else
                            break;
                    }
                }

                if (isStop)
                    break;

//                System.out.println("Отправка в UI ...");
                Platform.runLater(() -> mController.updateView(allAnswers));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Поток отправки запросов к ПСТ остановлен.");
    }

    /**
     * Запускает поток для запросов к ПСТ
     */
    public void startThread() {
        start();
    }

    /**
     * Останавливает поток для запросов к ПСТ.
     */
    synchronized public void stopThread() {
        System.out.println("Stop Thread.");
        isStop = true;
        isStopSend = false;
        notify();
    }

    public boolean isStopSend() {
        return isStopSend;
    }

    synchronized public void setStopSend(boolean stopSend) {
        isStopSend = stopSend;
        notify();
    }
}

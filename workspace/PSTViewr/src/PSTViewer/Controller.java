package PSTViewer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

public class Controller {

    private ComPort mComPort;
    private WorkThread mWorkThread;

    @FXML
    public Label lab_P;
    @FXML
    public Label lab_Ttnd;
    @FXML
    public Label lab_Ttvd;
    @FXML
    public Label lab_Timer;
    @FXML
    public Label lab_Valve;
    @FXML
    public Label lab_Flp;
    @FXML
    public Button btn;
    @FXML
    public SplitMenuButton split_menu;

    public void setWorkThread(WorkThread workThread) {
        mWorkThread = workThread;
    }

    public void setComPort(ComPort comPort) {
        mComPort = comPort;
    }

    @FXML
    public void setSplitMenu(String [] items) {
        if (items.length == 0) {
            split_menu.setText("COM#");
            btn.setDisable(true);
        } else {
            if (split_menu.getItems().size() != 0)
                split_menu.getItems().removeAll(split_menu.getItems());

            MenuItem [] menu = new MenuItem[items.length];

            for (int i = 0; i < items.length; i++) {
                MenuItem menuItem = new MenuItem(items[i]);
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        mComPort.setComName(menuItem.getText());
                        split_menu.setText(menuItem.getText());
                    }
                });

                menu[i] = menuItem;
            }

            split_menu.getItems().addAll(menu);

            if (!mComPort.isFirstCheck()) {
                mComPort.setComName(menu[0].getText());
                mComPort.setFirstCheck(true);

                split_menu.setText(menu[0].getText());
            } else {
                boolean isName = false; // В новом списке есть старый порт

                for (int i = 0; i < items.length; i++) {
                    if (items[i].equals(mComPort.getComName())) {
                        mComPort.setComName(items[i]);
                        isName = true;
                    }
                }

                // Если нет старого порта, то берем первый из новых
                if (!isName) {
                    mComPort.setComName(menu[0].getText());
                    split_menu.setText(menu[0].getText());
                }
            }
        }
    }

    @FXML
    private void buttonAction(ActionEvent event) {
        if (mComPort != null && mComPort.getPort() != null) {
            mComPort.closeComPort();
            btn.setText("Run");
        } else if (mComPort != null && mComPort.getPort()== null){
            if (mComPort.checkPortNames()) {
                mComPort.initPort();
                btn.setText("Stop");
            }
        }
    }

    @FXML
    public void portError() {
        if (mComPort != null && mComPort.getPort() != null) {
            mComPort.closeComPort();
            btn.setText("Run");

            String [] str = new String[6];

            for (int i= 0; i < 6; i++)
                str[i] = ".";

            updateView(str);
        }
    }

    @FXML
    public void updateView(String [] str) {
        lab_P.setText(str[0]);
        lab_Ttnd.setText(str[1]);
        lab_Ttvd.setText(str[2]);
        lab_Timer.setText(str[3]);
        lab_Valve.setText(str[4]);
        lab_Flp.setText(str[5]);
    }
}

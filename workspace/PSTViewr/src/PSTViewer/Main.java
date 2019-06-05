package PSTViewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private String app_name = "PST Viewer ";
    private String app_version = "v. 1.0";

    private ComPort mComPort;
    private Controller mController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("window.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle(app_name + app_version);
        primaryStage.setScene(new Scene(root, 220, 330));
        primaryStage.show();

        mController = loader.getController();

        mComPort = new ComPort(mController);
        mController.setComPort(mComPort);
        mComPort.checkPortNames();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Нажата кнопка закрытия окна.");
        mComPort.closeComPort();
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

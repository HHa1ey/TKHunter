package com.tkteam.start;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class MainStart extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/Main.fxml"));
        stage.setTitle("TK Hunter                 Author：Ha1ey@天魁战队");    //设置程序title
        stage.setScene(new Scene(root));
        // 退出程序的时候，子线程也一起退出，防止多线程卡顿
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("icon/icon.jpeg").toString()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
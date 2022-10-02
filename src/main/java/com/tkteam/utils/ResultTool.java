package com.tkteam.utils;


import javafx.scene.control.Alert;
import javafx.stage.Window;

import java.util.Random;


public class ResultTool {


    public static void alert(String alert_info){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest((e) -> {
            window.hide();
        });
        alert.setHeaderText(alert_info);
        alert.show();
    }

    //生成随机字符串
    public static String getRandomStr(int length){
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random =new Random();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i=0; i < length; ++i){
            int number = random.nextInt(62);   //随机产生0-61之间的一个数字
            stringBuffer.append(str.charAt(number));   //根据0-61索引的取字符添加进stringBuffer中
        }
        return stringBuffer.toString();
    }
}
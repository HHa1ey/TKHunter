package com.tkteam.utils;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkteam.bean.JsonBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Window;

import java.util.Random;


public class ResultTool {
    private String component_name;
    private String component_version;
    JsonBean jsonBean = new JsonBean();

    ObservableList<JsonBean> result_list = FXCollections.observableArrayList();
    public ObservableList<JsonBean> getObservableList(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json).getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("arr");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject arr_json_element = (JSONObject) jsonArray.get(i);
            //处理服务器指纹显示问题
            JSONArray component_arr = JSONArray.parseArray(arr_json_element.getString("component"));
            if (component_arr != null) {
                for (int j = 0; j < component_arr.size(); j++) {
                    JSONObject component_json = component_arr.getJSONObject(j);
                    this.component_name = component_json.getString("name");
                    this.component_version = component_json.getString("version");
                }
            }
            String component = "name:" + this.component_name + "\tversion:" + this.component_version;
            jsonBean.setId(i+1);
            jsonBean.setUrl(arr_json_element.getString("url"));
            jsonBean.setIp(arr_json_element.getString("ip"));
            jsonBean.setPort(arr_json_element.getString("port"));
            jsonBean.setWeb_title(arr_json_element.getString("web_title"));
            jsonBean.setDomain(arr_json_element.getString("domain"));
            jsonBean.setProtocol(arr_json_element.getString("protocol"));
            jsonBean.setBase_protocol(arr_json_element.getString("base_protocol"));
            jsonBean.setStatus_code(arr_json_element.getString("status_code"));
            jsonBean.setComponent(component);
            jsonBean.setCompany(arr_json_element.getString("company"));
            jsonBean.setNumber(arr_json_element.getString("number"));
            jsonBean.setCountry(arr_json_element.getString("country"));
            jsonBean.setProvince(arr_json_element.getString("province"));
            jsonBean.setCity(arr_json_element.getString("city"));
            jsonBean.setUpdated_at(arr_json_element.getString("updated_at"));
            jsonBean.setIs_web(arr_json_element.getString("is_web"));
            jsonBean.setAs_org(arr_json_element.getString("as_org"));
            jsonBean.setIsp(arr_json_element.getString("isp"));
            result_list.add(new JsonBean(jsonBean.getNumber(),jsonBean.getCountry(),jsonBean.getProvince(),jsonBean.getCity(),jsonBean.getUpdated_at(),jsonBean.getIs_web(),jsonBean.getAs_org(),jsonBean.getIsp(),jsonBean.getId(), jsonBean.getUrl(), jsonBean.getIp(), jsonBean.getPort(), jsonBean.getWeb_title(), jsonBean.getDomain(), jsonBean.getBase_protocol(), jsonBean.getProtocol(), jsonBean.getStatus_code(), jsonBean.getComponent(), jsonBean.getCompany()));
        }
        return result_list;
    }


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
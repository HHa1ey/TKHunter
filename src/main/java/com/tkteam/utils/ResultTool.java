package com.tkteam.utils;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkteam.bean.JsonBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Window;


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
            result_list.add(new JsonBean(jsonBean.getId(), jsonBean.getUrl(), jsonBean.getIp(), jsonBean.getPort(), jsonBean.getWeb_title(), jsonBean.getDomain(), jsonBean.getBase_protocol(), jsonBean.getProtocol(), jsonBean.getStatus_code(), jsonBean.getComponent(), jsonBean.getCompany()));
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
}
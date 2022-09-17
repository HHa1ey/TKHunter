package com.tkteam.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jfoenix.controls.JFXComboBox;;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.tkteam.bean.ColumnBean;
import com.tkteam.bean.JsonBean;
import com.tkteam.hunter.HunterSearch;
import com.tkteam.start.MainStart;
import com.tkteam.utils.ResultTool;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class Controller {
    @FXML
    private Text result_num;
    @FXML
    private TableColumn<JsonBean, String> result_base_protocol;
    @FXML
    private TableColumn<JsonBean, String> result_company;
    @FXML
    private TableColumn<JsonBean, Integer> result_id;
    @FXML
    private TableColumn<JsonBean, String> result_web_title;
    @FXML
    private TableColumn<JsonBean, String> result_status_code;
    @FXML
    private TableColumn<JsonBean, String> result_url;
    @FXML
    private TableColumn<JsonBean, String> result_ip;
    @FXML
    private TableColumn<JsonBean, String> result_port;
    @FXML
    private TableColumn<JsonBean, String> result_domain;
    @FXML
    private TableColumn<JsonBean, String> result_protocol;
    @FXML
    private TableColumn<JsonBean, String> result_component;
    @FXML
    public TableView<JsonBean> result_table;
    @FXML
    private TextField hunter_key;                 //hunter的key
    @FXML
    private TextField hunter_search_grammar;      //hunter搜索语法
    @FXML
    private JFXComboBox<String> hunter_is_web;              //资产类型选择
    @FXML
    private JFXComboBox<String> hunter_time;                //时间范围
    @FXML
    private JFXTextField hunter_status_code;         //状态码选择
    @FXML
    private TableView<ColumnBean> grammar_table;       //默认查询语法表
    @FXML
    private TableColumn<ColumnBean, String> grammar_content;       //语法内容
    @FXML
    private TableColumn<ColumnBean, String> grammar_explain;         //语法说明
    public static String starttime;
    public static String endtime;
    private String key;
    private String code;
    private String grammar;
    private String isweb_str;
    private String isweb_int;
    private String bs64_grammar;
    private String component_name;
    private String component_version;
    int page =1;
    int num_id;
    private String add_result_json;
    ObservableList<JsonBean> result_list = FXCollections.observableArrayList();

    //is_web选择资产类型
    private static final String[] is_webs = {
            "Web资产",        //1代表web资产
            "非Web资产",        //2代表非web资产
            "全部",        //3代表全部
    };

    //时间范围
    private static final String[] times = {
            "最近一个月",
            "最近半年",
            "最近一年",
            "自定义时间范围"
    };


    @FXML
    public void initialize() {
        this.hunter_is_web.setValue(is_webs[0]);
        for (String web : is_webs) {
            this.hunter_is_web.getItems().add(web);
        }
        this.hunter_time.setValue(times[0]);
        for (String time : times) {
            this.hunter_time.getItems().add(time);
        }

        //常用语法查询
        ObservableList<ColumnBean> grammar_list = FXCollections.observableArrayList();
        grammar_list.add(new ColumnBean("ip=\"1.1.1.1\"", "搜索IP为 ”1.1.1.1”的资产"));
        grammar_list.add(new ColumnBean("ip=\"220.181.111.1/24\"", "搜索起始IP为”220.181.111.1“的C段资产"));
        grammar_list.add(new ColumnBean("ip.port=\"6379\"", "搜索开放端口为”6379“的资产"));
        grammar_list.add(new ColumnBean("ip.country=\"CN\" 或 ip.country=\"中国\"", "搜索IP对应主机所在国为”中国“的资产"));
        grammar_list.add(new ColumnBean("ip.province=\"江苏\"", "搜索IP对应主机在江苏省的资产"));
        grammar_list.add(new ColumnBean("ip.city=\"北京\"", "搜索IP对应主机所在城市为”北京“市的资产"));
        grammar_list.add(new ColumnBean("ip.isp=\"电信\"", "搜索运营商为”中国电信”的资产"));
        grammar_list.add(new ColumnBean("ip.os=\"Windows\"", "搜索操作系统标记为”Windows“的资产"));
        grammar_list.add(new ColumnBean("app=\"Hikvision 海康威视 Firmware 5.0+\" && ip.ports=\"8000\"", "检索使用了Hikvision且ip开放8000端口的资产"));
        grammar_list.add(new ColumnBean("ip.port_count>\"2\"", "搜索开放端口大于2的IP（支持等于、大于、小于）"));
        grammar_list.add(new ColumnBean("ip.ports=\"80\" && ip.ports=\"443\"", "查询开放了80和443端口号的资产"));
        grammar_list.add(new ColumnBean("ip.tag=\"CDN\"", "查询包含IP标签\"CDN\"的资产"));
        grammar_list.add(new ColumnBean("is_domain=true", "搜索域名标记不为空的资产"));
        grammar_list.add(new ColumnBean("domain=\"qianxin.com\"", "搜索域名包含\"qianxin.com\"的网站"));
        grammar_list.add(new ColumnBean("domain.suffix=\"qianxin.com\"", "搜索主域为\"qianxin.com\"的网站"));
        grammar_list.add(new ColumnBean("header.server==\"Microsoft-IIS/10\"", "搜索server全名为“Microsoft-IIS/10”的服务器"));
        grammar_list.add(new ColumnBean("header.content_length=\"691\"", "搜索HTTP消息主体的大小为691的网站"));
        grammar_list.add(new ColumnBean("header.status_code=\"402\"", "搜索HTTP请求返回状态码为”402”的资产"));
        grammar_list.add(new ColumnBean("header=\"elastic\"", "搜索HTTP请求头中含有”elastic“的资产"));
        grammar_list.add(new ColumnBean("is_web=true", "搜索web资产"));
        grammar_list.add(new ColumnBean("web.title=\"北京\"", "从网站标题中搜索“北京”"));
        grammar_list.add(new ColumnBean("web.body=\"网络空间测绘\"", "搜索网站正文包含”网络空间测绘“的资产"));
        grammar_list.add(new ColumnBean("after=\"2021-01-01\" && before=\"2021-12-21\"", "搜索2021年的资产"));
        grammar_list.add(new ColumnBean("web.similar=\"baidu.com:443\"", "查询与baidu.com:443网站的特征相似的资产"));
        grammar_list.add(new ColumnBean("web.similar_icon==\"17262739310191283300\"", "查询网站icon与该icon相似的资产"));
        grammar_list.add(new ColumnBean("web.icon=\"22eeab765346f14faf564a4709f98548\"", "查询网站icon与该icon相同的资产"));
        grammar_list.add(new ColumnBean("web.similar_id=\"3322dfb483ea6fd250b29de488969b35\"", "查询与该网页相似的资产"));
        grammar_list.add(new ColumnBean("web.tag=\"登录页面\"", "查询包含资产标签\"登录页面\"的资产"));
        grammar_list.add(new ColumnBean("icp.number=\"京ICP备16020626号-8\"", "搜索通过域名关联的ICP备案号为”京ICP备16020626号-8”的网站资产"));
        grammar_list.add(new ColumnBean("icp.web_name=\"奇安信\"", "搜索ICP备案网站名中含有“奇安信”的资产"));
        grammar_list.add(new ColumnBean("icp.name=\"奇安信\"", "搜索ICP备案单位名中含有“奇安信”的资产"));
        grammar_list.add(new ColumnBean("icp.type=\"企业\"", "搜索ICP备案主体为“企业”的资产"));
        grammar_list.add(new ColumnBean("protocol=\"http\"", "搜索协议为”http“的资产"));
        grammar_list.add(new ColumnBean("protocol.transport=\"udp\"", "搜索传输层协议为”udp“的资产"));
        grammar_list.add(new ColumnBean("protocol.banner=\"nginx\"", "查询端口响应中包含\"nginx\"的资产"));
        grammar_list.add(new ColumnBean("app.name=\"小米 Router\"", "搜索标记为”小米 Router“的资产"));
        grammar_list.add(new ColumnBean("app.type=\"开发与运维\"", "查询包含组件分类为\"开发与运维\"的资产"));
        grammar_list.add(new ColumnBean("app.vendor=\"PHP\"", "查询包含组件厂商为\"PHP\"的资产"));
        grammar_list.add(new ColumnBean("app.version=\"1.8.1\"", "查询包含组件版本为\"1.8.1\"的资产"));
        grammar_list.add(new ColumnBean("cert=\"baidu\"", "搜索证书中带有baidu的资产"));
        grammar_list.add(new ColumnBean("cert.subject=\"qianxin.com\"", "搜索证书使用者是qianxin.com的资产"));
        grammar_list.add(new ColumnBean("cert.subject_org=\"奇安信科技集团股份有限公司\"", "搜索证书使用者组织是奇安信科技集团股份有限公司的资产"));
        grammar_list.add(new ColumnBean("cert.issuer=\"Let's Encrypt Authority X3\"", "搜索证书颁发者是Let's Encrypt Authority X3的资产"));
        grammar_list.add(new ColumnBean("cert.issuer_org=\"Let's Encrypt\"", "搜索证书颁发者组织是Let's Encrypt的资产"));
        grammar_list.add(new ColumnBean("cert.sha-1=\"be7605a3b72b60fcaa6c58b6896b9e2e7442ec50\"", "搜索证书签名哈希算法sha1为be7605a3b72b60fcaa6c58b6896b9e2e7442ec50的资产"));
        grammar_list.add(new ColumnBean("cert.sha-256=\"4e529a65512029d77a28cbe694c7dad1e60f98b5cb89bf2aa329233acacc174e\"", "搜索证书签名哈希算法sha256为4e529a65512029d77a28cbe694c7dad1e60f98b5cb89bf2aa329233acacc174e的资产"));
        grammar_list.add(new ColumnBean("cert.sha-md5=\"aeedfb3c1c26b90d08537523bbb16bf1\"\n", "搜索证书签名哈希算法shamd5为aeedfb3c1c26b90d08537523bbb16bf1的资产"));
        grammar_list.add(new ColumnBean("cert.serial_number=\"35351242533515273557482149369\"", "搜索证书序列号是35351242533515273557482149369的资产"));
        grammar_list.add(new ColumnBean("cert.is_expired=true", "搜索证书已过期的资产"));
        grammar_list.add(new ColumnBean("cert.is_trust=true", "搜索证书可信的资产"));
        grammar_list.add(new ColumnBean("as.number=\"136800\"", "搜索asn为\"136800\"的资产"));
        grammar_list.add(new ColumnBean("as.name=\"CLOUDFLARENET\"", "搜索asn名称为\"CLOUDFLARENET\"的资产"));
        grammar_list.add(new ColumnBean("as.org=\"PDR\"", "搜索asn注册机构为\"PDR\"的资产"));
        grammar_list.add(new ColumnBean("tls-jarm.hash=\"21d19d00021d21d21c21d19d21d21da1a818a999858855445ec8a8fdd38eb5\"", "搜索tls-jarm哈希为21d19d00021d21d21c21d19d21d21da1a818a999858855445ec8a8fdd38eb5的资产"));
        grammar_list.add(new ColumnBean("tls-jarm.ans=\"c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017,00c0|0303|h2|ff01-0000-0001-0023-0010-0017,|||,c013|0303||ff01-0000-0001-000b-0023-0017,c013|0303||ff01-0000-0001-000b-0023-0017,c013|0302|h2|ff01-0000-0001-000b-0023-0010-0017,c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017,00c0|0303|h2|ff01-0000-0001-0023-0010-0017,c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017,c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017\"", "搜索tls-jarmANS为c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017,00c0|0303|h2|ff01-0000-0001-0023-0010-0017,|||,c013|0303||ff01-0000-0001-000b-0023-0017,c013|0303||ff01-0000-0001-000b-0023-0017,c013|0302|h2|ff01-0000-0001-000b-0023-0010-0017,c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017,00c0|0303|h2|ff01-0000-0001-0023-0010-0017,c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017,c013|0303|h2|ff01-0000-0001-000b-0023-0010-0017的资产"));
        grammar_content.setCellValueFactory(new PropertyValueFactory<>("content"));
        grammar_explain.setCellValueFactory(new PropertyValueFactory<>("explain"));
        grammar_table.setItems(grammar_list);


        //自定义时间选项
        this.hunter_time.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (hunter_time.getValue().trim().equals("自定义时间范围")){
                AnchorPane anchorPane;
                Stage stage = new Stage();
                try {
                    anchorPane = FXMLLoader.load(getClass().getResource("/fxml/Date.fxml"));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JFXDatePicker custom_end_time = (JFXDatePicker) anchorPane.lookup("#custom_end_time");
                JFXDatePicker custom_start_time = (JFXDatePicker) anchorPane.lookup("#custom_start_time");
                Button callback_button = (Button) anchorPane.lookup("#callback_button");
                callback_button.setOnAction(event -> {
                    starttime=custom_start_time.getValue()+"%2000:00:00";
                    endtime=custom_end_time.getValue()+"%2000:00:00";
                    this.hunter_time.setValue(custom_start_time.getValue()+"到"+custom_end_time.getValue());
                    stage.close();          //点击确定关闭stage
                });
                stage.setScene(new Scene(anchorPane));
                stage.setTitle("请选择时间区间");
                stage.show();
            }
        });

    }


    @FXML
    public void hunterSearch() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        //接收前端传参处理
        if (hunter_key.getText().trim().equals("")){
            ResultTool.alert("API-Key不能为空！！");
        }else {
            this.key=hunter_key.getText().trim();
        }

        if (hunter_search_grammar.getText().trim().equals("")){
            ResultTool.alert("搜索语法不能为空！！");
        }else {
            this.grammar=hunter_search_grammar.getText().trim();
        }
        if (hunter_status_code.getText().equals("默认200 逗号分割")){
            this.code ="200";
        }else {
            this.code = hunter_status_code.getText();
        }

        this.isweb_str = this.hunter_is_web.getValue().trim();
        if (isweb_str.equals("Web资产")) {
            isweb_int = "1";
        } else if (isweb_str.equals("非Web资产")) {
            isweb_int = "2";
        } else {
            isweb_int = "3";
        }

        this.bs64_grammar = Base64.getUrlEncoder().encodeToString(grammar.getBytes());


        //资产时间处理
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd%20hh:mm:ss");
        String now = simpleDateFormat.format(date);
        String month = simpleDateFormat.format(new Date(date.getTime() - 31L * 24 * 60 * 60 * 1000));
        String half_year = simpleDateFormat.format(new Date(date.getTime() - 186L * 24 * 60 * 60 * 1000));
        String one_year = simpleDateFormat.format(new Date(date.getTime() - 366L * 24 * 60 * 60 * 1000));

        if (hunter_time.getValue().trim().equals("最近一个月")) {
            starttime = month;
            endtime = now;
        } else if (hunter_time.getValue().trim().equals("最近半年")) {
            starttime = half_year;
            endtime = now;
        } else if (hunter_time.getValue().trim().equals("最近一年")) {
            starttime = one_year;
            endtime = now;
       }


        String result_json = new HunterSearch().getResult(key, bs64_grammar, isweb_int, 1, code, starttime, endtime);

        //获取查询结果总量
        JSONObject jsonObj_total = JSONObject.parseObject(result_json).getJSONObject("data");
        int total = jsonObj_total.getInteger("total");
        result_num.setText(total + "条");

        this.result_list = new ResultTool().getObservableList(result_json);
        result_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        result_ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
        result_url.setCellValueFactory(new PropertyValueFactory<>("url"));
        result_port.setCellValueFactory(new PropertyValueFactory<>("port"));
        result_web_title.setCellValueFactory(new PropertyValueFactory<>("web_title"));
        result_domain.setCellValueFactory(new PropertyValueFactory<>("domain"));
        result_protocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        result_base_protocol.setCellValueFactory(new PropertyValueFactory<>("base_protocol"));
        result_status_code.setCellValueFactory(new PropertyValueFactory<>("status_code"));
        result_component.setCellValueFactory(new PropertyValueFactory<>("component"));
        result_company.setCellValueFactory(new PropertyValueFactory<>("company"));
        result_table.setItems(this.result_list);

        //表格添加可编辑属性
        result_table.setEditable(true);
        result_url.setCellFactory(TextFieldTableCell.forTableColumn());
        result_ip.setCellFactory(TextFieldTableCell.forTableColumn());
        result_company.setCellFactory(TextFieldTableCell.forTableColumn());
        result_web_title.setCellFactory(TextFieldTableCell.forTableColumn());
        result_company.setCellFactory(TextFieldTableCell.forTableColumn());
        result_component.setCellFactory(TextFieldTableCell.forTableColumn());

        //双击默认浏览器打开资产URL实现方法
        result_table.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2 && !(result_table.getColumns().isEmpty())) {
                String open_url = result_table.getSelectionModel().getSelectedItem().getUrl();
                URI uri = URI.create(open_url);
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        num_id=(page-1)+101;
        //自动翻页实现--监听事件
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2.2));
        pauseTransition.setOnFinished(event -> {
            ScrollBar scrollBar = (ScrollBar) result_table.lookup(".scroll-bar:vertical");
            scrollBar.valueProperty().addListener(((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() >= scrollBar.getMax()&&this.page<total/100+1){
                    this.page+=1;
                    try {
                        this.add_result_json = new HunterSearch().getResult(key, bs64_grammar, isweb_int,page, code, starttime, endtime);
                    } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | KeyManagementException e) {
                        throw new RuntimeException(e);
                    }
                    JSONObject add_jsonObj = JSONObject.parseObject(this.add_result_json);
                    JSONObject add_arrjson = JSONObject.parseObject(this.add_result_json).getJSONObject("data");
                    String is_arr = add_arrjson.getString("arr");
                    if (!(add_jsonObj.getString("data").equals("null")) && !(is_arr.equals("null"))) {
                        addData(result_table);
                    }
                }
            }));
        });
        pauseTransition.playFromStart();
    }

    //自动翻页实现--获取剩余数据监听事件后续触发
    private void addData(TableView<JsonBean> tableView) {
        JsonBean jsonBean = new JsonBean();
        Task<ObservableList<JsonBean>> task = new Task<ObservableList<JsonBean>>() {
            @Override
            protected ObservableList<JsonBean> call() {
                JSONObject jsonObj = JSONObject.parseObject(add_result_json).getJSONObject("data");
                JSONArray arr_json = jsonObj.getJSONArray("arr");
                for (Object o : arr_json) {
                    JSONObject arr_json_element = (JSONObject) o;
                    //处理服务器指纹显示问题
                    JSONArray component_arr = JSONArray.parseArray(arr_json_element.getString("component"));
                    if (component_arr != null) {
                        for (int h = 0; h < component_arr.size(); h++) {
                            JSONObject component_json = component_arr.getJSONObject(h);
                            component_name = component_json.getString("name");
                            component_version = component_json.getString("version");
                        }
                    }
                    String component = "name:" + component_name + "\tversion:" + component_version;
                    jsonBean.setId(num_id);
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
                    num_id+=1;
                    result_list.add(new JsonBean(jsonBean.getId(),jsonBean.getUrl(), jsonBean.getIp(), jsonBean.getPort(), jsonBean.getWeb_title(), jsonBean.getDomain(), jsonBean.getBase_protocol(), jsonBean.getProtocol(), jsonBean.getStatus_code(), jsonBean.getComponent(), jsonBean.getCompany()));
                    result_table.setItems(result_list);
                }
                return result_list;
            }
        };
        tableView.scrollTo(tableView.getItems().size()-20);
        new Thread(task).start();
    }

}

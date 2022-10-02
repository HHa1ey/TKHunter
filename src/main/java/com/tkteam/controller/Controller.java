package com.tkteam.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jfoenix.controls.*;
import com.tkteam.bean.ColumnBean;
import com.tkteam.bean.JsonBean;
import com.tkteam.bean.Response;
import com.tkteam.hunter.HunterSearch;
import com.tkteam.utils.HttpTool;
import com.tkteam.utils.ResultTool;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


public class Controller {
    public static HashMap<String,Object> setProxy=new HashMap<>();
    @FXML
    private JFXButton exportButton;
    @FXML
    private JFXTabPane resultTabPane;
    @FXML
    private TextArea batchArea;
    @FXML
    private MenuItem github;
    @FXML
    private MenuItem proxy;
    @FXML
    private MenuItem apikey;

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
    public  String starttime;
    public  String endtime;
    public String key;
    public String code;
    public String grammar;
    public String isweb_str;
    public String isweb_int;
    public String bs64_grammar;
    private final HashMap<String,String> headers=new HashMap<>();

    private AnchorPane api_key_pane;

    private TextField hunter_key;

    private String task_id;

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
    public void hunterSearch() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        if (hunter_key.getText().trim().equals("")){
            Properties properties = new Properties();
            try {
                FileInputStream fis = new FileInputStream("Hunter.properties");
                properties.load(fis);
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!properties.getProperty("Hunter_Key").equals("")){
                hunter_key.setText(properties.getProperty("Hunter_Key"));
                key=hunter_key.getText().trim();
            }else {ResultTool.alert("请填写Api-Key!!");}
        }

        if (hunter_search_grammar.getText().trim().equals("")){
            ResultTool.alert("搜索语法不能为空！！");
        }else {
            this.grammar=hunter_search_grammar.getText().trim();
        }
        if (hunter_status_code.getText().equals("默认200 逗号分割")){
            this.code ="200";
        }else {
            this.code = hunter_status_code.getText().trim();
        }

        this.isweb_str = this.hunter_is_web.getValue().trim();
        if (isweb_str.equals("Web资产")) {
            isweb_int = "1";
        } else if (isweb_str.equals("非Web资产")) {
            isweb_int = "2";
        } else {
            isweb_int = "3";
        }

        this.bs64_grammar = Base64.getUrlEncoder().encodeToString(grammar.getBytes(StandardCharsets.UTF_8));


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
        handleData();
        exportFile();
    }


    //自定义时间
    private void customDate(){
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

    //apikey模块
    private void addApiKey(){
        try {
            api_key_pane=FXMLLoader.load(getClass().getResource("/fxml/ApiKey.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JFXButton backbutton = (JFXButton)api_key_pane.lookup("#key_back");
        JFXButton cancelbutton = (JFXButton)api_key_pane.lookup("#cancel_button");
        hunter_key = (TextField) api_key_pane.lookup("#hunter_key");
        apikey.setOnAction(event -> {
            Alert api_key_dialog = new Alert(Alert.AlertType.NONE);
            api_key_dialog.setResizable(true);
            api_key_dialog.setTitle("设置Api-Key");
            Window window = api_key_dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest((e) -> {
                window.hide();
            });
            //判断读取本地文件中的key
            File key_file = new File("Hunter.properties");
            Properties properties = new Properties();
            if (!key_file.exists()){
                try {
                    properties.setProperty("Hunter_Key","");
                    properties.store(new FileWriter(key_file),null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ResultTool.alert("未检测到存在Key配置文件，已自动创建");
            }
            try {
                FileInputStream fis = new FileInputStream("Hunter.properties");
                properties.load(fis);
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!properties.getProperty("Hunter_Key").equals("")){
                hunter_key.setText(properties.getProperty("Hunter_Key"));
            }
            backbutton.setOnAction(event1 -> {
                key=properties.getProperty("Hunter_Key");
                key=hunter_key.getText().trim();
                if (!key.equals("")){
                    properties.setProperty("Hunter_Key",key);
                    try {
                        properties.store(new FileWriter(key_file),null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    api_key_dialog.getDialogPane().getScene().getWindow().hide();
                }else {
                    ResultTool.alert("请填写Api-Key!!");
                }
            });

            cancelbutton.setOnAction(event1 -> {
                api_key_dialog.getDialogPane().getScene().getWindow().hide();
            });

            api_key_dialog.getDialogPane().setContent(api_key_pane);
            api_key_dialog.showAndWait();
        });
    }

    private void exportFile(){
        resultTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                TableView<JsonBean> tableView = (TableView<JsonBean>) resultTabPane.getSelectionModel().getSelectedItem().getContent().lookup("#tableView");
                exportButton.setOnAction(event -> {
                    ObservableList<JsonBean> exportData = FXCollections.observableArrayList();
                    exportData.addAll(tableView.getItems());
                    //文件导出实现
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
                    fileChooser.setTitle("选择导出CSV路径");
                    fileChooser.getExtensionFilters().add(extensionFilter);
                    File file = fileChooser.showSaveDialog(new Stage());
                    Writer writer;
                    try {
                        writer = new BufferedWriter(new FileWriter(file));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        writer.write("URL"+","+"IP"+","+"端口"+","+"Web_title"+","+"domain"+","+"protocol"+","+"base_protocol"+","+"状态码"+","+"中间件"+","+"ICP公司名"+","+"备案号"+","+"国家"+","+"省"+","+"市"+","+"更新时间"+","+"是否是Web资产"+","+"as_org"+","+"ISP"+"\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    for (JsonBean bean : exportData){
                        String csv_info = bean.getUrl()+","+bean.getIp()+","+bean.getPort()+","+bean.getWeb_title()+","+bean.getDomain()+","+bean.getProtocol()+","+bean.getBase_protocol()+","+bean.getStatus_code()+","+bean.getComponent()+","+bean.getCompany()+","+bean.getNumber()+","+bean.getCountry()+","+bean.getProvince()+","+bean.getCity()+","+bean.getUpdated_at()+","+bean.getIs_web()+","+bean.getAs_org()+","+bean.getIsp()+"\n";
                        try {
                            writer.write(csv_info);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        writer.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    //代理模块
    private void addProxy(){
        AnchorPane proxy_pane;
        ToggleGroup toggleGroup = new ToggleGroup();
        try {
            proxy_pane = FXMLLoader.load(getClass().getResource("/fxml/Proxy.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TextField field_ip = (TextField) proxy_pane.lookup("#autoproxy_ip");
        TextField field_port = (TextField) proxy_pane.lookup("#autoproxy_port");
        TextField field_user = (TextField) proxy_pane.lookup("#autoproxy_user");
        TextField field_pass = (TextField) proxy_pane.lookup("#autoproxy_pass");

        RadioButton rb_enable = (RadioButton) proxy_pane.lookup("#proxy_enable");
        RadioButton rb_disable = (RadioButton) proxy_pane.lookup("#proxy_disable");

        ComboBox<String> proxy_cbb = (ComboBox<String>)proxy_pane.lookup("#choose_proxy");

        JFXButton cancel_button = (JFXButton) proxy_pane.lookup("#cancel_button");
        JFXButton save_button =(JFXButton) proxy_pane.lookup("#save_button");
        proxy.setOnAction(event -> {
            Alert dialog = new Alert(Alert.AlertType.NONE);
            dialog.setResizable(true);
            dialog.setTitle("设置代理");
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest((e) -> {
                window.hide();
            });

            //开启or关闭
            rb_enable.setToggleGroup(toggleGroup);
            rb_disable.setSelected(true);
            rb_disable.setToggleGroup(toggleGroup);
            //代理类型选择

            proxy_cbb.setItems(FXCollections.observableArrayList("HTTP","SOCKS"));
            proxy_cbb.getSelectionModel().select(0);

            if (setProxy.get("proxy")!=null){
                Proxy curr_proxy = (Proxy) setProxy.get("proxy");
                String proxy_info = curr_proxy.address().toString();
                String[] info = proxy_info.split(":");
                String ipaddr = info[0].replace("/","");
                String port = info[1];
                field_port.setText(port);
                field_ip.setText(ipaddr);
                rb_enable.setSelected(true);
            }else {
                rb_disable.setSelected(true);
            }


            //保存代理参数
            save_button.setOnAction(event1 -> {
                String proxy_ip = field_ip.getText().trim();
                String proxy_port = field_port.getText().trim();
                String proxy_user = field_user.getText().trim();
                String proxy_pass = field_pass.getText().trim();
                if (rb_disable.isSelected()){
                    setProxy.put("proxy",null);
                }else {
                    if (!proxy_user.equals("")){
                        Authenticator.setDefault(new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(proxy_user,proxy_pass.toCharArray());
                            }
                        });
                    }else {
                        Authenticator.setDefault(null);
                    }
                    setProxy.put("username",proxy_user);
                    setProxy.put("password",proxy_pass);
                    InetSocketAddress socketAddress = new InetSocketAddress(proxy_ip,Integer.parseInt(proxy_port));
                    Proxy proxy;
                    if (proxy_cbb.getValue().equals("HTTP")){
                        proxy = new Proxy(Proxy.Type.HTTP,socketAddress);
                        setProxy.put("proxy",proxy);
                    } else if (proxy_cbb.getValue().equals("SOCKS")) {
                        proxy = new Proxy(Proxy.Type.SOCKS,socketAddress);
                        setProxy.put("proxy",proxy);
                    }
                    field_ip.setText(proxy_ip);
                    field_port.setText(proxy_port);
                }
                dialog.getDialogPane().getScene().getWindow().hide();
            });

            //不保存
            cancel_button.setOnAction(event1 -> {
                dialog.getDialogPane().getScene().getWindow().hide();
            });
            dialog.getDialogPane().setContent(proxy_pane);
            dialog.showAndWait();
        });
    }

    //关于模块
    private void getAbout(){
        github.setOnAction(event -> {
            URI uri = URI.create("https://github.com/HHa1ey/TKHunter");
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void batchQuery() throws NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyManagementException {
        if (hunter_key.getText().trim().equals("")){
            Properties properties = new Properties();
            try {
                FileInputStream fis = new FileInputStream("Hunter.properties");
                properties.load(fis);
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!properties.getProperty("Hunter_Key").equals("")){
                hunter_key.setText(properties.getProperty("Hunter_Key"));
                key=hunter_key.getText().trim();
            }else {ResultTool.alert("请填写Api-Key!!");}
        }

        if (hunter_search_grammar.getText().trim().equals("")){
            ResultTool.alert("搜索语法不能为空！！");
        }else {
            this.grammar=hunter_search_grammar.getText().trim();
        }
        if (hunter_status_code.getText().equals("默认200 逗号分割")){
            this.code ="200";
        }else {
            this.code = hunter_status_code.getText().trim();
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

        String random_str = ResultTool.getRandomStr(16);
        String filename = ResultTool.getRandomStr(4);
        String batchip = batchArea.getText();
        String batchdata = "--------------------------"+random_str+"\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\""+filename+".csv\"\r\n" +
                "Content-Type: application/octet-stream\r\n" +
                "\r\n"+
                batchip+
                "\r\n"+
                "--------------------------"+random_str+"--\r\n";
        String url = "https://hunter.qianxin.com/openApi/search/batch?api-key="+key+"&search="+bs64_grammar+"&is_web="+isweb_int+"&status_code="+code+"&start_time="+"%22"+starttime+"%22"+"&end_time="+"%22"+endtime+"%22";
        this.headers.put("Content-Type","multipart/form-data; boundary=------------------------"+random_str);
        Response response = HttpTool.post(url,this.headers,batchdata);
        String resp_json = response.getText();
        JSONObject jsonObject = JSONObject.parseObject(resp_json).getJSONObject("data");
        task_id = jsonObject.getString("task_id");
    }

    @FXML
    private void batchExport() throws NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyManagementException {
        String url = "https://hunter.qianxin.com/openApi/search/download/"+task_id+"?api-key="+key;
        Response response = HttpTool.get(url,this.headers);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.setTitle("请选择保存路径");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(new Stage());
        Writer writer = new BufferedWriter(new FileWriter(file));
        writer.write(response.getText());
        writer.close();
    }

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


        //加载自定义时间范围
        customDate();

        //加载apikey模块
        addApiKey();;

        //加载代理模块
        addProxy();

        //加载关于
        getAbout();

    }


    public void handleData() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        String result_json = new HunterSearch().getResult(key, bs64_grammar, isweb_int, 1, code, starttime, endtime);
        if (result_json.contains("令牌过期")){
            ResultTool.alert("令牌过期，请输入有效的Key");
        }
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/fxml/Tab.fxml"));
        Tab tab = new Tab();
        TableView<JsonBean> tableView =  (TableView<JsonBean>) anchorPane.lookup("#tableView");
        tableView.setEditable(true);
        tab.setText(grammar);
        tab.setContent(anchorPane);
        resultTabPane.getTabs().add(tab);
        resultTabPane.getSelectionModel().selectLast();

        //获取查询数据总量
        Text result_num = (Text) anchorPane.lookup("#result_num");
        JSONObject jsonObj_total = JSONObject.parseObject(result_json).getJSONObject("data");
        int total = jsonObj_total.getInteger("total");
        result_num.setText("查询数据总量为："+total + "条");
        TableColumn<JsonBean,String> column_id = (TableColumn<JsonBean, String>) tableView.getColumns().get(0);
        TableColumn<JsonBean,String> column_web_title = (TableColumn<JsonBean, String>) tableView.getColumns().get(1);
        TableColumn<JsonBean,String> column_status_code = (TableColumn<JsonBean, String>) tableView.getColumns().get(2);
        TableColumn<JsonBean,String> column_url = (TableColumn<JsonBean, String>) tableView.getColumns().get(3);
        TableColumn<JsonBean,String> column_ip = (TableColumn<JsonBean, String>) tableView.getColumns().get(4);
        TableColumn<JsonBean,String> column_port = (TableColumn<JsonBean, String>) tableView.getColumns().get(5);
        TableColumn<JsonBean,String> column_domain = (TableColumn<JsonBean, String>) tableView.getColumns().get(6);
        TableColumn<JsonBean,String> column_protocol = (TableColumn<JsonBean, String>) tableView.getColumns().get(7);
        TableColumn<JsonBean,String> column_base_protocol = (TableColumn<JsonBean, String>) tableView.getColumns().get(8);
        TableColumn<JsonBean,String> column_component = (TableColumn<JsonBean, String>) tableView.getColumns().get(9);
        TableColumn<JsonBean,String> column_company = (TableColumn<JsonBean, String>) tableView.getColumns().get(10);
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
        column_url.setCellValueFactory(new PropertyValueFactory<>("url"));
        column_port.setCellValueFactory(new PropertyValueFactory<>("port"));
        column_web_title.setCellValueFactory(new PropertyValueFactory<>(new String("web_title".getBytes(StandardCharsets.UTF_8))));
        column_domain.setCellValueFactory(new PropertyValueFactory<>("domain"));
        column_protocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        column_base_protocol.setCellValueFactory(new PropertyValueFactory<>("base_protocol"));
        column_status_code.setCellValueFactory(new PropertyValueFactory<>("status_code"));
        column_component.setCellValueFactory(new PropertyValueFactory<>("component"));
        column_company.setCellValueFactory(new PropertyValueFactory<>("company"));
        //表格添加可编辑属性
        column_url.setCellFactory(TextFieldTableCell.forTableColumn());
        column_ip.setCellFactory(TextFieldTableCell.forTableColumn());
        column_company.setCellFactory(TextFieldTableCell.forTableColumn());
        column_web_title.setCellFactory(TextFieldTableCell.forTableColumn());
        column_company.setCellFactory(TextFieldTableCell.forTableColumn());
        column_component.setCellFactory(TextFieldTableCell.forTableColumn());
        //序号递增
        column_id.setCellFactory((tableColumn) -> {
            TableCell<JsonBean, String> tableCell = new TableCell<JsonBean,String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        this.setText(String.valueOf(this.getIndex() + 1));
                    }
                }
            };
            return tableCell;
        });
        //双击默认浏览器打开资产URL实现方法
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2 && !(tableView.getColumns().isEmpty())) {
                String open_url = tableView.getSelectionModel().getSelectedItem().getUrl();
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


        JsonBean jsonBean1 = new JsonBean();
        ObservableList<JsonBean> result_list = FXCollections.observableArrayList();
        JSONObject jsonObject = JSONObject.parseObject(result_json).getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("arr");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject arr_json_element = (JSONObject) jsonArray.get(i);
            //处理服务器指纹显示问题
            JSONArray component_arr = JSONArray.parseArray(arr_json_element.getString("component"));
            String component_name = null;
            String component_version = null;
            if (component_arr != null) {
                for (int j = 0; j < component_arr.size(); j++) {
                    JSONObject component_json = component_arr.getJSONObject(j);
                    component_name = component_json.getString("name");
                    component_version = component_json.getString("version");
                }
            }
            String component = "name:" + component_name + "\tversion:" + component_version;
            jsonBean1.setUrl(arr_json_element.getString("url"));
            jsonBean1.setIp(arr_json_element.getString("ip"));
            jsonBean1.setPort(arr_json_element.getString("port"));
            jsonBean1.setWeb_title(arr_json_element.getString("web_title"));
            jsonBean1.setDomain(arr_json_element.getString("domain"));
            jsonBean1.setProtocol(arr_json_element.getString("protocol"));
            jsonBean1.setBase_protocol(arr_json_element.getString("base_protocol"));
            jsonBean1.setStatus_code(arr_json_element.getString("status_code"));
            jsonBean1.setComponent(component);
            jsonBean1.setCompany(arr_json_element.getString("company"));
            jsonBean1.setNumber(arr_json_element.getString("number"));
            jsonBean1.setCountry(arr_json_element.getString("country"));
            jsonBean1.setProvince(arr_json_element.getString("province"));
            jsonBean1.setCity(arr_json_element.getString("city"));
            jsonBean1.setUpdated_at(arr_json_element.getString("updated_at"));
            jsonBean1.setIs_web(arr_json_element.getString("is_web"));
            jsonBean1.setAs_org(arr_json_element.getString("as_org"));
            jsonBean1.setIsp(arr_json_element.getString("isp"));
            result_list.add(new JsonBean(jsonBean1.getNumber(),jsonBean1.getCountry(),jsonBean1.getProvince(),jsonBean1.getCity(),jsonBean1.getUpdated_at(),jsonBean1.getIs_web(),jsonBean1.getAs_org(),jsonBean1.getIsp(), jsonBean1.getUrl(), jsonBean1.getIp(), jsonBean1.getPort(), jsonBean1.getWeb_title(), jsonBean1.getDomain(), jsonBean1.getBase_protocol(), jsonBean1.getProtocol(), jsonBean1.getStatus_code(), jsonBean1.getComponent(), jsonBean1.getCompany()));
        }
        tableView.setItems(result_list);

        AtomicInteger page = new AtomicInteger(1);
        //自动翻页实现--监听事件
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2.2));
        pauseTransition.setOnFinished(event -> {
            ScrollBar scrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
            scrollBar.valueProperty().addListener(((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() >= scrollBar.getMax()){
                    bs64_grammar = Base64.getUrlEncoder().encodeToString(tab.getText().getBytes(StandardCharsets.UTF_8));
                    String add_result_json;
                    page.addAndGet(1);
                    try {
                        add_result_json = new HunterSearch().getResult(key, bs64_grammar, isweb_int, page.get(),code, starttime, endtime);
                    } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | KeyManagementException e) {
                        throw new RuntimeException(e);
                    }

                    JSONObject add_jsonObj = JSONObject.parseObject(add_result_json);
                    JSONObject add_arrjson = JSONObject.parseObject(add_result_json).getJSONObject("data");
                    String is_arr = add_arrjson.getString("arr");
                    if (!(add_jsonObj.getString("data").equals("null")) && !(is_arr.equals("null"))) {
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
                                    String component_name = null;
                                    String component_version = null;
                                    if (component_arr != null) {
                                        for (int h = 0; h < component_arr.size(); h++) {
                                            JSONObject component_json = component_arr.getJSONObject(h);
                                            component_name = component_json.getString("name");
                                            component_version = component_json.getString("version");
                                        }
                                    }
                                    String component = "name:" + component_name + "\tversion:" + component_version;
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
                                    result_list.add(new JsonBean(jsonBean.getNumber(),jsonBean.getCountry(),jsonBean.getProvince(),jsonBean.getCity(),jsonBean.getUpdated_at(),jsonBean.getIs_web(),jsonBean.getAs_org(),jsonBean.getIsp(), jsonBean.getUrl(), jsonBean.getIp(), jsonBean.getPort(), jsonBean.getWeb_title(), jsonBean.getDomain(), jsonBean.getBase_protocol(), jsonBean.getProtocol(), jsonBean.getStatus_code(), jsonBean.getComponent(), jsonBean.getCompany()));
                                    tableView.setItems(result_list);
                                }
                                return result_list;
                            }
                        };
                        tableView.scrollTo(tableView.getItems().size()-10);
                        new Thread(task).start();
                    }
                }
            }));
        });
        pauseTransition.playFromStart();


    }
}
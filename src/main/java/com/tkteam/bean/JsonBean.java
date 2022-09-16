package com.tkteam.bean;

public class JsonBean {
    private  String url;
    private  String port;
    private  String ip;
    private  String web_title;
    private  String domain;
    private  String protocol;
    private  String base_protocol;
    private  String status_code;
    private  String component;
    private  String company;
    private int id;

    public JsonBean(int id,String url, String ip, String port, String web_title, String domain, String base_protocol, String protocol, String status_code, String component,String company) {
        this.id=id;
        this.url = url;
        this.ip = ip;
        this.port = port;
        this.web_title = web_title;
        this.domain = domain;
        this.component = component;
        this.status_code = status_code;
        this.protocol = protocol;
        this.base_protocol =base_protocol;
        this.company=company;
    }

    public JsonBean() {

    }


    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public String getIp(){
        return ip;
    }
    public void setIp(String ip){
        this.ip=ip;
    }
    public String getPort(){
        return port;
    }
    public void setPort(String port){
        this.port=port;
    }

    public String getWeb_title(){
        return web_title;
    }
    public void setWeb_title(String web_title){
        this.web_title=web_title;
    }
    public String getDomain(){
        return domain;
    }
    public void setDomain(String domain){
        this.domain=domain;
    }
    public String getBase_protocol(){
        return base_protocol;
    }
    public void setBase_protocol(String base_protocol){
        this.base_protocol=base_protocol;
    }
    public String getProtocol(){
        return protocol;
    }
    public void setProtocol(String protocol){
        this.protocol=protocol;
    }
    public String getStatus_code(){
        return status_code;
    }
    public void setStatus_code(String status_code){
        this.status_code=status_code;
    }
    public String getComponent(){
        return component;
    }
    public void setComponent(String component){
        this.component=component;
    }
    public String getCompany(){
        return this.company;
    }
    public void setCompany(String company){
        this.company=company;
    }

    public void setId(int id) {
        this.id=id;
    }

    public int getId() {
        return this.id;
    }
}

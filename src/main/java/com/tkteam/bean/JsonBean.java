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
    private String number;
    private String country;
    private String province;
    private String city;
    private String updated_at;
    private String is_web;
    private String as_org;
    private String isp;

    public JsonBean(String number,String country,String province,String city,String updated_at,String is_web,String as_org,String isp,int id,String url, String ip, String port, String web_title, String domain, String base_protocol, String protocol, String status_code, String component,String company) {
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
        this.number=number;
        this.country=country;
        this.province=province;
        this.city=city;
        this.updated_at=updated_at;
        this.is_web=is_web;
        this.as_org=as_org;
        this.isp=isp;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getIs_web() {
        return is_web;
    }

    public void setIs_web(String is_web) {
        this.is_web = is_web;
    }

    public String getAs_org() {
        return as_org;
    }

    public void setAs_org(String as_org) {
        this.as_org = as_org;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

}

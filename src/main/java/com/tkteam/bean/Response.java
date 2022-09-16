package com.tkteam.bean;

public class Response{
    private int code;
    private String header;
    private String text;

    private String error;
    public Response(int code,String header, String text, String error) {
        this.code=code;
        this.header=header;
        this.text=text;
        this.error=error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
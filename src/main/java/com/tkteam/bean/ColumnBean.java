package com.tkteam.bean;

public class ColumnBean {

    private String content;

    public ColumnBean() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    private String explain;
    public ColumnBean(String content, String explain) {
        this.content=content;
        this.explain=explain;
    }
}

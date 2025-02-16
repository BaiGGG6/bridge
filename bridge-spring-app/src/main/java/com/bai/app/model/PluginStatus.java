package com.bai.app.model;


public enum PluginStatus {

    RUNNING(0, "运行"),
    SLEEPING(1, "休眠");

    private Integer val;
    private String des;

    PluginStatus(Integer val, String des) {
        this.val = val;
        this.des = des;
    }
}

package com.bai.bridge.model.enums;

public enum LoadMode {
    AUTO("auto", 0),
    MANUAL("manual", 1);

    private String sign;

    private Integer type;


    LoadMode(String sign, Integer type) {
        this.sign = sign;
        this.type = type;
    }

    public static LoadMode getBySign(String sign){
        for (LoadMode value : LoadMode.values()) {
            if(value.sign.equals(sign)){
                return value;
            }
        }
        return AUTO;
    }
}

package com.bai.spring.model.enums;

public enum BootUrlCoverEnum {
    COVER_OPEN("cover", true, "开启url覆盖"),
    COVER_BAN("unCover", false, "关闭url覆盖");

    private String sign;
    private Boolean val;
    private String des;

    BootUrlCoverEnum(String sign, Boolean val, String des) {
        this.sign = sign;
        this.val = val;
        this.des = des;
    }

    public static Boolean getValBySign(String sign){
        for (BootUrlCoverEnum value : BootUrlCoverEnum.values()) {
            if(value.sign.equals(sign)){
                return value.val;
            }
        }
        return true;
    }
}

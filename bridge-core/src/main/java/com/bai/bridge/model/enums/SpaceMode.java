package com.bai.bridge.model.enums;

/**
 * 在隔离模式下，每个插件都独立一个classLoad, 共享模式则是使用自带的classload
 */
public enum SpaceMode {
    ISOLATION("isolation", "隔离"),
    SHARE("share", "共享");

    private final String sign;
    private final String des;

    SpaceMode(String sign, String des) {
        this.sign = sign;
        this.des = des;
    }

    public static SpaceMode getBySign(String sign) {
        for (SpaceMode value : SpaceMode.values()) {
            if(value.sign.equals(sign)){
                return value;
            }
        }
        return SpaceMode.ISOLATION;
    }
}

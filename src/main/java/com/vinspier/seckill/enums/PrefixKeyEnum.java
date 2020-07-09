package com.vinspier.seckill.enums;

public enum  PrefixKeyEnum {
    SEC_KILLED_GOODS("SEC_KILLED_GOODS_");

    private String prefix;

    PrefixKeyEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

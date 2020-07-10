package com.vinspier.seckill.enums;

public enum PrefixKey {
    SEC_KILLED_GOODS("SEC_KILLED_GOODS_"),
    SEC_KILLED_INVENTORY("SEC_KILLED_INVENTORY_"),
    SEC_KILLED_IDS("SEC_KILLED_IDS")
    ;

    private String prefix;

    PrefixKey(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
package com.vinspier.seckill.enums;

public enum PrefixKey {
    /** 秒杀商品的ID前缀 */
    SEC_KILLED_GOODS("SEC_KILLED_GOODS_"),
    /** 秒杀商品的库存前缀 */
    SEC_KILLED_INVENTORY("SEC_KILLED_INVENTORY_"),
    /** 秒杀商品的ID集合前缀 */
    SEC_KILLED_IDS("SEC_KILLED_IDS"),
    /** 秒杀商品的抢到机会的用户前缀 */
    SEC_KILLED_BOUGHT_USERS("SEC_KILLED_BOUGHT_USERS"),
    /** 抢到机会 并且成功发送消息到MQ队列中的数据 */
    SEC_KILLED_PRE_GRABS("SEC_KILLED_PRE_GRABS")
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

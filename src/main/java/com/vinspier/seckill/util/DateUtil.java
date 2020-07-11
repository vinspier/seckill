package com.vinspier.seckill.util;


import java.util.Date;

public class DateUtil {

    private DateUtil(){

    }

    /**
     * 判断目标时间 是否在指定范围内
     * */
    public static boolean between(Date start,Date end,Date target){
        if (target.after(end) || target.before(start)){
            return false;
        }
        return true;
    }

    /**
     * 判断目标时间 是否早于指定时间
     * */
    public static boolean before(Date start,Date target){
        return target.before(start);
    }

    /**
     * 判断目标时间 是否在指定范围内
     * */
    public static boolean after(Date end,Date target){
        return target.after(end);
    }

    /** 判断是否过期 超过了允许的时间 */
    public static boolean expired(Date create,long wait){
        Date current = new Date();
        return current.getTime() - create.getTime() > wait;
    }

}

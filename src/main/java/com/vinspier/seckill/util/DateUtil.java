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

}

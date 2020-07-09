package com.vinspier.seckill.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextConfig implements ApplicationContextAware {
    //定义静态的ApplicationContext成员对象
    private static ApplicationContext applicationContext;

    // 重写setApplicationContext方法，把参数ApplicationContext复制给静态的ApplicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.applicationContext == null){
            ApplicationContextConfig.applicationContext = applicationContext;
        }
    }

    // 定义get方法获取上下文对象
    public static <T> T getBeanByClass(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }


    public static Object getBeanByName(String name){
        return applicationContext.getBean(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

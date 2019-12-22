package com.leiyza.utils;

import com.leiyza.exception.BusiException;
import com.leiyza.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtils {
    private static final ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
    private static final SpringUtils instance=new SpringUtils();
    private SpringUtils(){}
    public static SpringUtils getInstance(){
        return instance;
    }
    public static Object getBean(String beanName){
        return context.getBean(beanName);
    }
    public static boolean isNull(User user){
        if(user==null||user.getUserNo().isEmpty()){
            return true;
        }else {
            return false;
        }
    }
    public static BusiException createException(String errorMsg){
        return new BusiException(errorMsg);
    }
}

package com.leiyza;

import com.leiyza.dao.UserDao;
import com.leiyza.model.User;
import com.leiyza.server.Server;
import com.leiyza.utils.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        Server server=(Server) SpringUtils.getBean("server");
        server.startServer();
    }
}

package com.leiyza.server;
import com.leiyza.cache.ThreadPool;
import com.leiyza.communicate.Message;
import com.leiyza.exception.BusiException;
import com.leiyza.utils.SpringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final Logger logger=Logger.getLogger(Server.class);
    private static final ClientOnlineList onlineList=ClientOnlineList.getInstance();
    private static final ThreadPool threadPool=ThreadPool.getInstance();
    ServerSocket ss=null;
    public static final int TCP_PORT=1228;
    static int clientNum=0;
    public void startServer() throws IOException, ClassNotFoundException {
        ss = new ServerSocket(TCP_PORT);
        while (true) {
            logger.info("服务器已启动正在等待连接...");
            Socket s = ss.accept();
            String clientAddress=String.valueOf(s.getInetAddress()).substring(1)+":"+String.valueOf(s.getPort());
            logger.info("客户端:"+clientAddress+"接入服务器");
            ClientThread thread=new ClientThread(s);
            thread.setName(clientAddress);
            ThreadPool.getInstance().put(thread);
            thread.start();
        }

    }



}

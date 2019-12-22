package com.leiyza.server;

import com.leiyza.model.User;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static Logger logger=Logger.getLogger(Client.class);
    Socket socket;
    User user;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public boolean status=true;
    Client(Socket socket){
        this.socket=socket;
    }
    public ObjectInputStream getInputStream(){
        if(ois==null){
            try {
                ois=new ObjectInputStream(socket.getInputStream());
                return ois;
            } catch (IOException e) {
                logger.error("获取输入流失败："+e.getMessage());
                return null;
            }
        }else {
            return ois;
        }
    }
    public ObjectOutputStream getOutputStream(){

            if(oos==null){
                try {
                    oos=new ObjectOutputStream(socket.getOutputStream());
                    return oos;
                } catch (IOException e) {
                    logger.error("获取输出流失败："+e.getMessage());
                    return null;
                }
            }else {
                return oos;
            }
    }
    void closeSocket(){
        try {
            logger.info("关闭输入流");
            ois.close();
        }catch (Exception e){
            logger.error("输入流关闭失败:"+e.getMessage());
        }
        try {
            logger.info("关闭输出流");
            oos.close();
        }catch (Exception e){
            logger.error("输出流关闭失败:"+e.getMessage());
        }
        try {
            logger.info("关闭socket");
            socket.close();
        }catch (Exception e){
            logger.error("socket关闭失败:"+e.getMessage());
        }
    }
    public String toString(){
        return "UserName:"+user.getUserName()+" UserAcct:"+user.getUserNo();
    }
}

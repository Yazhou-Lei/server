package com.leiyza.server;

import com.leiyza.cache.ThreadPool;
import com.leiyza.communicate.Message;
import com.leiyza.dao.UserStatusDao;
import com.leiyza.model.UserStatus;
import com.leiyza.server.Client;
import com.leiyza.server.Commands;
import com.leiyza.server.ServiceProcess;
import com.leiyza.utils.SpringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread{
    private static final Logger logger=Logger.getLogger(ClientThread.class);
    private Client client;
    private ServiceProcess serviceProcess;
    private ClientOnlineList onlineList;
    private boolean stopByOther=false;
    private boolean isStopped=false;
    public ClientThread(Socket socket){
        client=new Client(socket);
        onlineList=ClientOnlineList.getInstance();
        serviceProcess=(ServiceProcess) SpringUtils.getBean("serviceProcess");
    }
    @Override
    public void run() {
        while (client.status){
            Message message=null;
            try {
                message=(Message) client.getInputStream().readObject();
                client.user=message.getMessageHead().getFrom();
                logger.info(client.user.toString()+"  is online");
                serviceProcess.execute(client,message);
                //注册成功后登录需要重连服务器，此处关闭socket和当前线程
                if(Commands.REGISTER.equals(message.getMessageHead().getCommandType())){
                    client.status=false;
                    client.closeSocket();
                }
            } catch (Exception e) {//若处理过程抛错，需要关闭socket退出当前线程
                e.printStackTrace();
                client.status=false;
                client.closeSocket();
                if(!SpringUtils.isNull(client.user)){
                    if(onlineList.isOnline(client.user.getUserNo())){
                        onlineList.remove(client.user.getUserNo());
                    }
                }
            }
        }
        if(stopByOther){
            logger.info("账号在其他地方登录!");
            Message message=new Message();
            Message.MessageHead messageHead=new Message.MessageHead();
            Message.TextMessage textMessage=new Message.TextMessage();
            messageHead.setCommandType(Commands.ERROR);
            messageHead.setFrom(client.user);
            UserStatusDao userStatusDao=(UserStatusDao)SpringUtils.getBean("userStatusDao");
            UserStatus userStatus=userStatusDao.getUserStatusByUserNo(client.user.getUserNo());
            textMessage.setMessageContent("当前账号已在其他地方登录,登录ip为"+userStatus.getAddress());
            message.setMessageHead(messageHead);
            message.setTextMessage(textMessage);
            try {
                client.getOutputStream().writeObject(message);
                client.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                client.closeSocket();
            }
        }
        onlineList.remove(client.user.getUserNo());
        logger.info("释放资源");
        client=null;//释放资源
        isStopped=true;
    }
    public boolean stopMe(){
        client.status=false;
        stopByOther=true;
        while (!isStopped){
        }
        return true;
    }
    public Client getClient(){
        return client;
    }
}
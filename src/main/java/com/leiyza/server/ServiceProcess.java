package com.leiyza.server;

import com.leiyza.cache.ThreadPool;
import com.leiyza.communicate.Message;
import com.leiyza.dao.UserDao;
import com.leiyza.dao.UserStatusDao;
import com.leiyza.exception.BusiException;
import com.leiyza.model.User;
import com.leiyza.model.UserStatus;
import com.leiyza.utils.SpringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ServiceProcess {
    @Resource
    private UserDao userDao;
    @Resource
    private UserStatusDao userStatusDao;
    private Client client;
    public static final Logger logger=Logger.getLogger(ServiceProcess.class);
    public void execute(Client sourceClient,Message message) throws Exception {
        this.client=sourceClient;
        process(message);

    }
    private void process(Message message)throws Exception{
        String commandType=message.getMessageHead().getCommandType();
        logger.info("process command："+commandType+"start...");
        switch (commandType){
            case Commands.LOGIN:
                login(message.getMessageHead().getFrom());
                break;
            case Commands.REGISTER:
                register(message.getMessageHead().getFrom());
                break;
            case Commands.TALKING:
                talking();
                break;
            default:
                break;
        }
        logger.info("process command："+commandType+"end...");
    }

    public void login(User user)throws Exception{
        Message message=new Message();
        Message.MessageHead messageHead=new Message.MessageHead();
        Message.TextMessage textMessage=new Message.TextMessage();
        messageHead.setCommandType(Commands.LOGIN);
        logger.info("login User:"+user.toString());
        User dbUser=userDao.getUserByUserNo(user.getUserNo());
        messageHead.setFrom(user);
        if(!SpringUtils.isNull(dbUser)){
            logger.info("dbUser:"+user.toString());
            if(user.getUserNo().equals(dbUser.getUserNo())&&user.getPassword().equals(dbUser.getPassword())){
                logger.info("match succeeded!");
                UserStatus userStatus=userStatusDao.getUserStatusByUserNo(dbUser.getUserNo());
                if(userStatus!=null&&"online".equals(userStatus.getStatus())){//已经处于登录状态,更新地址和时间,让其他位置下线
                    logger.info(dbUser.toString()+"已经处于登录状态,更新地址和时间,让其他位置下线");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    changeUserStatus(client.user.getUserNo(),"online",String.valueOf(client.socket.getInetAddress()).substring(1)+":"+client.socket.getPort(),df.format(new Date()));
                    ClientThread clientThread=ThreadPool.getInstance().getClientThread(userStatus.getAddress());
                    if(clientThread!=null){
                        logger.info("正在尝试关闭线程:"+clientThread.getName());
                        while (clientThread.stopMe()) {
                        }
                        ThreadPool.getInstance().remove(userStatus.getAddress());
                    }
                    messageHead.setSuccessFlag(true);
                    ClientOnlineList.getInstance().put(client);
                    textMessage.setMessageContent("登录成功");
                    messageHead.setFrom(dbUser);
                }else {//离线状态
                    client.user=dbUser;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    changeUserStatus(client.user.getUserNo(),"online",String.valueOf(client.socket.getInetAddress()).substring(1)+":"+client.socket.getPort(),df.format(new Date()));
                    messageHead.setSuccessFlag(true);
                    ClientOnlineList.getInstance().put(client);
                    textMessage.setMessageContent("登录成功");
                    messageHead.setFrom(dbUser);
                }
            }else {
                logger.info("match failed!");
                messageHead.setSuccessFlag(false);
                textMessage.setMessageContent("用户名或密码不正确！");
            }
        }else {
            logger.info("用户不存在！");
            messageHead.setSuccessFlag(false);
            textMessage.setMessageContent("用户不存在！");
        }
        message.setTextMessage(textMessage);
        message.setMessageHead(messageHead);
        client.getOutputStream().writeObject(message);
        client.getOutputStream().flush();
        if(!messageHead.isSuccessFlag()){
            throw SpringUtils.createException(textMessage.getMessageContent());
        }
    }
    public void register (User user) throws Exception{
        Message message=new Message();
        Message.MessageHead messageHead=new Message.MessageHead();
        Message.TextMessage textMessage=new Message.TextMessage();
        messageHead.setFrom(user);
        messageHead.setCommandType(Commands.REGISTER);
        User dbUser=userDao.getUserByUserNo(user.getUserNo());
        if(!SpringUtils.isNull(dbUser)){
            logger.info("账号已存在----"+dbUser.toString());
            messageHead.setSuccessFlag(false);
            textMessage.setMessageContent("该账户已注册！");
        }else {
            logger.info("register User:"+user.toString());
            try {
                userDao.insert(user);
                textMessage.setMessageContent("注册成功！");
                messageHead.setSuccessFlag(true);
                logger.info("register succeed!");
            }catch (Exception e){
                logger.info("register failed!");
                messageHead.setSuccessFlag(false);
                textMessage.setMessageContent("注册失败，请联系管理员！");
            }
        }
        message.setTextMessage(textMessage);
        message.setMessageHead(messageHead);
        client.getOutputStream().writeObject(message);
        client.getOutputStream().flush();
        if(!messageHead.isSuccessFlag()){
            throw SpringUtils.createException(textMessage.getMessageContent());
        }
    }
    public void changeUserStatus(String userNo,String status,String address,String loginTime){
        UserStatus userStatus=new UserStatus();
        userStatus.setUserNo(userNo);
        userStatus.setAddress(address);
        userStatus.setStatus(status);
        userStatus.setLoginTime(loginTime);
        userStatusDao.updateUserLoginStatus(userStatus);
    }
    public boolean talking(){
        return true;
    }
}

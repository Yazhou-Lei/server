package com.leiyza.server;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientOnlineList {
    private static Map<String,Client>map=new ConcurrentHashMap<>();
    private static final ClientOnlineList clientList=new ClientOnlineList();
    private static final Logger logger=Logger.getLogger(ClientOnlineList.class);
    private ClientOnlineList(){
    }
    public static ClientOnlineList getInstance(){
        return clientList;
    }
    public void put(Client client){
        map.put(client.user.getUserNo(),client);
        logger.info("put "+client.user.toString());
        outputClients();
    }
    public Client get(String userNo){
        return map.get(userNo);
    }
    public void remove(String userNo){
        if(isOnline(userNo)){
            logger.info(map.get(userNo).toString()+"下线了");
            map.remove(userNo);
        }
        outputClients();
    }
    public boolean isOnline(String userNo){
        return map.containsKey(userNo);
    }
    private void outputClients(){
        logger.info("当前在线人数:"+map.size());
        logger.info("--------------------------------------------------------------");
        for(Client client:map.values()){
            logger.info(client.toString());
        }
        logger.info("--------------------------------------------------------------");
    }
}

package com.leiyza.cache;

import com.leiyza.server.ClientThread;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadPool {
    private static final Logger logger= Logger.getLogger(ThreadPool.class);
    private static final ThreadPool threadPool=new ThreadPool();
    private int threadCount=0;
    private int MaxCapacity=100;//最大登录数;
    private ConcurrentHashMap<String, ClientThread> pool=new ConcurrentHashMap<>();
    private ThreadPool(){}
    public static ThreadPool getInstance(){
        return threadPool;
    }
    public synchronized boolean put(ClientThread thread){
        if(threadCount<MaxCapacity){
            pool.put(thread.getName(),thread);
            threadCount++;
            return true;
        }else {
            return false;
        }

    }
    public synchronized void increaseThreadCount (){
        threadCount++;
    }
    public synchronized void decreaseThreadCount(){
        threadCount--;
    }
    public synchronized void remove(String threadName){
        if(pool.containsKey(threadName)){
            logger.info("线程池中存在线程"+threadName);
            if(pool.get(threadName).isAlive()){
                logger.info("线程"+threadName+"仍处于活动状态");
                pool.remove(threadName);
                logger.info("线程"+threadName+"已移除");
            }else {
                pool.remove(threadName);
                logger.info("线程"+threadName+"已移除");
            }
        }
    }
    public ClientThread getClientThread(String threadName){
        return pool.get(threadName);
    }
}

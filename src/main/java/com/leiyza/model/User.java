package com.leiyza.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String userNo;
    private String password;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserNo() {
        return userNo;
    }
    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String toString(){
        String acct="账号:";
        String name="用户名:";
        String pwd="密码:";
        String res="";
        if(userNo!=null){
            res+=acct+userNo;
        }if(userName!=null){
            res+=name+userName;
        }if(password!=null){
            res+=pwd+password;
        }
        return res;
    }
}

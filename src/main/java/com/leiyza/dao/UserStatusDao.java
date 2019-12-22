package com.leiyza.dao;

import com.leiyza.model.UserStatus;
import com.leiyza.utils.SpringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStatusDao {
    @Resource
    private SqlSession sqlSession;
    public void updateUserLoginStatus(UserStatus status){
        Map<String,Object>param=new HashMap<>();
        param.put("userNo",status.getUserNo());
        param.put("status",status.getStatus());
        param.put("address",status.getAddress());
        param.put("loginTime",status.getLoginTime());
        UserStatus userStatus=getUserStatusByUserNo(status.getUserNo());
        if(userStatus==null){
            sqlSession.insert("insertUserStatus",param);
        }else {
            sqlSession.update("updateUserStatus",param);
        }
    }
    public UserStatus getUserStatusByUserNo(String userNo){
        return sqlSession.selectOne("getUserStatusByUserNo",userNo);
    }
}

package com.leiyza.dao;

import com.leiyza.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserDao {
    @Resource
    private SqlSession sqlSession;
    public void insert(User user){
        Map<String,Object>param=new HashMap<>();
        param.put("userNo",user.getUserNo());
        param.put("userName",user.getUserName());
        param.put("passWord",user.getPassword());
        sqlSession.insert("insert",param);
    }
    public User getUserByUserNo(String userNo){
        return sqlSession.selectOne("getUserByUserNo",userNo);
    }
}

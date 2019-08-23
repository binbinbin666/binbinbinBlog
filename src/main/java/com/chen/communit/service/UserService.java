package com.chen.communit.service;

import com.chen.communit.mapper.UserMapper;
import com.chen.communit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public void createOrUpdate(User user) {
        User DbUser = userMapper.findByAccountId(user.getAccountId());

        if (DbUser == null) {
            //插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //更新
            DbUser.setGmtModified(System.currentTimeMillis());
            DbUser.setAvatarUrl(user.getAvatarUrl());
            DbUser.setName(user.getName());
            DbUser.setToken(user.getToken());
            DbUser.setBio(user.getBio());
            userMapper.update(DbUser);
        }
    }
}

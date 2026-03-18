package com.example.demo.controller;

import com.example.demo.common.UserContext;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Resource
    private UserMapper userMapper;


    @GetMapping("/test/user/info")
    public User getInfo() {
        // 直接从咱们的 ThreadLocal “口袋”里掏数据
        return UserContext.getUser();
    }

    @GetMapping("/test/user/list")
    public List<User> getUsers() {
        System.out.println("🚀 成功触发了接口！正在执行数据库查询...");
        return userMapper.selectList(null);
    }
}
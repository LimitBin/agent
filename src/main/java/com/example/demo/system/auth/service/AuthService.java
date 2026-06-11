package com.example.demo.system.auth.service;


import com.example.demo.common.api.Result;
import com.example.demo.common.utils.JwtUtil;
import com.example.demo.system.auth.dto.LoginDTO;
import com.example.demo.system.user.entity.User;
import com.example.demo.system.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder PasswordEncoder = new BCryptPasswordEncoder();

    public Result<String> register(LoginDTO  loginDTO) {
        //检查账号是否存在
        User existUser = userMapper.selectUserForLogin(loginDTO.getAccount());
        if (existUser != null) {
            return Result.error("账号已存在");
        }

        //创建新用户
        User user = new User();
        user.setAccount(loginDTO.getAccount());
        user.setPassword(PasswordEncoder.encode(loginDTO.getPassword()));
        user.setTenantId("000000");
        user.setName(loginDTO.getAccount());
        userMapper.insert(user);

        return Result.success("注册成功",null);
    }

    public Result<String> login(LoginDTO  loginDTO) {
        User user = userMapper.selectUserForLogin(loginDTO.getAccount());
        if (user == null) {
            return Result.error("账号不存在");
        }

        //校验密码
        if (!PasswordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        Map<String,Object> claims = new HashMap<>();
        claims.put("user_id",user.getId());
        claims.put("tenant_id",user.getTenantId());

        String token = jwtUtil.createToken(claims,loginDTO.getAccount());
        return Result.success("登陆成功",token);
    }
}

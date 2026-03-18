package com.example.demo.controller;


import com.example.demo.common.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam String account) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", 1024);
        claims.put("tenant_id", "000000");

        return JwtUtil.createToken(claims, account);
    }
}

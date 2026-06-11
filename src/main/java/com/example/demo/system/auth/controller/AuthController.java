package com.example.demo.system.auth.controller;

import com.example.demo.common.api.Result;
import com.example.demo.system.auth.dto.LoginDTO;
import com.example.demo.system.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/auth")
public class AuthController {



    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result register(@RequestBody LoginDTO loginDTO){
        return  authService.register(loginDTO);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }
}

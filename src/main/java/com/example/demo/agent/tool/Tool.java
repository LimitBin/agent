package com.example.demo.agent.tool;


import java.util.Map;

public interface Tool {

    String getName();
    String getDescription();
    String getParametersSchema();

    //执行工具
    String execute(Map<String,Object> params);
}

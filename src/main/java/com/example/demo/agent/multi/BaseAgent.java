package com.example.demo.agent.multi;

public abstract class BaseAgent {

    //名称
    public abstract String getName();
    //描述
    public abstract String getDescription();
    //系统提示词
    public abstract String getSystemPrompt();
    //核心方法
    public abstract String execute(String task);
}

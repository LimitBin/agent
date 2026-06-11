package com.example.demo.agent.multi.impl;

import com.example.demo.agent.multi.BaseAgent;
import com.example.demo.agent.tool.impl.KnowledgeSearchTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResearchAgent extends BaseAgent {

    @Autowired
    private KnowledgeSearchTool knowledgeSearchTool;

    public String getName(){
        return "research";
    }

    public String getDescription(){
        return "负责从知识库中搜索相关信息";
    }

    public String getSystemPrompt(){
        return "你是一个研究员，专门负责从知识库中搜索相关信息。给定一个问题，你需要搜索知识库并返回找到的相关内容。只返回搜索到的原始信息，不要做总结。";
    }

    public String execute(String task){
        Map<String,Object> params = Map.of("query",task);
        return knowledgeSearchTool.execute(params);
    }
}

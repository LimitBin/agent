package com.example.demo.agent.multi.impl;

import com.example.demo.agent.multi.BaseAgent;
import com.example.demo.agent.multi.protocol.AgentBlackboard;
import com.example.demo.agent.multi.protocol.AgentMessage;
import com.example.demo.agent.tool.impl.KnowledgeSearchTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RetrieverAgent extends BaseAgent {

    @Autowired
    private KnowledgeSearchTool knowledgeSearchTool;

    @Override
    public String getName(){
        return "retriever";
    }

    @Override
    public String getDescription(){
        return "检索专家，负责从知识库中搜索相关信息，支持查询改写和多步检索";
    }

    public String getSystemPrompt(){
        return "你是一个检索专家。你的任务是从知识库中搜索与问题相关的信息。\n"
                + "请执行以下步骤：\n"
                + "1. 分析用户问题，提取核心关键词\n"
                + "2. 用关键词搜索知识库\n"
                + "3. 评估搜索结果的相关性\n"
                + "4. 如果结果不相关，改写查询词重新搜索\n"
                + "5. 返回最终的搜索结果";
    }
    @Override
    public String execute(AgentBlackboard blackboard){
        String question = blackboard.getQuestion();

        //让LLM改写查询，提取关键词
        String rewritePrompt = "请从以下问题中提取3个搜索关键词，每行一个：\n" + question;
        String keywords = callLLM(getSystemPrompt(), rewritePrompt);

        //用关键词搜索知识库
        StringBuilder allResults = new StringBuilder();
        for(String keyword : keywords.split("\n")){
            keyword = keyword.trim();
            if(keyword.isEmpty()) continue;

            Map<String,Object> params = Map.of("query",keyword);
            String result = knowledgeSearchTool.execute(params);
            allResults.append("关键词[").append(keyword).append("]的搜索结果：\n");
            allResults.append(result).append("\n\n");
        }

        //让LLM评估结果相关性
        String evalPrompt = "问题：" + question + "\n搜索结果：\n" + allResults +
                            "\n请判断这些搜索结果是否与问题相关。如果相关，提炼出有用信息；如果不相关，说明原因。";
        String evaluation = callLLM(getSystemPrompt(), evalPrompt);

        //写入黑板
        blackboard.writeResult("retriever", evaluation);
        blackboard.addMessage(new AgentMessage("retriever", "analyst", evaluation,
                              AgentMessage.MessageType.RESULT, blackboard.getCurrentRound()));
        return evaluation;
    }
}

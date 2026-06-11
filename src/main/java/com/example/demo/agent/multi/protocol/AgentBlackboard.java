package com.example.demo.agent.multi.protocol;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class AgentBlackboard {

    private String question;                    // 原始问题
    private Map<String, String> agentResults;   // 每个 Agent 的结果
    private List<AgentMessage> messageHistory;  // 所有消息历史
    private List<String> feedbackHistory;       // Critic 的反馈历史
    private int currentRound;                   // 当前协作轮次
    private int maxRounds;                      // 最大轮次

    public AgentBlackboard(String question,int maxRounds) {
        this.question = question;
        this.maxRounds = maxRounds;
        this.agentResults = new LinkedHashMap<>();
        this.feedbackHistory = new ArrayList<>();
        this.messageHistory = new ArrayList<>();
        this.currentRound = 1;
    }

    //Agent写入自己的结果
    public void writeResult(String agentName,String result){
        agentResults.put(agentName,result);
    }


    //读取某个Agent的结果
    public String getResult(String agentName){
        return agentResults.getOrDefault(agentName,"");
    }


    //记录一条消息
    public void addMessage(AgentMessage message){
        messageHistory.add(message);
    }


    //记录Critici反馈
    public void addFeedback(String feedback){
        feedbackHistory.add(feedback);
    }


    //获取最新一条反馈
    public String getLatestFeedback(){
        if(feedbackHistory.isEmpty()){
            return null;
        }
        return feedbackHistory.get(feedbackHistory.size()-1);
    }


    //轮次+1
    public void nextRound(){
        currentRound++;
    }


    public boolean hasMoreRounds() {
        return currentRound <= maxRounds;
    }


    public String getAllResults(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : agentResults.entrySet()){
            sb.append("[").append(entry.getKey()).append(" 的结果]\n");
            sb.append(entry.getValue()).append("\n\n");
        }
        return sb.toString();
    }

}

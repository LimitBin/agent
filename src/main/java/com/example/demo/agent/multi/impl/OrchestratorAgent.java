package com.example.demo.agent.multi.impl;


import com.example.demo.agent.multi.BaseAgent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrchestratorAgent {

    @Autowired
    private List<BaseAgent> agents;

    OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey("sk-ff2057b0bbc2454fb3642a9f3ef59ac6")
            .baseUrl("https://api.deepseek.com")
            .modelName("deepseek-chat")
            .temperature(0.7)
            .build();

    public String orchestrate(String question) {
        //遍历，获取agent列表
        StringBuilder agentDesc = new StringBuilder();
        for (BaseAgent agent : agents) {
            agentDesc.append("- ").append(agent.getName())
                    .append(": ").append(agent.getDescription()).append("\n");
        }
        //决定调用谁
        String prompt1 = "你是指挥官，以下是可用的 Agent：\n" + agentDesc
                + "\n请分析用户问题，决定调用哪些 Agent，返回 JSON 格式：\n"
                + "{\"agents\": [\"agent名称\"]}";

        List<ChatMessage> msg1 = new ArrayList<>();
        msg1.add(new SystemMessage(prompt1));
        msg1.add(new UserMessage(question));
        Response<AiMessage> res1 = model.generate(msg1);
        String json = res1.content().text();

        // 清理 markdown 代码块
        json = json.replaceAll("```json", "").replaceAll("```", "").trim();


        //解析json
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(json);
            JsonNode agentNames = root.get("agents");

            // 遍历调用
            StringBuilder results = new StringBuilder();
            for (JsonNode nameNode : agentNames) {
                String name = nameNode.asText();
                for (BaseAgent agent : agents) {
                    if (agent.getName().equals(name)) {
                        String result = agent.execute(question);
                        results.append(name).append(" 的结果：").append(result).append("\n\n");
                    }
                }
            }

            // 汇总结果
            String prompt2 = "你是总结者，请根据以下各 Agent 的结果，回答用户问题。\n" + results;
            List<ChatMessage> msg2 = new ArrayList<>();
            msg2.add(new SystemMessage(prompt2));
            msg2.add(new UserMessage(question));
            Response<AiMessage> res2 = model.generate(msg2);
            return res2.content().text();

        } catch (Exception e) {
            return "编排失败: " + e.getMessage();
        }

    }
}
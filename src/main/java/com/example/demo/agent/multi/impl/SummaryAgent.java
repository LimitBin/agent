package com.example.demo.agent.multi.impl;

import com.example.demo.agent.multi.BaseAgent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SummaryAgent extends BaseAgent {

    public String getName(){
        return "summary";
    }

    public String getDescription(){
        return "负责对文本进行总结提炼";
    }

    public String getSystemPrompt(){
        return "你是一个总结专家，专门负责对文本进行总结提炼。 给定一段文本，你需要提取关键信息，生成简洁的总结。";

    }

    public String execute(String task) {
        //构建模型
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-ff2057b0bbc2454fb3642a9f3ef59ac6")
                .baseUrl("https://api.deepseek.com")
                .modelName("deepseek-chat")
                .temperature(0.7)
                .build();
        //构建消息
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new SystemMessage(getSystemPrompt()));
        messages.add(new UserMessage(task));

        //调用LLM
        Response<AiMessage> response = model.generate(messages);
        String answer = response.content().text();
        return answer;
    }
}

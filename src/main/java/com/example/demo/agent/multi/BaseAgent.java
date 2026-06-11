package com.example.demo.agent.multi;

import com.example.demo.agent.multi.protocol.AgentBlackboard;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;

import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAgent {

    @Value("${agent.deepseek.api-key}")
    protected String apiKey;

    @Value("${agent.deepseek.base-url}")
    protected String baseUrl;

    @Value("${agent.deepseek.model-name}")
    protected String modelName;

    public abstract String getName();

    public abstract String getDescription();

    protected abstract String getSystemPrompt();

    public abstract String execute(AgentBlackboard blackboard);

    protected OpenAiChatModel buildModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    protected String callLLM(String systemPrompt, String userMessage) {
        OpenAiChatModel model = buildModel();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(userMessage));
        Response<AiMessage> response = model.generate(messages);
        return response.content().text();
    }
}

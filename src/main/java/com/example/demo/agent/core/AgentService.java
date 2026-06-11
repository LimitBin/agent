package com.example.demo.agent.core;

import com.example.demo.agent.tool.Tool;
import com.example.demo.agent.tool.ToolRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentService {

    @Autowired
    private ToolRegistry toolRegistry;

    @Value("${agent.deepseek.api-key}")
    private String apiKey;

    @Value("${agent.deepseek.base-url}")
    private String baseUrl;

    @Value("${agent.deepseek.model-name}")
    private String modelName;

    // 推理步骤记录
    public static class ReasoningStep {
        public String thought;
        public String action;
        public String actionInput;
        public String observation;
    }

    // Agent 响应（包含答案和推理过程）
    public static class AgentResponse {
        public String answer;
        public List<ReasoningStep> steps;
    }

    // 构建 LLM
    private OpenAiChatModel buildModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.7)
                .build();
    }

    // 普通对话（兼容旧接口）
    public String chat(String question) {
        AgentResponse response = chatWithReasoning(question);
        return response.answer;
    }

    // ReAct 推理对话
    public AgentResponse chatWithReasoning(String question) {
        OpenAiChatModel model = buildModel();
        List<ChatMessage> messages = new ArrayList<>();
        List<ReasoningStep> steps = new ArrayList<>();

        messages.add(new SystemMessage(buildSystemPrompt()));
        messages.add(new UserMessage(question));

        for (int i = 0; i < 5; i++) {
            Response<AiMessage> response = model.generate(messages);
            AiMessage aiMessage = response.content();
            String text = aiMessage.text();

            // 提取 Thought
            String thought = extractThought(text);

            // 检查是否要调用工具
            if (aiMessage.toolExecutionRequests() != null
                    && !aiMessage.toolExecutionRequests().isEmpty()) {

                messages.add(aiMessage);

                for (var toolRequest : aiMessage.toolExecutionRequests()) {
                    // 记录推理步骤
                    ReasoningStep step = new ReasoningStep();
                    step.thought = thought;
                    step.action = toolRequest.name();
                    step.actionInput = toolRequest.arguments();

                    // 执行工具
                    String result = executeTool(toolRequest.name(), toolRequest.arguments());
                    step.observation = result;
                    steps.add(step);

                    messages.add(ToolExecutionResultMessage.from(
                            toolRequest.id(), toolRequest.name(), result));
                }
            } else {
                // 最终答案
                ReasoningStep step = new ReasoningStep();
                step.thought = thought;
                steps.add(step);

                AgentResponse agentResponse = new AgentResponse();
                agentResponse.answer = text;
                agentResponse.steps = steps;
                return agentResponse;
            }
        }

        AgentResponse agentResponse = new AgentResponse();
        agentResponse.answer = "达到最大推理步数";
        agentResponse.steps = steps;
        return agentResponse;
    }

    // 构建 ReAct 系统提示
    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个智能助手，使用 ReAct 推理框架回答问题。\n\n");
        sb.append("可用工具：\n");
        for (Tool tool : toolRegistry.getAllTools()) {
            sb.append("- ").append(tool.getName())
              .append(": ").append(tool.getDescription()).append("\n");
        }
        sb.append("\n");
        sb.append("回答问题时，请遵循以下步骤：\n");
        sb.append("1. Thought: 分析当前情况，决定下一步\n");
        sb.append("2. 如果需要工具，调用相应工具\n");
        sb.append("3. 拿到工具结果后，继续思考\n");
        sb.append("4. 信息足够时，给出最终答案\n\n");
        sb.append("每次回复时，先输出 Thought，再决定是否调用工具或给出答案。");
        return sb.toString();
    }

    // 提取 Thought
    private String extractThought(String text) {
        if (text == null) return null;
        String lower = text.toLowerCase();
        int idx = lower.indexOf("thought:");
        if (idx >= 0) {
            String after = text.substring(idx + 8).trim();
            int newline = after.indexOf("\n");
            if (newline > 0) return after.substring(0, newline).trim();
            return after;
        }
        return null;
    }

    // 执行工具
    private String executeTool(String toolName, String argsJson) {
        Tool tool = toolRegistry.getTool(toolName);
        if (tool == null) return "错误：工具不存在";

        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode argsNode = om.readTree(argsJson);
            Map<String, Object> params = new HashMap<>();
            argsNode.fields().forEachRemaining(entry ->
                    params.put(entry.getKey(), entry.getValue().asText()));
            return tool.execute(params);
        } catch (Exception e) {
            return "工具执行错误：" + e.getMessage();
        }
    }
}

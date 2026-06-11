package com.example.demo.agent.multi.impl;

import com.example.demo.agent.multi.BaseAgent;
import com.example.demo.agent.multi.protocol.AgentBlackboard;
import com.example.demo.agent.multi.protocol.AgentMessage;
import org.springframework.stereotype.Component;


@Component
public class AnalystAgent extends BaseAgent {

    @Override
    public String getName() {
        return "analyst";
    }

    @Override
    public String getDescription() {
        return "分析师，负责对检索结果进行深度推理和分析";
    }

    @Override
    protected String getSystemPrompt() {
        return "你是一个数据分析师。你的任务是对检索到的信息进行深度分析。\n"
                + "请执行以下步骤：\n"
                + "1. 仔细阅读检索结果\n"
                + "2. 提取关键事实和数据\n"
                + "3. 进行逻辑推理和因果分析\n"
                + "4. 得出有依据的结论\n"
                + "5. 指出信息不足或不确定的地方";
    }


    @Override
    public String execute(AgentBlackboard blackboard) {
        String question = blackboard.getQuestion();
        String retrieverResult = blackboard.getResult("retriever");

        //检查是否有Critic
        String feedback =  blackboard.getLatestFeedback();

        String prompt;
        if (feedback != null) {
            // 有反馈：根据反馈重新分析
            prompt = "原始问题：" + question
                    + "\n\n检索结果：\n" + retrieverResult
                    + "\n\n评审反馈：" + feedback
                    + "\n\n请根据反馈重新进行深度分析，修正之前的问题。";
        } else {
            // 无反馈：首次分析
            prompt = "原始问题：" + question
                    + "\n\n检索结果：\n" + retrieverResult
                    + "\n\n请进行深度分析。";
        }

        String analysis = callLLM(getSystemPrompt(), prompt);

        // 写入黑板
        blackboard.writeResult("analyst", analysis);
        blackboard.addMessage(new AgentMessage("analyst", "critic", analysis,
                AgentMessage.MessageType.RESULT, blackboard.getCurrentRound()));

        return analysis;

    }
}

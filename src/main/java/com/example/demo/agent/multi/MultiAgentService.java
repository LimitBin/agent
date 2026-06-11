package com.example.demo.agent.multi;


import com.example.demo.agent.multi.impl.OrchestratorAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultiAgentService {

    @Autowired
    private OrchestratorAgent orchestratorAgent;

    //普通对话
    public String chat(String question) {
        return orchestratorAgent.orchestrate(question);
    }
}

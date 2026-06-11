package com.example.demo.agent.core;

import com.example.demo.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        //校验问题非空
        if(question ==null||question.isBlank()){
            return Result.error("问题不为空");
        }
        String response = agentService.chat(question);
        return Result.success(response);
    }

    @PostMapping("/reasoning")
    public Result<AgentService.AgentResponse> chatWithReasoning(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        //校验非空
        if(question == null||question.isBlank()){
            return Result.error("问题不能为空");
        }
        AgentService.AgentResponse response = agentService.chatWithReasoning(question);
        return Result.success(response);
    }


}

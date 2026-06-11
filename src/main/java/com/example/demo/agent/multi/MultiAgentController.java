package com.example.demo.agent.multi;


import com.example.demo.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/multi-agent")
public class MultiAgentController {

    @Autowired
    private MultiAgentService multiAgentService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        if(question == null ||question.isBlank()){
            return Result.error("问题不能为空");
        }

        String answer = multiAgentService.chat(question);
        return Result.success(answer);
    }
}

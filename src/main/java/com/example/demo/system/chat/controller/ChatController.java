package com.example.demo.system.chat.controller;

import com.example.demo.common.api.Result;
import com.example.demo.system.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private RagService ragService;

    /**
     * 普通 RAG 对话
     */
    @PostMapping
    public Result<String> chat(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return Result.error("问题不能为空");
        }
        try {
            String answer = ragService.chat(question);
            return Result.success("回答成功", answer);
        } catch (Exception e) {
            return Result.error("回答失败: " + e.getMessage());
        }
    }
}

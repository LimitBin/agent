package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TextAnalysisTool implements Tool {

    @Override
    public String getName() {
        return "text_analysis";
    }

    @Override
    public String getDescription() {
        return "文本分析工具，统计字数、字符数、行数等。当用户需要分析文本时使用此工具。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "text": {
                            "type": "string",
                            "description": "要分析的文本内容"
                        },
                        "action": {
                            "type": "string",
                            "description": "分析类型：count(统计)、uppercase(转大写)、lowercase(转小写)、reverse(反转)"
                        }
                    },
                    "required": ["text"]
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String text = (String) params.get("text");
        String action = params.containsKey("action") ? (String) params.get("action") : "count";

        if (text == null) return "错误：缺少文本参数";

        return switch (action) {
            case "count" -> {
                int chars = text.length();
                int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
                int lines = text.split("\n").length;
                yield "字符数: %d, 词数: %d, 行数: %d".formatted(chars, words, lines);
            }
            case "uppercase" -> text.toUpperCase();
            case "lowercase" -> text.toLowerCase();
            case "reverse" -> new StringBuilder(text).reverse().toString();
            default -> "未知操作: " + action;
        };
    }
}

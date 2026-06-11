package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TranslateTool implements Tool {

    @Override
    public String getName() {
        return "translate";
    }

    @Override
    public String getDescription() {
        return "翻译工具，将文本从一种语言翻译为另一种语言。支持中英互译。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "text": {
                            "type": "string",
                            "description": "要翻译的文本"
                        },
                        "target_lang": {
                            "type": "string",
                            "description": "目标语言：en(英文) 或 zh(中文)"
                        }
                    },
                    "required": ["text", "target_lang"]
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String text = (String) params.get("text");
        String targetLang = (String) params.get("target_lang");

        if (text == null || targetLang == null) return "错误：缺少参数";

        // 简单翻译：实际项目中应调用翻译 API
        if ("en".equals(targetLang)) {
            return "翻译为英文: [需要调用翻译API] " + text;
        } else if ("zh".equals(targetLang)) {
            return "翻译为中文: [需要调用翻译API] " + text;
        }
        return "不支持的语言: " + targetLang;
    }
}

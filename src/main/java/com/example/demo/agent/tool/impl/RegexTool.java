package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexTool implements Tool {

    @Override
    public String getName() {
        return "regex";
    }

    @Override
    public String getDescription() {
        return "正则表达式匹配工具，从文本中提取特定模式的内容。当用户需要提取文本中的特定信息时使用此工具。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "text": {
                            "type": "string",
                            "description": "要搜索的文本"
                        },
                        "pattern": {
                            "type": "string",
                            "description": "正则表达式模式"
                        }
                    },
                    "required": ["text", "pattern"]
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String text = (String) params.get("text");
        String pattern = (String) params.get("pattern");

        if (text == null || pattern == null) return "错误：缺少参数";

        try {
            Matcher matcher = Pattern.compile(pattern).matcher(text);
            List<String> matches = new ArrayList<>();
            while (matcher.find()) {
                matches.add(matcher.group());
            }
            if (matches.isEmpty()) return "未找到匹配项";
            return "找到 %d 个匹配: %s".formatted(matches.size(), String.join(", ", matches));
        } catch (Exception e) {
            return "正则表达式错误: " + e.getMessage();
        }
    }
}

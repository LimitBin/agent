package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class DateTimeTool implements Tool {

    @Override
    public String getName() {
        return "datetime";
    }

    @Override
    public String getDescription() {
        return "获取当前日期和时间。当用户问今天日期、现在时间时使用此工具。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "format": {
                            "type": "string",
                            "description": "日期格式，默认 yyyy-MM-dd HH:mm:ss"
                        }
                    },
                    "required": []
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String format = params.containsKey("format")
            ? (String) params.get("format")
            : "yyyy-MM-dd HH:mm:ss";
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }
}

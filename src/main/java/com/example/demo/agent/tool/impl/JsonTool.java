package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonTool implements Tool {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String getName() {
        return "json";
    }

    @Override
    public String getDescription() {
        return "JSON 格式化和验证工具。当用户需要格式化或验证 JSON 时使用此工具。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "json_string": {
                            "type": "string",
                            "description": "JSON 字符串"
                        },
                        "action": {
                            "type": "string",
                            "description": "操作类型：format(格式化) 或 validate(验证)"
                        }
                    },
                    "required": ["json_string"]
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String jsonStr = (String) params.get("json_string");
        String action = params.containsKey("action") ? (String) params.get("action") : "format";

        if (jsonStr == null) return "错误：缺少 JSON 参数";

        try {
            Object obj = mapper.readValue(jsonStr, Object.class);
            if ("validate".equals(action)) {
                return "JSON 格式有效";
            }
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "JSON 错误: " + e.getMessage();
        }
    }
}

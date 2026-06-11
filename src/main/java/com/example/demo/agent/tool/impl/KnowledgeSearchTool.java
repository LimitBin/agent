package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import com.example.demo.system.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KnowledgeSearchTool implements Tool {

    @Autowired
    private RagService ragService;

    @Override
    public String getName() {
        return "knowledge_search";
    }

    @Override
    public String getDescription() {
        return "搜索知识库，查找与问题相关的文档内容。当用户问到需要参考资料的问题时使用此工具。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "query": {
                            "type": "string",
                            "description": "搜索关键词或问题"
                        }
                    },
                    "required": ["query"]
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String query = (String) params.get("query");
        if (query == null || query.isBlank()) {
            return "错误：缺少搜索关键词";
        }
        try {
            return ragService.chat(query);
        } catch (Exception e) {
            return "搜索失败: " + e.getMessage();
        }
    }
}

package com.example.demo.agent.tool;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ToolRegistry {

    private Map<String,Tool> tools = new HashMap<String,Tool>();

    @Autowired
    public ToolRegistry(List<Tool> toolList){
        for(Tool tool:toolList){
            tools.put(tool.getName(), tool);
        }
    }
    //获取工具
    public Tool getTool(String name){
        return tools.get(name);
    }
    //获取所有工具
    public List<Tool> getAllTools(){
        return List.copyOf(tools.values());
    }

    //生成Function Calling 格式的工具描述
    public String generateToolJson(){
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for(Tool tool:tools.values()){
            if(!first) sb.append(",");
            sb.append("""
                    {
                        "type":"function",
                        "function":{
                        "name":"%s",
                        "description":"%s",
                        "parameters": %s
                        }
                    }
                    """.formatted(tool.getName(),
                                  tool.getDescription(),
                                  tool.getParametersSchema()));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}

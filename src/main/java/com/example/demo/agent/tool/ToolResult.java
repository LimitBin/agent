package com.example.demo.agent.tool;


import lombok.Data;

@Data
public class ToolResult {

    private boolean success;
    private String data;
    private String error;

    public static ToolResult success(String data){
        ToolResult r = new ToolResult();
        r.setSuccess(true);
        r.setData(data);
        return r;
    }

    public static ToolResult error(String error){
        ToolResult r = new ToolResult();
        r.setSuccess(false);
        r.setError(error);
        return r;
    }
}

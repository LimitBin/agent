package com.example.demo.agent.tool.impl;

import com.example.demo.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CalculatorTool implements Tool {

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "数学计算器，可以计算数学表达式。当用户问数学计算问题时使用此工具。";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "expression": {
                            "type": "string",
                            "description": "数学表达式，如 1+1, 2*3+5, (10-3)*2"
                        }
                    },
                    "required": ["expression"]
                }
                """;
    }

    @Override
    public String execute(Map<String, Object> params) {
        String expression = (String) params.get("expression");
        if (expression == null || expression.isBlank()) {
            return "错误：缺少表达式参数";
        }
        try {
            double result = evaluate(expression.trim());
            return String.valueOf(result);
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }

    private double evaluate(String expr) {
        return new Object() {
            int pos = 0;

            double parse() {
                double result = parseTerm();
                while (pos < expr.length()) {
                    char c = expr.charAt(pos);
                    if (c == '+') { pos++; result += parseTerm(); }
                    else if (c == '-') { pos++; result -= parseTerm(); }
                    else break;
                }
                return result;
            }

            double parseTerm() {
                double result = parseFactor();
                while (pos < expr.length()) {
                    char c = expr.charAt(pos);
                    if (c == '*') { pos++; result *= parseFactor(); }
                    else if (c == '/') { pos++; result /= parseFactor(); }
                    else break;
                }
                return result;
            }

            double parseFactor() {
                if (expr.charAt(pos) == '(') {
                    pos++;
                    double result = parse();
                    pos++; // skip ')'
                    return result;
                }
                int start = pos;
                while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) {
                    pos++;
                }
                return Double.parseDouble(expr.substring(start, pos));
            }
        }.parse();
    }
}

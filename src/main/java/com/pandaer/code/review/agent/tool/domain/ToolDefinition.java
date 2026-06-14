package com.pandaer.code.review.agent.tool.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 工具定义
 */
@Data
public class ToolDefinition {

    /**
     * 工具的名称 唯一标识
     */
    private String name;

    /**
     * 工具的描述 AI友好的文本
     */
    private String description;

    /**
     * 工具参数Schema
     */
    private JsonNode paramsSchema;

    private JsonNode resultSchema;
}

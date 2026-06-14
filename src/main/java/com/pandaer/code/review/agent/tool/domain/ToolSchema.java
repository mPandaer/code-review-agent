package com.pandaer.code.review.agent.tool.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 工具参数或者工具结果的结构描述
 */
@Data
public class ToolSchema {

    private String type;

    private JsonNode schema;
}

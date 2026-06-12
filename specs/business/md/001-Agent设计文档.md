# Agent 整体设计

> 本文沉淀第一版 Agent 架构：CLI 负责交互入口，Agent Core 负责编排 Agent Loop，LLM Client 负责模型供应商适配。设计重点是把长期上下文、单次运行状态、工具系统和 LLM 协议边界拆清楚。

## 版本信息

| 属性 | 值 |
|------|-----|
| 文档编号 | 001 |
| 第一版范围 | 不依赖 Agent 框架 |
| 实现语言 | Java |
| 核心机制 | Function Call / Tool Use |

---

## 核心结论

**三层架构成立，但 Agent Core 内部必须继续拆分。**

外部可以分为 `CLI`、`Agent Core`、`LLM API Client` 三层；真正容易崩掉的地方在 `Agent Core` 内部。如果 `Session`、`RunState`、`RunConfig`、`ToolRegistry` 的生命周期混在一起，后续实现 Agent Loop 时会出现上下文污染、配置语义不清、工具调用逻辑散落等问题。

---

## 分层职责

### CLI

- 解析用户命令和交互输入
- 创建、选择或加载 `Session`
- 调用 `Agent.run(session, userInput)`
- 渲染 `AgentRunResult`

### Agent Core

- 维护 Agent Loop
- 追加 user、assistant、tool message
- 根据 LLM 返回决定是否调用工具
- 处理最大轮次和运行结果

### LLM API Client

- 把内部请求转换为供应商请求
- 发送具体 LLM API 请求
- 把供应商响应转换为 `LlmResponse`
- 不关心 Agent Loop

---

## 核心对象边界

### 长期上下文

- `Session` 保存会话级历史信息
- `message list` 是 LLM 可见上下文的主载体
- `ToolResult` 最终转换为 `ToolMessage` 后追加进消息列表
- `maxRounds exceeded` 是运行控制结果，不写入消息列表

### 单次运行状态

- `RunState` 保存当前轮次、是否结束等临时状态
- `currentRound` 只属于一次 `run`
- `RunConfig` 从 `SessionConfig` 快照生成
- 运行中的配置不被后续 Session 修改影响

---

## 推荐结构

```
CLI
  CommandParser
  ConsoleRenderer

Agent Core
  Agent
  Session
  SessionConfig
  RunConfig
  RunState
  AgentRunResult

LLM
  LlmClient
  LlmRequest
  LlmResponse
  AssistantMessage
  ToolCall

Tool
  Tool
  ToolDefinition
  ToolRegistry
  ToolResult
  ToolMessageFactory
```

---

## Agent Loop 流程

1. **追加用户消息。** `agent.run(session, userInput)` 开始时，先把用户输入追加为 `UserMessage`，确保 LLM 第一轮能看到新任务。

2. **冻结运行配置。** 从 `SessionConfig` 复制生成 `RunConfig`，并创建空的 `RunState`。

3. **调用 LLM。** 使用当前 `session.messages`、工具定义和 `RunConfig` 构造 `LlmRequest`。

4. **保存 assistant 消息。** 把 `LlmResponse.assistantMessage` 追加进 `Session.messages`。

5. **处理工具调用。** 如果存在 tool calls，第一版按顺序执行；每个结果转换成 `ToolMessage` 后追加进消息列表。

6. **判断结束。** 没有 tool calls 时返回完成结果；达到 `maxRounds` 时返回 `MAX_ROUNDS_EXCEEDED`，但不追加控制消息。

---

## 接口草图

```java
interface LlmClient {
    LlmResponse chat(LlmRequest request);
}

interface Tool {
    ToolDefinition definition();
    ToolResult execute(JsonNode arguments);
}

interface ToolRegistry {
    List<ToolDefinition> definitions();
    Optional<Tool> find(String name);
}
```

---

## 关键设计决策

### 工具调用

- `ToolRegistry` 只负责注册、列出定义和按名称查找
- `Tool` 自己负责 JSON 参数反序列化和校验
- 参数错误包装为 `ToolResult` 回传给 LLM
- 未知工具由 Agent Core 显式转换为失败的 `ToolResult`

### 工具结果

- `ToolResult` 是工具领域结果，不直接等同于消息
- `ToolMessage` 是 LLM 协议消息
- 第一版 `ToolResult.status` 使用 `SUCCESS`、`INVALID_ARGUMENTS`、`EXECUTION_ERROR`
- `message` 描述失败原因，`data` 保存结构化结果

---

## 运行结果

```java
class AgentRunResult {
    RunStatus status;      // COMPLETED / MAX_ROUNDS_EXCEEDED / FAILED
    String finalMessage;   // 成功时的最终回答，失败时的说明
    int roundsUsed;
}
```

---

## ⚠️ 最容易崩掉的地方：三个边界必须守住

1. **不要把 `Session` 和 `RunState` 混在一起。** 会导致当前轮次、最大轮次错误、临时状态污染长期上下文。

2. **不要把 `ToolResult` 和 `ToolMessage` 混在一起。** 会让工具领域模型和 LLM 协议格式耦合。

3. **不要让 `Agent Core` 解析供应商原始响应。** 否则多 LLM Client 实现时，Agent Core 会变成响应格式适配层。

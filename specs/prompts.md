# 对话历史

## 想法
我想要动手实现一个Code Review Agent，我想要达到的效果如下：
1. 我在命令行中输入 `pr`指令，就进入一个命令行的交互界面，和输入`claude`一样，然后就可以开始输入自然语言进行Code Review
   比如：“请Review当前未提交的代码，并给出建议”，这个时候Agent就会使用工具，读文件，审查代码，给出对应的Review建议
2. 当我输入 `pr -t 请Review CommitID 为323444对应的代码变更，并给出对应的建议` 这个是非交互式的，LLM直接给出对应的建议

我希望利用Java自己来实现这个Agent，不借助任何AI应用开发框架，比如SpringAI等等，而你的作用就是作为我的一个导师，一位苏格拉底式的导师，
协助我完成对应的任务

---
以上是我的简单的想法，我还有那些地方没有说清楚的，如果说清楚了，请生成CLAUDE.md 以及 AGENTS.md

1. LLM的接入是用OpenAI的格式，不过可以利用接口进行一层抽象，方便后续扩展其他格式的API
2. Tool Use,是LLM的Function Call,这是我的理解
3. 交互模式，需要保留对应的上下文，有Session的概念
4. Reveiw不是一个工具，而是一个流程，一个Code Reivew的流程，只不过在这个流程中，Agent可能会调用一些工具，比如读文件内容的工具，去获取更加详细的上下文信息
5. Java的版本，只要是Java8以上即可，构建工具使用Gradle


## 整体的Agent设计
我认为我们这个Agent可以分为3层：
1. CLI层 负责处理用户的输入，解析指令
2. Agent Core 负责Agent Loop,包括工具调用，LLM调用
3. LLM Api Client层，负责发送具体的LLM请求
你认为这样设计足够吗？在具体的实现过程中，会在哪一步崩掉呢？

> 从用户在 CLI 输入一条命令开始，到 LLM 返回一次 function call，再到工具执行完毕，哪些状态必须跨层保存？比如 session、message list、tool registry、当前轮次、停止条件，这些分别应该归谁管？
> 答：我认为session是需要维护的，message List归属于Session,所以也是需要维护的，tool registery应该是在Agent启动时就初始化好了，我认为也可以放在session中，这样后续可以支持Session级别的修改工具列表，当前轮次也可以维护到session中，停止条件也是。

> 再往前推进一步：如果 LLM 这一轮返回的不是普通文本，而是一个 tool call，你觉得谁应该决定“把这个调用结果追加回上下文，然后进入下一轮”？如果这个责任落在 CLI或 LLM Client 里，你觉得会出现什么问题？
> 答：我认为应该放在agent core中，落在CLI中，CLI层的职责不单一了，落在LLM Client中，如果后续扩展多个LLM Client，每个Client都要维护这个逻辑

> 我建议你继续推导一个问题：假设同一个 Session 里，用户连续发了两次指令。第一次 Agent Loop 跑了 5 轮，第二次又开始新一轮处理。那 当前轮次 应该保留在 Session里吗？还是应该属于这一次 agent.run(userInput) 的局部状态？
> 答：我认为应该保留呀，如果第二轮的任务依赖第一轮的上下文，或者第二轮中的某一个小步骤，依赖第二轮的前面的步骤产生的信息，这样LLM获取到的信息就是完整的呀？难道不是吗？

> 你可以先分开想两个问题：
> 1. 如果 currentRound=5 只是一个数字，它对下一次 agent.run(...) 的决策有什么直接价值？
> 2. 如果第二次任务需要第一轮的结果，你真正要保留的是 round，还是那一轮产生的 messages、tool results、summary？
> 再往前一步想：
> 如果同一个 Session 里有两次独立的 run，你希望它们共享的是“历史消息”，还是“执行进度状态”？
> 你先回答这个区分：Session 里应该长期保存的是哪些信息，哪些信息只该活在一次 run 里？
> 答：我理解你的意思了，currentRound其实应该是Agent Loop里的状态，用来控制无限调用工具，我在Session中存储的应该是上一轮中产生的messages,tool Results,summary信息。我更加希望的是共享历史消息，而不是执行进度状态

---
  可以继续用这个问题检查你的设计：

  当 agent.run(session, userInput) 开始时，它至少会用到几类东西？

  - Session：长期上下文，比如 historical messages、必要的 summary、也许还有 session id。
  - AgentConfig：稳定配置，比如 model、maxRounds、system prompt、enabled tools。
  - RunState：本次执行的临时状态，比如 currentRound、是否已完成、这一轮 LLM 返回了什么。
  - ToolRegistry：工具定义与工具执行入口。

  这里我想追问你一个关键点：你刚才说 tool results 可以存在 Session 里。那你觉得它应该作为一个独立字段保存，比如 session.toolResults，还是应该以某种 message
  的形式追加进 message list？

  换句话说，LLM 下一轮要看到工具结果时，它是从哪里看到的？
  是 Agent Core 单独拿 toolResults 拼 prompt，还是 message list 本身就已经包含了 tool result message？

  答：Session中的历史消息，AgentConfig我认为在Session创建的时候，就存入一份，这样我们可以基于这份配置在这个Session中修改，RunState不放在Session，表示这一次执行的状态，比如是否结束，存储这个轮次过程中的LLM的历史输入，比如tool result,llm output,我认为 tool results可以看做是一个特殊的Message，就放在message list中吧。
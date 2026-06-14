# 提示词模板 CRUD 与在线编辑

## 所属阶段
**P1 核心能力 → T4 提示词管理与版本控制**

## 使用技术
- Spring Web MVC
- MyBatis-Plus
- 自定义模板引擎（变量占位符替换）

## 涉及数据库表
- `t_prompt_template`

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/prompts` | 创建提示词模板 |
| GET | `/api/v1/prompts` | 模板列表（分页） |
| GET | `/api/v1/prompts/{id}` | 模板详情 |
| PUT | `/api/v1/prompts/{id}` | 编辑模板 |
| DELETE | `/api/v1/prompts/{id}` | 删除模板 |
| POST | `/api/v1/prompts/{id}/preview` | 变量填充预览渲染 |

## 实现方案

### 1. 模板语法

使用 `{{variable_name}}` 占位符：

```
你是一个专业的{{role}}，名叫{{agent_name}}。

## 你的能力
{{capabilities}}

## 当前用户
- 姓名：{{user_name}}
- 部门：{{department}}

## 知识库上下文
{{knowledge_context}}

## 对话历史
{{conversation_history}}

## 用户问题
{{user_query}}

请以{{tone}}的语气回答，必要时使用以下工具：
{{available_tools}}
```

### 2. 模板存储模型

```java
@Data
@TableName("t_prompt_template")
public class PromptTemplate {
    private Long id;
    private String promptId;        // 唯一标识
    private String tenantId;
    private String name;            // 模板名称
    private String description;
    private String templateText;    // 含 {{variable}} 占位符
    private List<VariableDef> variables;  // JSON → variables JSON 字段
    private Integer version;        // 当前发布版本号
    private String status;          // DRAFT / PUBLISHED / ARCHIVED
    private String abTestConfig;    // A/B 测试配置 JSON
}

@Data
public class VariableDef {
    private String name;            // 变量名
    private String type;            // string / number / boolean / array
    private String description;     // 变量说明
    private String defaultValue;    // 默认值
    private boolean required;       // 是否必填
}
```

### 3. 预览渲染

```java
@PostMapping("/{promptId}/preview")
public Result<String> preview(@PathVariable String promptId, @RequestBody Map<String, Object> variables) {
    PromptTemplate template = promptService.getByPromptId(promptId);
    String rendered = promptRenderService.render(template.getTemplateText(), variables);
    return Result.success(rendered);
}

@Service
public class PromptRenderService {

    public String render(String templateText, Map<String, Object> variables) {
        String result = templateText;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}",
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }
        // 检查未填充的必填变量
        validateUnfilledVariables(result);
        return result;
    }

    private void validateUnfilledVariables(String rendered) {
        Pattern p = Pattern.compile("\\{\\{(\\w+)\\}\\}");
        Matcher m = p.matcher(rendered);
        if (m.find()) {
            throw new BusinessException(400, "存在未填充的变量: " + m.group(1));
        }
    }
}
```

# Agent Platform API


**简介**:Agent Platform API


**HOST**:http://localhost:8080


**联系人**:Agent Platform Team


**Version**:1.0.0


**接口路径**:/v3/api-docs


[TOC]






# 安全审计
暂无接口文档


# 安全围栏


## 敏感词规则详情


**接口地址**:`/api/v1/security/sensitive-words/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultSensitiveWordResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||SensitiveWordResponse|SensitiveWordResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;word|敏感词或正则表达式|string||
|&emsp;&emsp;matchType|匹配方式: EXACT/REGEX/SEMANTIC|string||
|&emsp;&emsp;matchTypeLabel|匹配方式标签|string||
|&emsp;&emsp;category|分类: INJECTION/JAILBREAK/PII/CUSTOM|string||
|&emsp;&emsp;categoryLabel|分类标签|string||
|&emsp;&emsp;severity|严重程度: LOW/MEDIUM/HIGH/BLOCK|string||
|&emsp;&emsp;severityLabel|严重程度标签|string||
|&emsp;&emsp;action|动作: LOG/WARN/BLOCK|string||
|&emsp;&emsp;actionLabel|动作标签|string||
|&emsp;&emsp;status|状态: ACTIVE/DISABLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"word": "",
		"matchType": "",
		"matchTypeLabel": "",
		"category": "",
		"categoryLabel": "",
		"severity": "",
		"severityLabel": "",
		"action": "",
		"actionLabel": "",
		"status": "",
		"statusLabel": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新敏感词规则


**接口地址**:`/api/v1/security/sensitive-words/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "word": "暴力",
  "matchType": "EXACT",
  "category": "CUSTOM",
  "severity": "MEDIUM",
  "action": "BLOCK"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|updateSensitiveWordRequest|更新敏感词规则请求|body|true|UpdateSensitiveWordRequest|UpdateSensitiveWordRequest|
|&emsp;&emsp;word|敏感词或正则表达式||true|string||
|&emsp;&emsp;matchType|匹配方式: EXACT/REGEX/SEMANTIC||true|string||
|&emsp;&emsp;category|分类: INJECTION/JAILBREAK/PII/CUSTOM||true|string||
|&emsp;&emsp;severity|严重程度: LOW/MEDIUM/HIGH/BLOCK||true|string||
|&emsp;&emsp;action|命中动作: LOG/WARN/BLOCK||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultSensitiveWordResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||SensitiveWordResponse|SensitiveWordResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;word|敏感词或正则表达式|string||
|&emsp;&emsp;matchType|匹配方式: EXACT/REGEX/SEMANTIC|string||
|&emsp;&emsp;matchTypeLabel|匹配方式标签|string||
|&emsp;&emsp;category|分类: INJECTION/JAILBREAK/PII/CUSTOM|string||
|&emsp;&emsp;categoryLabel|分类标签|string||
|&emsp;&emsp;severity|严重程度: LOW/MEDIUM/HIGH/BLOCK|string||
|&emsp;&emsp;severityLabel|严重程度标签|string||
|&emsp;&emsp;action|动作: LOG/WARN/BLOCK|string||
|&emsp;&emsp;actionLabel|动作标签|string||
|&emsp;&emsp;status|状态: ACTIVE/DISABLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"word": "",
		"matchType": "",
		"matchTypeLabel": "",
		"category": "",
		"categoryLabel": "",
		"severity": "",
		"severityLabel": "",
		"action": "",
		"actionLabel": "",
		"status": "",
		"statusLabel": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除敏感词规则


**接口地址**:`/api/v1/security/sensitive-words/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启用-禁用敏感词规则


**接口地址**:`/api/v1/security/sensitive-words/{id}/toggle-status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 敏感词规则列表


**接口地址**:`/api/v1/security/sensitive-words`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListSensitiveWordResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|SensitiveWordResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;word|敏感词或正则表达式|string||
|&emsp;&emsp;matchType|匹配方式: EXACT/REGEX/SEMANTIC|string||
|&emsp;&emsp;matchTypeLabel|匹配方式标签|string||
|&emsp;&emsp;category|分类: INJECTION/JAILBREAK/PII/CUSTOM|string||
|&emsp;&emsp;categoryLabel|分类标签|string||
|&emsp;&emsp;severity|严重程度: LOW/MEDIUM/HIGH/BLOCK|string||
|&emsp;&emsp;severityLabel|严重程度标签|string||
|&emsp;&emsp;action|动作: LOG/WARN/BLOCK|string||
|&emsp;&emsp;actionLabel|动作标签|string||
|&emsp;&emsp;status|状态: ACTIVE/DISABLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"word": "",
			"matchType": "",
			"matchTypeLabel": "",
			"category": "",
			"categoryLabel": "",
			"severity": "",
			"severityLabel": "",
			"action": "",
			"actionLabel": "",
			"status": "",
			"statusLabel": "",
			"createdAt": "",
			"updatedAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建敏感词规则


**接口地址**:`/api/v1/security/sensitive-words`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "word": "暴力",
  "matchType": "EXACT",
  "category": "CUSTOM",
  "severity": "MEDIUM",
  "action": "BLOCK"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createSensitiveWordRequest|创建敏感词规则请求|body|true|CreateSensitiveWordRequest|CreateSensitiveWordRequest|
|&emsp;&emsp;word|敏感词或正则表达式||true|string||
|&emsp;&emsp;matchType|匹配方式: EXACT/REGEX/SEMANTIC||true|string||
|&emsp;&emsp;category|分类: INJECTION/JAILBREAK/PII/CUSTOM||true|string||
|&emsp;&emsp;severity|严重程度: LOW/MEDIUM/HIGH/BLOCK||true|string||
|&emsp;&emsp;action|命中动作: LOG/WARN/BLOCK||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultSensitiveWordResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||SensitiveWordResponse|SensitiveWordResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;word|敏感词或正则表达式|string||
|&emsp;&emsp;matchType|匹配方式: EXACT/REGEX/SEMANTIC|string||
|&emsp;&emsp;matchTypeLabel|匹配方式标签|string||
|&emsp;&emsp;category|分类: INJECTION/JAILBREAK/PII/CUSTOM|string||
|&emsp;&emsp;categoryLabel|分类标签|string||
|&emsp;&emsp;severity|严重程度: LOW/MEDIUM/HIGH/BLOCK|string||
|&emsp;&emsp;severityLabel|严重程度标签|string||
|&emsp;&emsp;action|动作: LOG/WARN/BLOCK|string||
|&emsp;&emsp;actionLabel|动作标签|string||
|&emsp;&emsp;status|状态: ACTIVE/DISABLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"word": "",
		"matchType": "",
		"matchTypeLabel": "",
		"category": "",
		"categoryLabel": "",
		"severity": "",
		"severityLabel": "",
		"action": "",
		"actionLabel": "",
		"status": "",
		"statusLabel": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 安全事件列表


**接口地址**:`/api/v1/security/events`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListSecurityEventResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|SecurityEventResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;eventType|事件类型|string||
|&emsp;&emsp;ruleId|触发的规则 ID|integer(int64)||
|&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;messageId|关联消息 ID|string||
|&emsp;&emsp;originalContent|原始内容（截断）|string||
|&emsp;&emsp;processedContent|处理后内容|string||
|&emsp;&emsp;matchedPattern|匹配到的模式|string||
|&emsp;&emsp;actionTaken|采取动作|string||
|&emsp;&emsp;operator|操作人|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"eventType": "",
			"ruleId": 0,
			"conversationId": "",
			"messageId": "",
			"originalContent": "",
			"processedContent": "",
			"matchedPattern": "",
			"actionTaken": "",
			"operator": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 按会话查询安全事件


**接口地址**:`/api/v1/security/events/conversation/{conversationId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|conversationId||path|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListSecurityEventResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|SecurityEventResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;eventType|事件类型|string||
|&emsp;&emsp;ruleId|触发的规则 ID|integer(int64)||
|&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;messageId|关联消息 ID|string||
|&emsp;&emsp;originalContent|原始内容（截断）|string||
|&emsp;&emsp;processedContent|处理后内容|string||
|&emsp;&emsp;matchedPattern|匹配到的模式|string||
|&emsp;&emsp;actionTaken|采取动作|string||
|&emsp;&emsp;operator|操作人|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"eventType": "",
			"ruleId": 0,
			"conversationId": "",
			"messageId": "",
			"originalContent": "",
			"processedContent": "",
			"matchedPattern": "",
			"actionTaken": "",
			"operator": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 对话管理


## 我的会话列表


**接口地址**:`/api/v1/conversations`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseConversationResponse|PageResponseConversationResponse|
|&emsp;&emsp;records||array|ConversationResponse|
|&emsp;&emsp;&emsp;&emsp;conversationId||string||
|&emsp;&emsp;&emsp;&emsp;agentId||string||
|&emsp;&emsp;&emsp;&emsp;userId||string||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"conversationId": "",
				"agentId": "",
				"userId": "",
				"title": "",
				"status": "",
				"messageCount": 0,
				"totalTokens": 0,
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建新会话


**接口地址**:`/api/v1/conversations`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "agentId": "",
  "title": "",
  "metadata": {}
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createConversationRequest|CreateConversationRequest|body|true|CreateConversationRequest|CreateConversationRequest|
|&emsp;&emsp;agentId|||true|string||
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;metadata|||false|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ConversationResponse|ConversationResponse|
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;userId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"conversationId": "",
		"agentId": "",
		"userId": "",
		"title": "",
		"status": "",
		"messageCount": 0,
		"totalTokens": 0,
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发送消息（SSE 流式）


**接口地址**:`/api/v1/conversations/{id}/stream`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`text/event-stream,application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|sendMessageRequest|SendMessageRequest|body|true|SendMessageRequest|SendMessageRequest|
|&emsp;&emsp;content|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SseEmitter|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|timeout||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"timeout": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 加载更早的消息


**接口地址**:`/api/v1/conversations/{id}/messages`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|before||query|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseMessageResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseMessageResponse|PageResponseMessageResponse|
|&emsp;&emsp;records||array|MessageResponse|
|&emsp;&emsp;&emsp;&emsp;messageId||string||
|&emsp;&emsp;&emsp;&emsp;conversationId||string||
|&emsp;&emsp;&emsp;&emsp;role||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;tokenCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;feedback||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"messageId": "",
				"conversationId": "",
				"role": "",
				"content": "",
				"tokenCount": 0,
				"feedback": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发送消息（非流式）


**接口地址**:`/api/v1/conversations/{id}/messages`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|sendMessageRequest|SendMessageRequest|body|true|SendMessageRequest|SendMessageRequest|
|&emsp;&emsp;content|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMessageResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||MessageResponse|MessageResponse|
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;role||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;tokenCount||integer(int32)||
|&emsp;&emsp;feedback||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"messageId": "",
		"conversationId": "",
		"role": "",
		"content": "",
		"tokenCount": 0,
		"feedback": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新标题


**接口地址**:`/api/v1/conversations/{id}/title`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "title": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|updateConversationTitleRequest|UpdateConversationTitleRequest|body|true|UpdateConversationTitleRequest|UpdateConversationTitleRequest|
|&emsp;&emsp;title|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 状态流转


**接口地址**:`/api/v1/conversations/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "targetStatus": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|transitionConversationStatusRequest|TransitionConversationStatusRequest|body|true|TransitionConversationStatusRequest|TransitionConversationStatusRequest|
|&emsp;&emsp;targetStatus|可用值:ACTIVE,CLOSED,ARCHIVED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 消息反馈


**接口地址**:`/api/v1/conversations/{id}/messages/{msgId}/feedback`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "feedback": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|msgId||path|true|string||
|messageFeedbackRequest|MessageFeedbackRequest|body|true|MessageFeedbackRequest|MessageFeedbackRequest|
|&emsp;&emsp;feedback|可用值:LIKE,DISLIKE||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 会话详情


**接口地址**:`/api/v1/conversations/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ConversationResponse|ConversationResponse|
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;userId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"conversationId": "",
		"agentId": "",
		"userId": "",
		"title": "",
		"status": "",
		"messageCount": 0,
		"totalTokens": 0,
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 逻辑删除


**接口地址**:`/api/v1/conversations/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 对话管理


## 我的会话列表


**接口地址**:`/api/v1/conversations`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseConversationResponse|PageResponseConversationResponse|
|&emsp;&emsp;records||array|ConversationResponse|
|&emsp;&emsp;&emsp;&emsp;conversationId||string||
|&emsp;&emsp;&emsp;&emsp;agentId||string||
|&emsp;&emsp;&emsp;&emsp;userId||string||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"conversationId": "",
				"agentId": "",
				"userId": "",
				"title": "",
				"status": "",
				"messageCount": 0,
				"totalTokens": 0,
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建新会话


**接口地址**:`/api/v1/conversations`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "agentId": "",
  "title": "",
  "metadata": {}
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createConversationRequest|CreateConversationRequest|body|true|CreateConversationRequest|CreateConversationRequest|
|&emsp;&emsp;agentId|||true|string||
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;metadata|||false|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ConversationResponse|ConversationResponse|
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;userId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"conversationId": "",
		"agentId": "",
		"userId": "",
		"title": "",
		"status": "",
		"messageCount": 0,
		"totalTokens": 0,
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发送消息（SSE 流式）


**接口地址**:`/api/v1/conversations/{id}/stream`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`text/event-stream,application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|sendMessageRequest|SendMessageRequest|body|true|SendMessageRequest|SendMessageRequest|
|&emsp;&emsp;content|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SseEmitter|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|timeout||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"timeout": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 加载更早的消息


**接口地址**:`/api/v1/conversations/{id}/messages`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|before||query|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseMessageResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseMessageResponse|PageResponseMessageResponse|
|&emsp;&emsp;records||array|MessageResponse|
|&emsp;&emsp;&emsp;&emsp;messageId||string||
|&emsp;&emsp;&emsp;&emsp;conversationId||string||
|&emsp;&emsp;&emsp;&emsp;role||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;tokenCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;feedback||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"messageId": "",
				"conversationId": "",
				"role": "",
				"content": "",
				"tokenCount": 0,
				"feedback": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发送消息（非流式）


**接口地址**:`/api/v1/conversations/{id}/messages`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|sendMessageRequest|SendMessageRequest|body|true|SendMessageRequest|SendMessageRequest|
|&emsp;&emsp;content|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMessageResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||MessageResponse|MessageResponse|
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;role||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;tokenCount||integer(int32)||
|&emsp;&emsp;feedback||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"messageId": "",
		"conversationId": "",
		"role": "",
		"content": "",
		"tokenCount": 0,
		"feedback": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新标题


**接口地址**:`/api/v1/conversations/{id}/title`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "title": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|updateConversationTitleRequest|UpdateConversationTitleRequest|body|true|UpdateConversationTitleRequest|UpdateConversationTitleRequest|
|&emsp;&emsp;title|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 状态流转


**接口地址**:`/api/v1/conversations/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "targetStatus": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|transitionConversationStatusRequest|TransitionConversationStatusRequest|body|true|TransitionConversationStatusRequest|TransitionConversationStatusRequest|
|&emsp;&emsp;targetStatus|可用值:ACTIVE,CLOSED,ARCHIVED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 消息反馈


**接口地址**:`/api/v1/conversations/{id}/messages/{msgId}/feedback`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "feedback": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|msgId||path|true|string||
|messageFeedbackRequest|MessageFeedbackRequest|body|true|MessageFeedbackRequest|MessageFeedbackRequest|
|&emsp;&emsp;feedback|可用值:LIKE,DISLIKE||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 会话详情


**接口地址**:`/api/v1/conversations/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ConversationResponse|ConversationResponse|
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;userId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"conversationId": "",
		"agentId": "",
		"userId": "",
		"title": "",
		"status": "",
		"messageCount": 0,
		"totalTokens": 0,
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 逻辑删除


**接口地址**:`/api/v1/conversations/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 对话管理


## 我的会话列表


**接口地址**:`/api/v1/conversations`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseConversationResponse|PageResponseConversationResponse|
|&emsp;&emsp;records||array|ConversationResponse|
|&emsp;&emsp;&emsp;&emsp;conversationId||string||
|&emsp;&emsp;&emsp;&emsp;agentId||string||
|&emsp;&emsp;&emsp;&emsp;userId||string||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"conversationId": "",
				"agentId": "",
				"userId": "",
				"title": "",
				"status": "",
				"messageCount": 0,
				"totalTokens": 0,
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建新会话


**接口地址**:`/api/v1/conversations`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "agentId": "",
  "title": "",
  "metadata": {}
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createConversationRequest|CreateConversationRequest|body|true|CreateConversationRequest|CreateConversationRequest|
|&emsp;&emsp;agentId|||true|string||
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;metadata|||false|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ConversationResponse|ConversationResponse|
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;userId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"conversationId": "",
		"agentId": "",
		"userId": "",
		"title": "",
		"status": "",
		"messageCount": 0,
		"totalTokens": 0,
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发送消息（SSE 流式）


**接口地址**:`/api/v1/conversations/{id}/stream`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`text/event-stream,application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|sendMessageRequest|SendMessageRequest|body|true|SendMessageRequest|SendMessageRequest|
|&emsp;&emsp;content|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SseEmitter|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|timeout||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"timeout": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 加载更早的消息


**接口地址**:`/api/v1/conversations/{id}/messages`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|before||query|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseMessageResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseMessageResponse|PageResponseMessageResponse|
|&emsp;&emsp;records||array|MessageResponse|
|&emsp;&emsp;&emsp;&emsp;messageId||string||
|&emsp;&emsp;&emsp;&emsp;conversationId||string||
|&emsp;&emsp;&emsp;&emsp;role||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;tokenCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;feedback||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"messageId": "",
				"conversationId": "",
				"role": "",
				"content": "",
				"tokenCount": 0,
				"feedback": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发送消息（非流式）


**接口地址**:`/api/v1/conversations/{id}/messages`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|sendMessageRequest|SendMessageRequest|body|true|SendMessageRequest|SendMessageRequest|
|&emsp;&emsp;content|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMessageResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||MessageResponse|MessageResponse|
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;role||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;tokenCount||integer(int32)||
|&emsp;&emsp;feedback||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"messageId": "",
		"conversationId": "",
		"role": "",
		"content": "",
		"tokenCount": 0,
		"feedback": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新标题


**接口地址**:`/api/v1/conversations/{id}/title`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "title": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|updateConversationTitleRequest|UpdateConversationTitleRequest|body|true|UpdateConversationTitleRequest|UpdateConversationTitleRequest|
|&emsp;&emsp;title|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 状态流转


**接口地址**:`/api/v1/conversations/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "targetStatus": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|transitionConversationStatusRequest|TransitionConversationStatusRequest|body|true|TransitionConversationStatusRequest|TransitionConversationStatusRequest|
|&emsp;&emsp;targetStatus|可用值:ACTIVE,CLOSED,ARCHIVED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 消息反馈


**接口地址**:`/api/v1/conversations/{id}/messages/{msgId}/feedback`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "feedback": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|msgId||path|true|string||
|messageFeedbackRequest|MessageFeedbackRequest|body|true|MessageFeedbackRequest|MessageFeedbackRequest|
|&emsp;&emsp;feedback|可用值:LIKE,DISLIKE||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 会话详情


**接口地址**:`/api/v1/conversations/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultConversationResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ConversationResponse|ConversationResponse|
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;userId||string||
|&emsp;&emsp;title||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;messageCount||integer(int32)||
|&emsp;&emsp;totalTokens||integer(int64)||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"conversationId": "",
		"agentId": "",
		"userId": "",
		"title": "",
		"status": "",
		"messageCount": 0,
		"totalTokens": 0,
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 逻辑删除


**接口地址**:`/api/v1/conversations/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 工具平台


## 工具详情


**接口地址**:`/api/v1/tools/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultToolResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ToolResponse|ToolResponse|
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;tenantId|租户 ID|integer(int64)||
|&emsp;&emsp;name|工具名称|string||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;toolType|工具类型代码|string||
|&emsp;&emsp;toolTypeLabel|工具类型中文|string||
|&emsp;&emsp;schema||ToolSchema|ToolSchema|
|&emsp;&emsp;&emsp;&emsp;inputSchema||string||
|&emsp;&emsp;&emsp;&emsp;outputSchema||string||
|&emsp;&emsp;endpoint|连接端点|string||
|&emsp;&emsp;authConfig||AuthConfig|AuthConfig|
|&emsp;&emsp;&emsp;&emsp;authType||string||
|&emsp;&emsp;&emsp;&emsp;apiKey||string||
|&emsp;&emsp;&emsp;&emsp;token||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;password||string||
|&emsp;&emsp;&emsp;&emsp;headers||object||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;status|状态代码|string||
|&emsp;&emsp;statusLabel|状态中文|string||
|&emsp;&emsp;version|当前版本号|integer(int32)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"toolId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"toolType": "",
		"toolTypeLabel": "HTTP接口",
		"schema": {
			"inputSchema": "",
			"outputSchema": ""
		},
		"endpoint": "",
		"authConfig": {
			"authType": "",
			"apiKey": "",
			"token": "",
			"username": "",
			"password": "",
			"headers": {}
		},
		"requireApproval": true,
		"status": "",
		"statusLabel": "",
		"version": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 编辑工具配置


**接口地址**:`/api/v1/tools/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "description": "",
  "toolType": "",
  "inputSchema": "",
  "outputSchema": "",
  "endpoint": "",
  "authType": "",
  "apiKey": "",
  "token": "",
  "requireApproval": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||
|updateToolRequest|UpdateToolRequest|body|true|UpdateToolRequest|UpdateToolRequest|
|&emsp;&emsp;name|工具名称||false|string||
|&emsp;&emsp;description|功能描述||false|string||
|&emsp;&emsp;toolType|工具类型,可用值:MCP,HTTP,BUILTIN,CUSTOM||false|string||
|&emsp;&emsp;inputSchema|输入参数 JSON Schema||false|string||
|&emsp;&emsp;outputSchema|输出格式 JSON Schema||false|string||
|&emsp;&emsp;endpoint|连接端点||false|string||
|&emsp;&emsp;authType|认证类型||false|string||
|&emsp;&emsp;apiKey|API 密钥||false|string||
|&emsp;&emsp;token|Bearer Token||false|string||
|&emsp;&emsp;requireApproval|是否需要审批||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultToolResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ToolResponse|ToolResponse|
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;tenantId|租户 ID|integer(int64)||
|&emsp;&emsp;name|工具名称|string||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;toolType|工具类型代码|string||
|&emsp;&emsp;toolTypeLabel|工具类型中文|string||
|&emsp;&emsp;schema||ToolSchema|ToolSchema|
|&emsp;&emsp;&emsp;&emsp;inputSchema||string||
|&emsp;&emsp;&emsp;&emsp;outputSchema||string||
|&emsp;&emsp;endpoint|连接端点|string||
|&emsp;&emsp;authConfig||AuthConfig|AuthConfig|
|&emsp;&emsp;&emsp;&emsp;authType||string||
|&emsp;&emsp;&emsp;&emsp;apiKey||string||
|&emsp;&emsp;&emsp;&emsp;token||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;password||string||
|&emsp;&emsp;&emsp;&emsp;headers||object||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;status|状态代码|string||
|&emsp;&emsp;statusLabel|状态中文|string||
|&emsp;&emsp;version|当前版本号|integer(int32)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"toolId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"toolType": "",
		"toolTypeLabel": "HTTP接口",
		"schema": {
			"inputSchema": "",
			"outputSchema": ""
		},
		"endpoint": "",
		"authConfig": {
			"authType": "",
			"apiKey": "",
			"token": "",
			"username": "",
			"password": "",
			"headers": {}
		},
		"requireApproval": true,
		"status": "",
		"statusLabel": "",
		"version": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 工具列表（按类型筛选）


**接口地址**:`/api/v1/tools`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码|query|false|integer(int32)||
|size|每页数量|query|false|integer(int32)||
|type|工具类型（可选）|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseToolResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseToolResponse|PageResponseToolResponse|
|&emsp;&emsp;records||array|ToolResponse|
|&emsp;&emsp;&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;&emsp;&emsp;tenantId|租户 ID|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;name|工具名称|string||
|&emsp;&emsp;&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;&emsp;&emsp;toolType|工具类型代码|string||
|&emsp;&emsp;&emsp;&emsp;toolTypeLabel|工具类型中文|string||
|&emsp;&emsp;&emsp;&emsp;schema||ToolSchema|ToolSchema|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;inputSchema||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;outputSchema||string||
|&emsp;&emsp;&emsp;&emsp;endpoint|连接端点|string||
|&emsp;&emsp;&emsp;&emsp;authConfig||AuthConfig|AuthConfig|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;authType||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;apiKey||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;token||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;password||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;headers||object||
|&emsp;&emsp;&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;&emsp;&emsp;status|状态代码|string||
|&emsp;&emsp;&emsp;&emsp;statusLabel|状态中文|string||
|&emsp;&emsp;&emsp;&emsp;version|当前版本号|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updatedAt|更新时间|string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"toolId": "",
				"tenantId": 0,
				"name": "",
				"description": "",
				"toolType": "",
				"toolTypeLabel": "HTTP接口",
				"schema": {
					"inputSchema": "",
					"outputSchema": ""
				},
				"endpoint": "",
				"authConfig": {
					"authType": "",
					"apiKey": "",
					"token": "",
					"username": "",
					"password": "",
					"headers": {}
				},
				"requireApproval": true,
				"status": "",
				"statusLabel": "",
				"version": 0,
				"createdAt": "",
				"updatedAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 注册新工具


**接口地址**:`/api/v1/tools`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "订单查询",
  "description": "查询用户的订单信息，支持按时间范围和状态筛选",
  "toolType": "HTTP",
  "inputSchema": "",
  "outputSchema": "",
  "endpoint": "https://api.example.com/orders/query",
  "authType": "API_KEY",
  "apiKey": "",
  "token": "",
  "requireApproval": false
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createToolRequest|CreateToolRequest|body|true|CreateToolRequest|CreateToolRequest|
|&emsp;&emsp;name|工具名称||true|string||
|&emsp;&emsp;description|功能描述（供 LLM 理解）||false|string||
|&emsp;&emsp;toolType|工具类型,可用值:MCP,HTTP,BUILTIN,CUSTOM||true|string||
|&emsp;&emsp;inputSchema|输入参数 JSON Schema||false|string||
|&emsp;&emsp;outputSchema|输出格式 JSON Schema||false|string||
|&emsp;&emsp;endpoint|MCP SSE 端点或 HTTP URL||false|string||
|&emsp;&emsp;authType|认证类型,可用值:API_KEY,BEARER,BASIC,NONE||false|string||
|&emsp;&emsp;apiKey|API 密钥||false|string||
|&emsp;&emsp;token|Bearer Token||false|string||
|&emsp;&emsp;requireApproval|高风险操作需审批||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultToolResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ToolResponse|ToolResponse|
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;tenantId|租户 ID|integer(int64)||
|&emsp;&emsp;name|工具名称|string||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;toolType|工具类型代码|string||
|&emsp;&emsp;toolTypeLabel|工具类型中文|string||
|&emsp;&emsp;schema||ToolSchema|ToolSchema|
|&emsp;&emsp;&emsp;&emsp;inputSchema||string||
|&emsp;&emsp;&emsp;&emsp;outputSchema||string||
|&emsp;&emsp;endpoint|连接端点|string||
|&emsp;&emsp;authConfig||AuthConfig|AuthConfig|
|&emsp;&emsp;&emsp;&emsp;authType||string||
|&emsp;&emsp;&emsp;&emsp;apiKey||string||
|&emsp;&emsp;&emsp;&emsp;token||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;password||string||
|&emsp;&emsp;&emsp;&emsp;headers||object||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;status|状态代码|string||
|&emsp;&emsp;statusLabel|状态中文|string||
|&emsp;&emsp;version|当前版本号|integer(int32)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"toolId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"toolType": "",
		"toolTypeLabel": "HTTP接口",
		"schema": {
			"inputSchema": "",
			"outputSchema": ""
		},
		"endpoint": "",
		"authConfig": {
			"authType": "",
			"apiKey": "",
			"token": "",
			"username": "",
			"password": "",
			"headers": {}
		},
		"requireApproval": true,
		"status": "",
		"statusLabel": "",
		"version": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 测试工具调用


**接口地址**:`/api/v1/tools/{id}/test`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "params": {
    "status": "pending",
    "start_date": "2026-01-01"
  }
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||
|toolTestRequest|ToolTestRequest|body|true|ToolTestRequest|ToolTestRequest|
|&emsp;&emsp;params|测试参数||false|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultToolTestResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ToolTestResponse|ToolTestResponse|
|&emsp;&emsp;success|调用是否成功|boolean||
|&emsp;&emsp;result|调用返回结果|object||
|&emsp;&emsp;durationMs|调用耗时（毫秒）|integer(int64)||
|&emsp;&emsp;errorMessage|错误信息|string||
|&emsp;&emsp;invocationId|调用记录 ID|string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"success": true,
		"result": {},
		"durationMs": 0,
		"errorMessage": "",
		"invocationId": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 回滚到指定版本


**接口地址**:`/api/v1/tools/{id}/rollback`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||
|version|目标版本号|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultToolResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ToolResponse|ToolResponse|
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;tenantId|租户 ID|integer(int64)||
|&emsp;&emsp;name|工具名称|string||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;toolType|工具类型代码|string||
|&emsp;&emsp;toolTypeLabel|工具类型中文|string||
|&emsp;&emsp;schema||ToolSchema|ToolSchema|
|&emsp;&emsp;&emsp;&emsp;inputSchema||string||
|&emsp;&emsp;&emsp;&emsp;outputSchema||string||
|&emsp;&emsp;endpoint|连接端点|string||
|&emsp;&emsp;authConfig||AuthConfig|AuthConfig|
|&emsp;&emsp;&emsp;&emsp;authType||string||
|&emsp;&emsp;&emsp;&emsp;apiKey||string||
|&emsp;&emsp;&emsp;&emsp;token||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;password||string||
|&emsp;&emsp;&emsp;&emsp;headers||object||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;status|状态代码|string||
|&emsp;&emsp;statusLabel|状态中文|string||
|&emsp;&emsp;version|当前版本号|integer(int32)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"toolId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"toolType": "",
		"toolTypeLabel": "HTTP接口",
		"schema": {
			"inputSchema": "",
			"outputSchema": ""
		},
		"endpoint": "",
		"authConfig": {
			"authType": "",
			"apiKey": "",
			"token": "",
			"username": "",
			"password": "",
			"headers": {}
		},
		"requireApproval": true,
		"status": "",
		"statusLabel": "",
		"version": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启停工具


**接口地址**:`/api/v1/tools/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "status": "DISABLED"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||
|toggleToolStatusRequest|ToggleToolStatusRequest|body|true|ToggleToolStatusRequest|ToggleToolStatusRequest|
|&emsp;&emsp;status|目标状态,可用值:ACTIVE,DISABLED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultToolResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ToolResponse|ToolResponse|
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;tenantId|租户 ID|integer(int64)||
|&emsp;&emsp;name|工具名称|string||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;toolType|工具类型代码|string||
|&emsp;&emsp;toolTypeLabel|工具类型中文|string||
|&emsp;&emsp;schema||ToolSchema|ToolSchema|
|&emsp;&emsp;&emsp;&emsp;inputSchema||string||
|&emsp;&emsp;&emsp;&emsp;outputSchema||string||
|&emsp;&emsp;endpoint|连接端点|string||
|&emsp;&emsp;authConfig||AuthConfig|AuthConfig|
|&emsp;&emsp;&emsp;&emsp;authType||string||
|&emsp;&emsp;&emsp;&emsp;apiKey||string||
|&emsp;&emsp;&emsp;&emsp;token||string||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;password||string||
|&emsp;&emsp;&emsp;&emsp;headers||object||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;status|状态代码|string||
|&emsp;&emsp;statusLabel|状态中文|string||
|&emsp;&emsp;version|当前版本号|integer(int32)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"toolId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"toolType": "",
		"toolTypeLabel": "HTTP接口",
		"schema": {
			"inputSchema": "",
			"outputSchema": ""
		},
		"endpoint": "",
		"authConfig": {
			"authType": "",
			"apiKey": "",
			"token": "",
			"username": "",
			"password": "",
			"headers": {}
		},
		"requireApproval": true,
		"status": "",
		"statusLabel": "",
		"version": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 版本历史列表


**接口地址**:`/api/v1/tools/{id}/versions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListVersionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|VersionResponse|
|&emsp;&emsp;id|版本 ID|integer(int64)||
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;version|版本号|integer(int32)||
|&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;toolType|工具类型|string||
|&emsp;&emsp;endpointUrl|连接端点|string||
|&emsp;&emsp;inputSchema|输入 Schema|string||
|&emsp;&emsp;outputSchema|输出 Schema|string||
|&emsp;&emsp;authConfigJson|认证配置 JSON|string||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;changeReason|变更原因|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"toolId": "",
			"version": 0,
			"toolName": "",
			"toolType": "",
			"endpointUrl": "",
			"inputSchema": "",
			"outputSchema": "",
			"authConfigJson": "",
			"requireApproval": true,
			"description": "",
			"changeReason": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 版本详情


**接口地址**:`/api/v1/tools/{id}/versions/{version}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|工具 ID|path|true|string||
|version|版本号|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVersionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||VersionResponse|VersionResponse|
|&emsp;&emsp;id|版本 ID|integer(int64)||
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;version|版本号|integer(int32)||
|&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;toolType|工具类型|string||
|&emsp;&emsp;endpointUrl|连接端点|string||
|&emsp;&emsp;inputSchema|输入 Schema|string||
|&emsp;&emsp;outputSchema|输出 Schema|string||
|&emsp;&emsp;authConfigJson|认证配置 JSON|string||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;changeReason|变更原因|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"toolId": "",
		"version": 0,
		"toolName": "",
		"toolType": "",
		"endpointUrl": "",
		"inputSchema": "",
		"outputSchema": "",
		"authConfigJson": "",
		"requireApproval": true,
		"description": "",
		"changeReason": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 调用日志列表


**接口地址**:`/api/v1/tools/invocations`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|toolId|工具 ID（可选）|query|false|string||
|page|页码|query|false|integer(int32)||
|size|每页数量|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseToolInvocationLogResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseToolInvocationLogResponse|PageResponseToolInvocationLogResponse|
|&emsp;&emsp;records||array|ToolInvocationLogResponse|
|&emsp;&emsp;&emsp;&emsp;invocationId|调用记录 ID|string||
|&emsp;&emsp;&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;&emsp;&emsp;messageId|关联消息 ID|string||
|&emsp;&emsp;&emsp;&emsp;executionId|关联任务执行 ID|string||
|&emsp;&emsp;&emsp;&emsp;inputJson|输入参数 JSON|string||
|&emsp;&emsp;&emsp;&emsp;outputJson|输出结果 JSON|string||
|&emsp;&emsp;&emsp;&emsp;status|调用状态代码|string||
|&emsp;&emsp;&emsp;&emsp;statusLabel|调用状态中文|string||
|&emsp;&emsp;&emsp;&emsp;durationMs|调用耗时（毫秒）|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;errorMessage|错误信息|string||
|&emsp;&emsp;&emsp;&emsp;createdAt|调用时间|string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"invocationId": "",
				"toolId": "",
				"toolName": "",
				"conversationId": "",
				"messageId": "",
				"executionId": "",
				"inputJson": "",
				"outputJson": "",
				"status": "",
				"statusLabel": "",
				"durationMs": 0,
				"errorMessage": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 健康检查
暂无接口文档


# 角色管理


## 更新角色


**接口地址**:`/api/v1/roles/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "roleName": "高级数据分析师",
  "description": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|角色主键 ID|path|true|integer(int64)||
|updateRoleRequest|UpdateRoleRequest|body|true|UpdateRoleRequest|UpdateRoleRequest|
|&emsp;&emsp;roleName|角色名称||true|string||
|&emsp;&emsp;description|角色描述||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultRoleResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||RoleResponse|RoleResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;roleCode|角色编码|string||
|&emsp;&emsp;roleName|角色名称|string||
|&emsp;&emsp;description|角色描述|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"roleCode": "",
		"roleName": "",
		"description": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除角色


**接口地址**:`/api/v1/roles/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|角色主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 角色列表


**接口地址**:`/api/v1/roles`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListRoleResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|RoleResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;roleCode|角色编码|string||
|&emsp;&emsp;roleName|角色名称|string||
|&emsp;&emsp;description|角色描述|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"roleCode": "",
			"roleName": "",
			"description": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建角色


**接口地址**:`/api/v1/roles`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "tenantId": 0,
  "roleCode": "DATA_ANALYST",
  "roleName": "数据分析师",
  "description": "可查看和导出数据分析报表"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createRoleRequest|CreateRoleRequest|body|true|CreateRoleRequest|CreateRoleRequest|
|&emsp;&emsp;tenantId|所属租户标识||true|integer(int64)||
|&emsp;&emsp;roleCode|角色编码||true|string||
|&emsp;&emsp;roleName|角色名称||true|string||
|&emsp;&emsp;description|角色描述||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultRoleResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||RoleResponse|RoleResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;roleCode|角色编码|string||
|&emsp;&emsp;roleName|角色名称|string||
|&emsp;&emsp;description|角色描述|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"roleCode": "",
		"roleName": "",
		"description": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查看角色下的用户


**接口地址**:`/api/v1/roles/{id}/users`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|角色主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 为用户分配角色


**接口地址**:`/api/v1/roles/{id}/users`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>将角色分配给指定用户，权限变更后强制用户下线</p>



**请求示例**:


```javascript
{
  "userId": "user_abc123"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|角色主键 ID|path|true|integer(int64)||
|assignRoleToUserRequest|AssignRoleToUserRequest|body|true|AssignRoleToUserRequest|AssignRoleToUserRequest|
|&emsp;&emsp;userId|用户唯一标识||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 为角色分配权限


**接口地址**:`/api/v1/roles/{id}/permissions`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "permissionId": 1
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|角色主键 ID|path|true|integer(int64)||
|assignPermissionToRoleRequest|AssignPermissionToRoleRequest|body|true|AssignPermissionToRoleRequest|AssignPermissionToRoleRequest|
|&emsp;&emsp;permissionId|权限主键 ID||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 精度监控


## Grid Search 自动调优


**接口地址**:`/api/v1/knowledge-bases/{kbId}/precision/optimize`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|kbId|知识库 ID|path|true|string||
|datasetId|评测数据集 ID|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOptimizationResult|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||OptimizationResult|OptimizationResult|
|&emsp;&emsp;kbId||string||
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;totalCombinations||integer(int32)||
|&emsp;&emsp;evaluatedCombinations||integer(int32)||
|&emsp;&emsp;recommendations||array|ScoredCombination|
|&emsp;&emsp;&emsp;&emsp;params||ParamCombination|ParamCombination|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;searchStrategy||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;topK||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;rerankerType||string||
|&emsp;&emsp;&emsp;&emsp;score||number(double)||
|&emsp;&emsp;&emsp;&emsp;report||PrecisionReport|PrecisionReport|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;kbId||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;datasetId||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;totalQueries||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;recallAt5||number(double)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;mrr||number(double)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;ndcgAt5||number(double)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;hitRate||number(double)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;perQueryResults||array|QueryResult|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;question||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;expectedChunkId||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;hitRank||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;retrievedChunkIds||array|string|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;evaluatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"kbId": "",
		"datasetId": "",
		"totalCombinations": 0,
		"evaluatedCombinations": 0,
		"recommendations": [
			{
				"params": {
					"name": "",
					"searchStrategy": "",
					"topK": 0,
					"rerankerType": ""
				},
				"score": 0,
				"report": {
					"kbId": "",
					"datasetId": "",
					"totalQueries": 0,
					"recallAt5": 0,
					"mrr": 0,
					"ndcgAt5": 0,
					"hitRate": 0,
					"perQueryResults": [
						{
							"question": "",
							"expectedChunkId": "",
							"hitRank": 0,
							"retrievedChunkIds": []
						}
					],
					"evaluatedAt": ""
				}
			}
		]
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 运行精度评估


**接口地址**:`/api/v1/knowledge-bases/{kbId}/precision/evaluate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|kbId|知识库 ID|path|true|string||
|datasetId|评测数据集 ID|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPrecisionReport|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PrecisionReport|PrecisionReport|
|&emsp;&emsp;kbId||string||
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;totalQueries||integer(int32)||
|&emsp;&emsp;recallAt5||number(double)||
|&emsp;&emsp;mrr||number(double)||
|&emsp;&emsp;ndcgAt5||number(double)||
|&emsp;&emsp;hitRate||number(double)||
|&emsp;&emsp;perQueryResults||array|QueryResult|
|&emsp;&emsp;&emsp;&emsp;question||string||
|&emsp;&emsp;&emsp;&emsp;expectedChunkId||string||
|&emsp;&emsp;&emsp;&emsp;hitRank||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;retrievedChunkIds||array|string|
|&emsp;&emsp;evaluatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"kbId": "",
		"datasetId": "",
		"totalQueries": 0,
		"recallAt5": 0,
		"mrr": 0,
		"ndcgAt5": 0,
		"hitRate": 0,
		"perQueryResults": [
			{
				"question": "",
				"expectedChunkId": "",
				"hitRank": 0,
				"retrievedChunkIds": []
			}
		],
		"evaluatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 评测数据集


## 数据集列表


**接口地址**:`/api/v1/evaluation/datasets`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListDatasetResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|DatasetResponse|
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;itemCount||integer(int32)||
|&emsp;&emsp;source||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"datasetId": "",
			"name": "",
			"description": "",
			"itemCount": 0,
			"source": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建评测数据集


**接口地址**:`/api/v1/evaluation/datasets`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "description": "",
  "source": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createDatasetRequest|CreateDatasetRequest|body|true|CreateDatasetRequest|CreateDatasetRequest|
|&emsp;&emsp;name|||true|string||
|&emsp;&emsp;description|||false|string||
|&emsp;&emsp;source|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDatasetResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DatasetResponse|DatasetResponse|
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;itemCount||integer(int32)||
|&emsp;&emsp;source||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"datasetId": "",
		"name": "",
		"description": "",
		"itemCount": 0,
		"source": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 样本列表


**接口地址**:`/api/v1/evaluation/datasets/{datasetId}/items`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|datasetId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListItemResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|ItemResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;question||string||
|&emsp;&emsp;expectedAnswer||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"question": "",
			"expectedAnswer": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 添加样本


**接口地址**:`/api/v1/evaluation/datasets/{datasetId}/items`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "question": "",
  "expectedAnswer": "",
  "retrievalContext": "",
  "metadataJson": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|datasetId||path|true|string||
|addItemRequest|AddItemRequest|body|true|AddItemRequest|AddItemRequest|
|&emsp;&emsp;question|||true|string||
|&emsp;&emsp;expectedAnswer|||false|string||
|&emsp;&emsp;retrievalContext|||false|string||
|&emsp;&emsp;metadataJson|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultItemResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ItemResponse|ItemResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;question||string||
|&emsp;&emsp;expectedAnswer||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"question": "",
		"expectedAnswer": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 数据集详情


**接口地址**:`/api/v1/evaluation/datasets/{datasetId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|datasetId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDatasetResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DatasetResponse|DatasetResponse|
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;itemCount||integer(int32)||
|&emsp;&emsp;source||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"datasetId": "",
		"name": "",
		"description": "",
		"itemCount": 0,
		"source": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除数据集


**接口地址**:`/api/v1/evaluation/datasets/{datasetId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|datasetId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除样本


**接口地址**:`/api/v1/evaluation/datasets/{datasetId}/items/{itemId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|datasetId||path|true|string||
|itemId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 评测执行


## 执行评测（LLM-as-Judge）


**接口地址**:`/api/v1/evaluation/datasets/{datasetId}/run`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|datasetId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultEvaluationRunResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||EvaluationRunResponse|EvaluationRunResponse|
|&emsp;&emsp;evaluationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;overallScore||number||
|&emsp;&emsp;metricsJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;finishedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"evaluationId": "",
		"agentId": "",
		"datasetId": "",
		"status": "",
		"overallScore": 0,
		"metricsJson": "",
		"createdAt": "",
		"finishedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 评测历史列表


**接口地址**:`/api/v1/evaluation`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListEvaluationRunResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|EvaluationRunResponse|
|&emsp;&emsp;evaluationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;overallScore||number||
|&emsp;&emsp;metricsJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;finishedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"evaluationId": "",
			"agentId": "",
			"datasetId": "",
			"status": "",
			"overallScore": 0,
			"metricsJson": "",
			"createdAt": "",
			"finishedAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 评测结果详情


**接口地址**:`/api/v1/evaluation/{evaluationId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|evaluationId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultEvaluationRunResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||EvaluationRunResponse|EvaluationRunResponse|
|&emsp;&emsp;evaluationId||string||
|&emsp;&emsp;agentId||string||
|&emsp;&emsp;datasetId||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;overallScore||number||
|&emsp;&emsp;metricsJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;finishedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"evaluationId": "",
		"agentId": "",
		"datasetId": "",
		"status": "",
		"overallScore": 0,
		"metricsJson": "",
		"createdAt": "",
		"finishedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 权限管理


## 权限列表


**接口地址**:`/api/v1/permissions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListPermissionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|PermissionResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;permissionCode|权限编码|string||
|&emsp;&emsp;resource|资源路径|string||
|&emsp;&emsp;action|操作类型|string||
|&emsp;&emsp;description|权限描述|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"permissionCode": "",
			"resource": "",
			"action": "",
			"description": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建权限


**接口地址**:`/api/v1/permissions`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "permissionCode": "report:export",
  "resource": "report",
  "action": "WRITE",
  "description": "导出报表"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createPermissionRequest|CreatePermissionRequest|body|true|CreatePermissionRequest|CreatePermissionRequest|
|&emsp;&emsp;permissionCode|权限编码||true|string||
|&emsp;&emsp;resource|资源路径||true|string||
|&emsp;&emsp;action|操作类型,可用值:READ,WRITE,DELETE,ADMIN,PUBLISH||true|string||
|&emsp;&emsp;description|权限描述||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPermissionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PermissionResponse|PermissionResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;permissionCode|权限编码|string||
|&emsp;&emsp;resource|资源路径|string||
|&emsp;&emsp;action|操作类型|string||
|&emsp;&emsp;description|权限描述|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"permissionCode": "",
		"resource": "",
		"action": "",
		"description": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 人机协同审批


## 拒绝审批


**接口地址**:`/api/v1/approvals/{approvalId}/reject`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "reason": "参数异常，拒绝执行"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|approvalId||path|true|string||
|rejectRequest|RejectRequest|body|true|RejectRequest|RejectRequest|
|&emsp;&emsp;reason|拒绝原因||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultApprovalWorkflowResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ApprovalWorkflowResponse|ApprovalWorkflowResponse|
|&emsp;&emsp;approvalId|审批业务 ID|string||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;toolId|关联工具 ID|string||
|&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;executionId|关联任务执行 ID|string||
|&emsp;&emsp;requesterId|请求人 ID|string||
|&emsp;&emsp;approverId|审批人 ID|string||
|&emsp;&emsp;title|审批标题|string||
|&emsp;&emsp;operationDetail|操作内容详情（JSON）|string||
|&emsp;&emsp;status|审批状态: PENDING/APPROVED/REJECTED/TIMEOUT/CANCELLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;approveComment|审批意见|string||
|&emsp;&emsp;timeoutAt|超时时间|string(date-time)||
|&emsp;&emsp;approvedAt|审批完成时间|string(date-time)||
|&emsp;&emsp;expired|是否已超时|boolean||
|&emsp;&emsp;remainingSeconds|剩余时间（秒）|integer(int64)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"approvalId": "",
		"tenantId": 0,
		"toolId": "",
		"conversationId": "",
		"executionId": "",
		"requesterId": "",
		"approverId": "",
		"title": "",
		"operationDetail": "",
		"status": "",
		"statusLabel": "",
		"approveComment": "",
		"timeoutAt": "",
		"approvedAt": "",
		"expired": true,
		"remainingSeconds": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 同意审批


**接口地址**:`/api/v1/approvals/{approvalId}/approve`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "comment": "同意执行"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|approvalId||path|true|string||
|approveRequest|ApproveRequest|body|true|ApproveRequest|ApproveRequest|
|&emsp;&emsp;comment|审批意见||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultApprovalWorkflowResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ApprovalWorkflowResponse|ApprovalWorkflowResponse|
|&emsp;&emsp;approvalId|审批业务 ID|string||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;toolId|关联工具 ID|string||
|&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;executionId|关联任务执行 ID|string||
|&emsp;&emsp;requesterId|请求人 ID|string||
|&emsp;&emsp;approverId|审批人 ID|string||
|&emsp;&emsp;title|审批标题|string||
|&emsp;&emsp;operationDetail|操作内容详情（JSON）|string||
|&emsp;&emsp;status|审批状态: PENDING/APPROVED/REJECTED/TIMEOUT/CANCELLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;approveComment|审批意见|string||
|&emsp;&emsp;timeoutAt|超时时间|string(date-time)||
|&emsp;&emsp;approvedAt|审批完成时间|string(date-time)||
|&emsp;&emsp;expired|是否已超时|boolean||
|&emsp;&emsp;remainingSeconds|剩余时间（秒）|integer(int64)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"approvalId": "",
		"tenantId": 0,
		"toolId": "",
		"conversationId": "",
		"executionId": "",
		"requesterId": "",
		"approverId": "",
		"title": "",
		"operationDetail": "",
		"status": "",
		"statusLabel": "",
		"approveComment": "",
		"timeoutAt": "",
		"approvedAt": "",
		"expired": true,
		"remainingSeconds": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 审批列表（我的待审批-我已审批-我发起的）


**接口地址**:`/api/v1/approvals`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|filter||query|false|string||
|approverId||query|false|string||
|requesterId||query|false|string||
|status||query|false|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListApprovalWorkflowResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|ApprovalWorkflowResponse|
|&emsp;&emsp;approvalId|审批业务 ID|string||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;toolId|关联工具 ID|string||
|&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;executionId|关联任务执行 ID|string||
|&emsp;&emsp;requesterId|请求人 ID|string||
|&emsp;&emsp;approverId|审批人 ID|string||
|&emsp;&emsp;title|审批标题|string||
|&emsp;&emsp;operationDetail|操作内容详情（JSON）|string||
|&emsp;&emsp;status|审批状态: PENDING/APPROVED/REJECTED/TIMEOUT/CANCELLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;approveComment|审批意见|string||
|&emsp;&emsp;timeoutAt|超时时间|string(date-time)||
|&emsp;&emsp;approvedAt|审批完成时间|string(date-time)||
|&emsp;&emsp;expired|是否已超时|boolean||
|&emsp;&emsp;remainingSeconds|剩余时间（秒）|integer(int64)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"approvalId": "",
			"tenantId": 0,
			"toolId": "",
			"conversationId": "",
			"executionId": "",
			"requesterId": "",
			"approverId": "",
			"title": "",
			"operationDetail": "",
			"status": "",
			"statusLabel": "",
			"approveComment": "",
			"timeoutAt": "",
			"approvedAt": "",
			"expired": true,
			"remainingSeconds": 0,
			"createdAt": "",
			"updatedAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 审批详情


**接口地址**:`/api/v1/approvals/{approvalId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|approvalId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultApprovalWorkflowResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ApprovalWorkflowResponse|ApprovalWorkflowResponse|
|&emsp;&emsp;approvalId|审批业务 ID|string||
|&emsp;&emsp;tenantId|所属租户 ID|integer(int64)||
|&emsp;&emsp;toolId|关联工具 ID|string||
|&emsp;&emsp;conversationId|关联会话 ID|string||
|&emsp;&emsp;executionId|关联任务执行 ID|string||
|&emsp;&emsp;requesterId|请求人 ID|string||
|&emsp;&emsp;approverId|审批人 ID|string||
|&emsp;&emsp;title|审批标题|string||
|&emsp;&emsp;operationDetail|操作内容详情（JSON）|string||
|&emsp;&emsp;status|审批状态: PENDING/APPROVED/REJECTED/TIMEOUT/CANCELLED|string||
|&emsp;&emsp;statusLabel|状态标签|string||
|&emsp;&emsp;approveComment|审批意见|string||
|&emsp;&emsp;timeoutAt|超时时间|string(date-time)||
|&emsp;&emsp;approvedAt|审批完成时间|string(date-time)||
|&emsp;&emsp;expired|是否已超时|boolean||
|&emsp;&emsp;remainingSeconds|剩余时间（秒）|integer(int64)||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|&emsp;&emsp;updatedAt|最后更新时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"approvalId": "",
		"tenantId": 0,
		"toolId": "",
		"conversationId": "",
		"executionId": "",
		"requesterId": "",
		"approverId": "",
		"title": "",
		"operationDetail": "",
		"status": "",
		"statusLabel": "",
		"approveComment": "",
		"timeoutAt": "",
		"approvedAt": "",
		"expired": true,
		"remainingSeconds": 0,
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 审批统计


**接口地址**:`/api/v1/approvals/stats`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 认证管理


## 刷新 Token


**接口地址**:`/api/v1/auth/refresh`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>使用 RefreshToken 换取新的 AccessToken，旧 RefreshToken 立即失效</p>



**请求示例**:


```javascript
{
  "userId": "user_abc12345",
  "refreshToken": "a1b2c3d4-e5f6-..."
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|refreshTokenRequest|RefreshTokenRequest|body|true|RefreshTokenRequest|RefreshTokenRequest|
|&emsp;&emsp;userId|用户唯一标识||true|string||
|&emsp;&emsp;refreshToken|刷新令牌（登录时获取）||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|刷新成功，返回新 Token|ResultLoginResponse|
|400|Bad Request|ResultVoid|
|401|RefreshToken 无效或已过期|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||LoginResponse|LoginResponse|
|&emsp;&emsp;token||string||
|&emsp;&emsp;refreshToken||string||
|&emsp;&emsp;tokenType||string||
|&emsp;&emsp;expiresIn||integer(int64)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"token": "",
		"refreshToken": "",
		"tokenType": "",
		"expiresIn": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 用户登出


**接口地址**:`/api/v1/auth/logout`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:<p>从 Redis 中删除当前 AccessToken 和 RefreshToken</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 用户登录


**接口地址**:`/api/v1/auth/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>校验用户名密码，返回 Sa-Token 访问令牌和刷新令牌</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "tenantId": 0,
  "provider": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginRequest|LoginRequest|body|true|LoginRequest|LoginRequest|
|&emsp;&emsp;username|||true|string||
|&emsp;&emsp;password|||true|string||
|&emsp;&emsp;tenantId|||false|integer(int64)||
|&emsp;&emsp;provider|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|登录成功，返回 Token|ResultLoginResponse|
|400|Bad Request|ResultVoid|
|401|用户名或密码错误|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||LoginResponse|LoginResponse|
|&emsp;&emsp;token||string||
|&emsp;&emsp;refreshToken||string||
|&emsp;&emsp;tokenType||string||
|&emsp;&emsp;expiresIn||integer(int64)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"token": "",
		"refreshToken": "",
		"tokenType": "",
		"expiresIn": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 获取当前用户信息


**接口地址**:`/api/v1/auth/me`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:<p>从 Sa-Token Session 返回登录时写入的用户上下文，需登录</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUserInfo|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||UserInfo|UserInfo|
|&emsp;&emsp;userId||string||
|&emsp;&emsp;username||string||
|&emsp;&emsp;tenantId||integer(int64)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"userId": "",
		"username": "",
		"tenantId": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 任务规划与执行


## 规划任务


**接口地址**:`/api/v1/tasks/plan`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>调用 LLM 将用户意图拆解为 DAG 步骤序列</p>



**请求示例**:


```javascript
{
  "userIntent": "帮我查一下上周的订单总额并发送邮件给张三",
  "conversationId": "conv_abc123",
  "agentId": "agent_001"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createTaskPlanRequest|任务规划请求|body|true|CreateTaskPlanRequest|CreateTaskPlanRequest|
|&emsp;&emsp;userIntent|用户原始意图描述||true|string||
|&emsp;&emsp;conversationId|关联会话 ID||false|string||
|&emsp;&emsp;agentId|关联 Agent ID||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 执行任务


**接口地址**:`/api/v1/tasks/execute`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>根据已规划的执行计划启动 DAG 并行执行</p>



**请求示例**:


```javascript
{
  "executionId": "exec_abc123",
  "async": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|executeTaskRequest|执行任务请求|body|true|ExecuteTaskRequest|ExecuteTaskRequest|
|&emsp;&emsp;executionId|任务规划返回的 executionId||true|string||
|&emsp;&emsp;async|是否异步执行（默认 true）||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 取消执行


**接口地址**:`/api/v1/tasks/cancel`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>取消正在执行或等待中的任务</p>



**请求示例**:


```javascript
{
  "executionId": "exec_abc123"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|cancelTaskRequest|取消任务请求|body|true|CancelTaskRequest|CancelTaskRequest|
|&emsp;&emsp;executionId|要取消的任务执行 ID||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查询执行状态


**接口地址**:`/api/v1/tasks/{executionId}/status`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:<p>获取任务执行的当前进度和各步骤状态</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|executionId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultExecutionStatusResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ExecutionStatusResponse|ExecutionStatusResponse|
|&emsp;&emsp;executionId||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;totalSteps||integer(int32)||
|&emsp;&emsp;completedSteps||integer(int32)||
|&emsp;&emsp;failedStepId||string||
|&emsp;&emsp;errorMessage||string||
|&emsp;&emsp;steps||array|StepStatusResponse|
|&emsp;&emsp;&emsp;&emsp;stepId||string||
|&emsp;&emsp;&emsp;&emsp;action||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;retryCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;durationMs||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;errorMessage||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"executionId": "",
		"status": "",
		"totalSteps": 0,
		"completedSteps": 0,
		"failedStepId": "",
		"errorMessage": "",
		"steps": [
			{
				"stepId": "",
				"action": "",
				"status": "",
				"retryCount": 0,
				"durationMs": 0,
				"errorMessage": ""
			}
		]
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查询执行计划


**接口地址**:`/api/v1/tasks/{executionId}/plan`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:<p>获取已规划的任务 DAG 结构</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|executionId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 可用动作列表


**接口地址**:`/api/v1/tasks/handlers`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:<p>获取所有已注册的 ActionHandler 及其参数 Schema</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 任务引擎
暂无接口文档


# 审批管理
暂无接口文档


# 提示词管理


## 模板详情


**接口地址**:`/api/v1/prompts/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 编辑模板（仅 DRAFT 状态可编辑）


**接口地址**:`/api/v1/prompts/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "description": "",
  "templateText": "",
  "variables": [
    {
      "name": "",
      "type": "",
      "description": "",
      "defaultValue": "",
      "required": true
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|updatePromptRequest|编辑提示词模板请求|body|true|UpdatePromptRequest|UpdatePromptRequest|
|&emsp;&emsp;name|模板名称||false|string||
|&emsp;&emsp;description|模板描述||false|string||
|&emsp;&emsp;templateText|模板文本||false|string||
|&emsp;&emsp;variables|变量定义列表||false|array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name|||false|string||
|&emsp;&emsp;&emsp;&emsp;type|||false|string||
|&emsp;&emsp;&emsp;&emsp;description|||false|string||
|&emsp;&emsp;&emsp;&emsp;defaultValue|||false|string||
|&emsp;&emsp;&emsp;&emsp;required|||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 软删除模板


**接口地址**:`/api/v1/prompts/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 提示词模板列表（分页）


**接口地址**:`/api/v1/prompts`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码|query|false|integer(int32)||
|size|每页大小|query|false|integer(int32)||
|status|状态过滤: DRAFT/PUBLISHED/ARCHIVED|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponsePromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponsePromptResponse|PageResponsePromptResponse|
|&emsp;&emsp;records||array|PromptResponse|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;promptId||string||
|&emsp;&emsp;&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;templateText||string||
|&emsp;&emsp;&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"id": 0,
				"promptId": "",
				"tenantId": 0,
				"name": "",
				"description": "",
				"templateText": "",
				"variables": [
					{
						"name": "",
						"type": "",
						"description": "",
						"defaultValue": "",
						"required": true
					}
				],
				"version": 0,
				"status": "",
				"statusLabel": "",
				"abTestConfig": "",
				"createdAt": "",
				"updatedAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建提示词模板（草稿）


**接口地址**:`/api/v1/prompts`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "客服系统提示词",
  "description": "用于客服对话的 System Prompt",
  "templateText": "你是一个专业的{{role}}，名叫{{agent_name}}。\n请以{{tone}}的语气回答。",
  "variables": [
    {
      "name": "",
      "type": "",
      "description": "",
      "defaultValue": "",
      "required": true
    }
  ],
  "abTestConfig": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createPromptRequest|创建提示词模板请求|body|true|CreatePromptRequest|CreatePromptRequest|
|&emsp;&emsp;name|模板名称||true|string||
|&emsp;&emsp;description|模板描述||false|string||
|&emsp;&emsp;templateText|模板文本（支持 {{variable}} 占位符）||true|string||
|&emsp;&emsp;variables|变量定义列表||false|array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name|||false|string||
|&emsp;&emsp;&emsp;&emsp;type|||false|string||
|&emsp;&emsp;&emsp;&emsp;description|||false|string||
|&emsp;&emsp;&emsp;&emsp;defaultValue|||false|string||
|&emsp;&emsp;&emsp;&emsp;required|||false|boolean||
|&emsp;&emsp;abTestConfig|A/B 测试配置 JSON||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 回滚到指定版本


**接口地址**:`/api/v1/prompts/{id}/rollback`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|version|目标版本号|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 运行时渲染（仅已发布模板，供编排引擎调用）


**接口地址**:`/api/v1/prompts/{id}/render`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "variables": {
    "role": "客服助手",
    "agent_name": "小智",
    "tone": "亲切友好"
  }
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|previewRenderRequest|预览渲染请求|body|true|PreviewRenderRequest|PreviewRenderRequest|
|&emsp;&emsp;variables|变量名 → 变量值的映射||true|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": "",
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发布当前草稿（版本号 +1）


**接口地址**:`/api/v1/prompts/{id}/publish`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 变量填充预览渲染


**接口地址**:`/api/v1/prompts/{id}/preview`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "variables": {
    "role": "客服助手",
    "agent_name": "小智",
    "tone": "亲切友好"
  }
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|previewRenderRequest|预览渲染请求|body|true|PreviewRenderRequest|PreviewRenderRequest|
|&emsp;&emsp;variables|变量名 → 变量值的映射||true|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": "",
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 两个版本的差异对比


**接口地址**:`/api/v1/prompts/{id}/diff`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|v1|版本1|query|true|integer(int32)||
|v2|版本2|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDiffResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DiffResponse|DiffResponse|
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;version1||integer(int32)||
|&emsp;&emsp;version2||integer(int32)||
|&emsp;&emsp;templateText1||string||
|&emsp;&emsp;templateText2||string||
|&emsp;&emsp;variables1||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;variables2||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;changeLog1||string||
|&emsp;&emsp;changeLog2||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"promptId": "",
		"version1": 0,
		"version2": 0,
		"templateText1": "",
		"templateText2": "",
		"variables1": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"variables2": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"changeLog1": "",
		"changeLog2": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 归档模板


**接口地址**:`/api/v1/prompts/{id}/archive`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查看版本历史列表


**接口地址**:`/api/v1/prompts/{id}/versions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListVersionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|VersionResponse|
|&emsp;&emsp;id|版本 ID|integer(int64)||
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;version|版本号|integer(int32)||
|&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;toolType|工具类型|string||
|&emsp;&emsp;endpointUrl|连接端点|string||
|&emsp;&emsp;inputSchema|输入 Schema|string||
|&emsp;&emsp;outputSchema|输出 Schema|string||
|&emsp;&emsp;authConfigJson|认证配置 JSON|string||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;changeReason|变更原因|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"toolId": "",
			"version": 0,
			"toolName": "",
			"toolType": "",
			"endpointUrl": "",
			"inputSchema": "",
			"outputSchema": "",
			"authConfigJson": "",
			"requireApproval": true,
			"description": "",
			"changeReason": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查看指定版本内容


**接口地址**:`/api/v1/prompts/{id}/versions/{version}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|version|版本号|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVersionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||VersionResponse|VersionResponse|
|&emsp;&emsp;id|版本 ID|integer(int64)||
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;version|版本号|integer(int32)||
|&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;toolType|工具类型|string||
|&emsp;&emsp;endpointUrl|连接端点|string||
|&emsp;&emsp;inputSchema|输入 Schema|string||
|&emsp;&emsp;outputSchema|输出 Schema|string||
|&emsp;&emsp;authConfigJson|认证配置 JSON|string||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;changeReason|变更原因|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"toolId": "",
		"version": 0,
		"toolName": "",
		"toolType": "",
		"endpointUrl": "",
		"inputSchema": "",
		"outputSchema": "",
		"authConfigJson": "",
		"requireApproval": true,
		"description": "",
		"changeReason": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 提示词管理


## 模板详情


**接口地址**:`/api/v1/prompts/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 编辑模板（仅 DRAFT 状态可编辑）


**接口地址**:`/api/v1/prompts/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "description": "",
  "templateText": "",
  "variables": [
    {
      "name": "",
      "type": "",
      "description": "",
      "defaultValue": "",
      "required": true
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|updatePromptRequest|编辑提示词模板请求|body|true|UpdatePromptRequest|UpdatePromptRequest|
|&emsp;&emsp;name|模板名称||false|string||
|&emsp;&emsp;description|模板描述||false|string||
|&emsp;&emsp;templateText|模板文本||false|string||
|&emsp;&emsp;variables|变量定义列表||false|array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name|||false|string||
|&emsp;&emsp;&emsp;&emsp;type|||false|string||
|&emsp;&emsp;&emsp;&emsp;description|||false|string||
|&emsp;&emsp;&emsp;&emsp;defaultValue|||false|string||
|&emsp;&emsp;&emsp;&emsp;required|||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 软删除模板


**接口地址**:`/api/v1/prompts/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 提示词模板列表（分页）


**接口地址**:`/api/v1/prompts`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码|query|false|integer(int32)||
|size|每页大小|query|false|integer(int32)||
|status|状态过滤: DRAFT/PUBLISHED/ARCHIVED|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponsePromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponsePromptResponse|PageResponsePromptResponse|
|&emsp;&emsp;records||array|PromptResponse|
|&emsp;&emsp;&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;promptId||string||
|&emsp;&emsp;&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;templateText||string||
|&emsp;&emsp;&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"id": 0,
				"promptId": "",
				"tenantId": 0,
				"name": "",
				"description": "",
				"templateText": "",
				"variables": [
					{
						"name": "",
						"type": "",
						"description": "",
						"defaultValue": "",
						"required": true
					}
				],
				"version": 0,
				"status": "",
				"statusLabel": "",
				"abTestConfig": "",
				"createdAt": "",
				"updatedAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建提示词模板（草稿）


**接口地址**:`/api/v1/prompts`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "客服系统提示词",
  "description": "用于客服对话的 System Prompt",
  "templateText": "你是一个专业的{{role}}，名叫{{agent_name}}。\n请以{{tone}}的语气回答。",
  "variables": [
    {
      "name": "",
      "type": "",
      "description": "",
      "defaultValue": "",
      "required": true
    }
  ],
  "abTestConfig": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createPromptRequest|创建提示词模板请求|body|true|CreatePromptRequest|CreatePromptRequest|
|&emsp;&emsp;name|模板名称||true|string||
|&emsp;&emsp;description|模板描述||false|string||
|&emsp;&emsp;templateText|模板文本（支持 {{variable}} 占位符）||true|string||
|&emsp;&emsp;variables|变量定义列表||false|array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name|||false|string||
|&emsp;&emsp;&emsp;&emsp;type|||false|string||
|&emsp;&emsp;&emsp;&emsp;description|||false|string||
|&emsp;&emsp;&emsp;&emsp;defaultValue|||false|string||
|&emsp;&emsp;&emsp;&emsp;required|||false|boolean||
|&emsp;&emsp;abTestConfig|A/B 测试配置 JSON||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 回滚到指定版本


**接口地址**:`/api/v1/prompts/{id}/rollback`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|version|目标版本号|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 运行时渲染（仅已发布模板，供编排引擎调用）


**接口地址**:`/api/v1/prompts/{id}/render`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "variables": {
    "role": "客服助手",
    "agent_name": "小智",
    "tone": "亲切友好"
  }
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|previewRenderRequest|预览渲染请求|body|true|PreviewRenderRequest|PreviewRenderRequest|
|&emsp;&emsp;variables|变量名 → 变量值的映射||true|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": "",
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 发布当前草稿（版本号 +1）


**接口地址**:`/api/v1/prompts/{id}/publish`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPromptResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PromptResponse|PromptResponse|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;templateText||string||
|&emsp;&emsp;variables||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;version||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;abTestConfig||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"promptId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"templateText": "",
		"variables": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"version": 0,
		"status": "",
		"statusLabel": "",
		"abTestConfig": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 变量填充预览渲染


**接口地址**:`/api/v1/prompts/{id}/preview`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "variables": {
    "role": "客服助手",
    "agent_name": "小智",
    "tone": "亲切友好"
  }
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|previewRenderRequest|预览渲染请求|body|true|PreviewRenderRequest|PreviewRenderRequest|
|&emsp;&emsp;variables|变量名 → 变量值的映射||true|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": "",
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 两个版本的差异对比


**接口地址**:`/api/v1/prompts/{id}/diff`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|v1|版本1|query|true|integer(int32)||
|v2|版本2|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDiffResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DiffResponse|DiffResponse|
|&emsp;&emsp;promptId||string||
|&emsp;&emsp;version1||integer(int32)||
|&emsp;&emsp;version2||integer(int32)||
|&emsp;&emsp;templateText1||string||
|&emsp;&emsp;templateText2||string||
|&emsp;&emsp;variables1||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;variables2||array|VariableDef|
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;type||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;defaultValue||string||
|&emsp;&emsp;&emsp;&emsp;required||boolean||
|&emsp;&emsp;changeLog1||string||
|&emsp;&emsp;changeLog2||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"promptId": "",
		"version1": 0,
		"version2": 0,
		"templateText1": "",
		"templateText2": "",
		"variables1": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"variables2": [
			{
				"name": "",
				"type": "",
				"description": "",
				"defaultValue": "",
				"required": true
			}
		],
		"changeLog1": "",
		"changeLog2": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 归档模板


**接口地址**:`/api/v1/prompts/{id}/archive`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查看版本历史列表


**接口地址**:`/api/v1/prompts/{id}/versions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListVersionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|VersionResponse|
|&emsp;&emsp;id|版本 ID|integer(int64)||
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;version|版本号|integer(int32)||
|&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;toolType|工具类型|string||
|&emsp;&emsp;endpointUrl|连接端点|string||
|&emsp;&emsp;inputSchema|输入 Schema|string||
|&emsp;&emsp;outputSchema|输出 Schema|string||
|&emsp;&emsp;authConfigJson|认证配置 JSON|string||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;changeReason|变更原因|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"toolId": "",
			"version": 0,
			"toolName": "",
			"toolType": "",
			"endpointUrl": "",
			"inputSchema": "",
			"outputSchema": "",
			"authConfigJson": "",
			"requireApproval": true,
			"description": "",
			"changeReason": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查看指定版本内容


**接口地址**:`/api/v1/prompts/{id}/versions/{version}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|模板 promptId|path|true|string||
|version|版本号|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVersionResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||VersionResponse|VersionResponse|
|&emsp;&emsp;id|版本 ID|integer(int64)||
|&emsp;&emsp;toolId|工具 ID|string||
|&emsp;&emsp;version|版本号|integer(int32)||
|&emsp;&emsp;toolName|工具名称|string||
|&emsp;&emsp;toolType|工具类型|string||
|&emsp;&emsp;endpointUrl|连接端点|string||
|&emsp;&emsp;inputSchema|输入 Schema|string||
|&emsp;&emsp;outputSchema|输出 Schema|string||
|&emsp;&emsp;authConfigJson|认证配置 JSON|string||
|&emsp;&emsp;requireApproval|是否需要审批|boolean||
|&emsp;&emsp;description|功能描述|string||
|&emsp;&emsp;changeReason|变更原因|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"toolId": "",
		"version": 0,
		"toolName": "",
		"toolType": "",
		"endpointUrl": "",
		"inputSchema": "",
		"outputSchema": "",
		"authConfigJson": "",
		"requireApproval": true,
		"description": "",
		"changeReason": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 文档管理


## 设置文档级精度参数覆盖


**接口地址**:`/api/v1/documents/{documentId}/precision-override`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "searchParamsOverrideJson": "",
  "multiStageOverrideJson": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||
|setPrecisionOverrideRequest|SetPrecisionOverrideRequest|body|true|SetPrecisionOverrideRequest|SetPrecisionOverrideRequest|
|&emsp;&emsp;searchParamsOverrideJson|||false|string||
|&emsp;&emsp;multiStageOverrideJson|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 上传文档（multipart-form-data，自动存 MinIO）


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/documents`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||
|file||query|true|file||
|chunk_strategy||query|false|string||
|chunk_size||query|false|integer(int32)||
|chunk_overlap||query|false|integer(int32)||
|chunk_config||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDocumentDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DocumentDTO|DocumentDTO|
|&emsp;&emsp;documentId||string||
|&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;filename||string||
|&emsp;&emsp;fileType||string||
|&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;minioPath||string||
|&emsp;&emsp;contentHash||string||
|&emsp;&emsp;chunkCount||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;errorMessage||string||
|&emsp;&emsp;uploadedBy||string||
|&emsp;&emsp;uploadedAt||string(date-time)||
|&emsp;&emsp;chunkStrategy||string||
|&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;searchStrategyOverride||string||
|&emsp;&emsp;searchParamsOverrideJson||string||
|&emsp;&emsp;multiStageOverrideJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"documentId": "",
		"knowledgeId": "",
		"filename": "",
		"fileType": "",
		"fileSize": 0,
		"minioPath": "",
		"contentHash": "",
		"chunkCount": 0,
		"status": "",
		"errorMessage": "",
		"uploadedBy": "",
		"uploadedAt": "",
		"chunkStrategy": "",
		"chunkConfigJson": "",
		"searchStrategyOverride": "",
		"searchParamsOverrideJson": "",
		"multiStageOverrideJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 批量触发文档解析


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/documents/batch-parse`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 手动触发文档异步解析（PENDING_PARSE-FAILED → 异步管线）


**接口地址**:`/api/v1/documents/{documentId}/parse`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDocumentDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DocumentDTO|DocumentDTO|
|&emsp;&emsp;documentId||string||
|&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;filename||string||
|&emsp;&emsp;fileType||string||
|&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;minioPath||string||
|&emsp;&emsp;contentHash||string||
|&emsp;&emsp;chunkCount||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;errorMessage||string||
|&emsp;&emsp;uploadedBy||string||
|&emsp;&emsp;uploadedAt||string(date-time)||
|&emsp;&emsp;chunkStrategy||string||
|&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;searchStrategyOverride||string||
|&emsp;&emsp;searchParamsOverrideJson||string||
|&emsp;&emsp;multiStageOverrideJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"documentId": "",
		"knowledgeId": "",
		"filename": "",
		"fileType": "",
		"fileSize": 0,
		"minioPath": "",
		"contentHash": "",
		"chunkCount": 0,
		"status": "",
		"errorMessage": "",
		"uploadedBy": "",
		"uploadedAt": "",
		"chunkStrategy": "",
		"chunkConfigJson": "",
		"searchStrategyOverride": "",
		"searchParamsOverrideJson": "",
		"multiStageOverrideJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 弃用文档（删除 Milvus 向量，保留 MinIO 文件和元数据）


**接口地址**:`/api/v1/documents/{documentId}/deprecate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDocumentDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DocumentDTO|DocumentDTO|
|&emsp;&emsp;documentId||string||
|&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;filename||string||
|&emsp;&emsp;fileType||string||
|&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;minioPath||string||
|&emsp;&emsp;contentHash||string||
|&emsp;&emsp;chunkCount||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;errorMessage||string||
|&emsp;&emsp;uploadedBy||string||
|&emsp;&emsp;uploadedAt||string(date-time)||
|&emsp;&emsp;chunkStrategy||string||
|&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;searchStrategyOverride||string||
|&emsp;&emsp;searchParamsOverrideJson||string||
|&emsp;&emsp;multiStageOverrideJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"documentId": "",
		"knowledgeId": "",
		"filename": "",
		"fileType": "",
		"fileSize": 0,
		"minioPath": "",
		"contentHash": "",
		"chunkCount": 0,
		"status": "",
		"errorMessage": "",
		"uploadedBy": "",
		"uploadedAt": "",
		"chunkStrategy": "",
		"chunkConfigJson": "",
		"searchStrategyOverride": "",
		"searchParamsOverrideJson": "",
		"multiStageOverrideJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 文档列表（按知识库）


**接口地址**:`/api/v1/documents`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||query|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseDocumentDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseDocumentDTO|PageResponseDocumentDTO|
|&emsp;&emsp;records||array|DocumentDTO|
|&emsp;&emsp;&emsp;&emsp;documentId||string||
|&emsp;&emsp;&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;&emsp;&emsp;filename||string||
|&emsp;&emsp;&emsp;&emsp;fileType||string||
|&emsp;&emsp;&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;minioPath||string||
|&emsp;&emsp;&emsp;&emsp;contentHash||string||
|&emsp;&emsp;&emsp;&emsp;chunkCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;errorMessage||string||
|&emsp;&emsp;&emsp;&emsp;uploadedBy||string||
|&emsp;&emsp;&emsp;&emsp;uploadedAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;chunkStrategy||string||
|&emsp;&emsp;&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;&emsp;&emsp;searchStrategyOverride||string||
|&emsp;&emsp;&emsp;&emsp;searchParamsOverrideJson||string||
|&emsp;&emsp;&emsp;&emsp;multiStageOverrideJson||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"documentId": "",
				"knowledgeId": "",
				"filename": "",
				"fileType": "",
				"fileSize": 0,
				"minioPath": "",
				"contentHash": "",
				"chunkCount": 0,
				"status": "",
				"errorMessage": "",
				"uploadedBy": "",
				"uploadedAt": "",
				"chunkStrategy": "",
				"chunkConfigJson": "",
				"searchStrategyOverride": "",
				"searchParamsOverrideJson": "",
				"multiStageOverrideJson": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 文档详情


**接口地址**:`/api/v1/documents/{documentId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDocumentDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DocumentDTO|DocumentDTO|
|&emsp;&emsp;documentId||string||
|&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;filename||string||
|&emsp;&emsp;fileType||string||
|&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;minioPath||string||
|&emsp;&emsp;contentHash||string||
|&emsp;&emsp;chunkCount||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;errorMessage||string||
|&emsp;&emsp;uploadedBy||string||
|&emsp;&emsp;uploadedAt||string(date-time)||
|&emsp;&emsp;chunkStrategy||string||
|&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;searchStrategyOverride||string||
|&emsp;&emsp;searchParamsOverrideJson||string||
|&emsp;&emsp;multiStageOverrideJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"documentId": "",
		"knowledgeId": "",
		"filename": "",
		"fileType": "",
		"fileSize": 0,
		"minioPath": "",
		"contentHash": "",
		"chunkCount": 0,
		"status": "",
		"errorMessage": "",
		"uploadedBy": "",
		"uploadedAt": "",
		"chunkStrategy": "",
		"chunkConfigJson": "",
		"searchStrategyOverride": "",
		"searchParamsOverrideJson": "",
		"multiStageOverrideJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除文档（含 MinIO + Milvus + MySQL，KB 创建者权限）


**接口地址**:`/api/v1/documents/{documentId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查询文档处理状态


**接口地址**:`/api/v1/documents/{documentId}/status`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": "",
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 下载原始文档（MinIO 流式代理）


**接口地址**:`/api/v1/documents/{documentId}/download`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查询文档切片列表


**接口地址**:`/api/v1/documents/{documentId}/chunks`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|documentId||path|true|string||
|offset||query|false|integer(int32)||
|limit||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 文件管理


## 文件管理列表（含状态标签、操作权限）


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/files`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 文件状态汇总


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/files/summary`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 效果评估
暂无接口文档


# 意图管理


## 意图详情


**接口地址**:`/api/v1/intents/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentResponse|IntentResponse|
|&emsp;&emsp;intentId||string||
|&emsp;&emsp;intentCode||string||
|&emsp;&emsp;intentName||string||
|&emsp;&emsp;category||string||
|&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;examples||array|string|
|&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"intentId": "",
		"intentCode": "",
		"intentName": "",
		"category": "",
		"patterns": [],
		"examples": [],
		"llmPrompt": "",
		"requiredParams": [],
		"riskLevel": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 编辑意图


**接口地址**:`/api/v1/intents/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "intentName": "",
  "patterns": [],
  "examples": [],
  "llmPrompt": "",
  "requiredParams": [],
  "riskLevel": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|updateIntentRequest|UpdateIntentRequest|body|true|UpdateIntentRequest|UpdateIntentRequest|
|&emsp;&emsp;intentName|||false|string||
|&emsp;&emsp;patterns|||false|array|string|
|&emsp;&emsp;examples|||false|array|string|
|&emsp;&emsp;llmPrompt|||false|string||
|&emsp;&emsp;requiredParams|||false|array|object|
|&emsp;&emsp;riskLevel|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentResponse|IntentResponse|
|&emsp;&emsp;intentId||string||
|&emsp;&emsp;intentCode||string||
|&emsp;&emsp;intentName||string||
|&emsp;&emsp;category||string||
|&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;examples||array|string|
|&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"intentId": "",
		"intentCode": "",
		"intentName": "",
		"category": "",
		"patterns": [],
		"examples": [],
		"llmPrompt": "",
		"requiredParams": [],
		"riskLevel": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除意图


**接口地址**:`/api/v1/intents/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 意图列表


**接口地址**:`/api/v1/intents`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseIntentResponse|PageResponseIntentResponse|
|&emsp;&emsp;records||array|IntentResponse|
|&emsp;&emsp;&emsp;&emsp;intentId||string||
|&emsp;&emsp;&emsp;&emsp;intentCode||string||
|&emsp;&emsp;&emsp;&emsp;intentName||string||
|&emsp;&emsp;&emsp;&emsp;category||string||
|&emsp;&emsp;&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;&emsp;&emsp;examples||array|string|
|&emsp;&emsp;&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"intentId": "",
				"intentCode": "",
				"intentName": "",
				"category": "",
				"patterns": [],
				"examples": [],
				"llmPrompt": "",
				"requiredParams": [],
				"riskLevel": "",
				"status": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建意图


**接口地址**:`/api/v1/intents`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "intentCode": "",
  "intentName": "",
  "category": "",
  "patterns": [],
  "examples": [],
  "llmPrompt": "",
  "requiredParams": [],
  "riskLevel": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createIntentRequest|CreateIntentRequest|body|true|CreateIntentRequest|CreateIntentRequest|
|&emsp;&emsp;intentCode|||true|string||
|&emsp;&emsp;intentName|||true|string||
|&emsp;&emsp;category|||false|string||
|&emsp;&emsp;patterns|||false|array|string|
|&emsp;&emsp;examples|||false|array|string|
|&emsp;&emsp;llmPrompt|||false|string||
|&emsp;&emsp;requiredParams|||false|array|object|
|&emsp;&emsp;riskLevel|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentResponse|IntentResponse|
|&emsp;&emsp;intentId||string||
|&emsp;&emsp;intentCode||string||
|&emsp;&emsp;intentName||string||
|&emsp;&emsp;category||string||
|&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;examples||array|string|
|&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"intentId": "",
		"intentCode": "",
		"intentName": "",
		"category": "",
		"patterns": [],
		"examples": [],
		"llmPrompt": "",
		"requiredParams": [],
		"riskLevel": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 测试意图识别


**接口地址**:`/api/v1/intents/{id}/test`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "input": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|intentTestRequest|IntentTestRequest|body|true|IntentTestRequest|IntentTestRequest|
|&emsp;&emsp;input|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentTestResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentTestResponse|IntentTestResponse|
|&emsp;&emsp;input||string||
|&emsp;&emsp;expectedIntentCode||string||
|&emsp;&emsp;actualIntentCode||string||
|&emsp;&emsp;matched||boolean||
|&emsp;&emsp;confidence||number(double)||
|&emsp;&emsp;matchedLayer||string||
|&emsp;&emsp;reasoning||string||
|&emsp;&emsp;costMs||number(double)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"input": "",
		"expectedIntentCode": "",
		"actualIntentCode": "",
		"matched": true,
		"confidence": 0,
		"matchedLayer": "",
		"reasoning": "",
		"costMs": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 批量测试


**接口地址**:`/api/v1/intents/batch-test`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "items": [
    {
      "input": "",
      "expectedIntentCode": ""
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|batchTestRequest|BatchTestRequest|body|true|BatchTestRequest|BatchTestRequest|
|&emsp;&emsp;items|||false|array|TestItem|
|&emsp;&emsp;&emsp;&emsp;input|||false|string||
|&emsp;&emsp;&emsp;&emsp;expectedIntentCode|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultBatchTestResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||BatchTestResponse|BatchTestResponse|
|&emsp;&emsp;total||integer(int32)||
|&emsp;&emsp;correct||integer(int32)||
|&emsp;&emsp;accuracy||number(double)||
|&emsp;&emsp;details||array|IntentTestResponse|
|&emsp;&emsp;&emsp;&emsp;input||string||
|&emsp;&emsp;&emsp;&emsp;expectedIntentCode||string||
|&emsp;&emsp;&emsp;&emsp;actualIntentCode||string||
|&emsp;&emsp;&emsp;&emsp;matched||boolean||
|&emsp;&emsp;&emsp;&emsp;confidence||number(double)||
|&emsp;&emsp;&emsp;&emsp;matchedLayer||string||
|&emsp;&emsp;&emsp;&emsp;reasoning||string||
|&emsp;&emsp;&emsp;&emsp;costMs||number(double)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"total": 0,
		"correct": 0,
		"accuracy": 0,
		"details": [
			{
				"input": "",
				"expectedIntentCode": "",
				"actualIntentCode": "",
				"matched": true,
				"confidence": 0,
				"matchedLayer": "",
				"reasoning": "",
				"costMs": 0
			}
		]
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启停意图


**接口地址**:`/api/v1/intents/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "status": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|toggleIntentStatusRequest|ToggleIntentStatusRequest|body|true|ToggleIntentStatusRequest|ToggleIntentStatusRequest|
|&emsp;&emsp;status|可用值:ACTIVE,DISABLED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 意图管理


## 意图详情


**接口地址**:`/api/v1/intents/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentResponse|IntentResponse|
|&emsp;&emsp;intentId||string||
|&emsp;&emsp;intentCode||string||
|&emsp;&emsp;intentName||string||
|&emsp;&emsp;category||string||
|&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;examples||array|string|
|&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"intentId": "",
		"intentCode": "",
		"intentName": "",
		"category": "",
		"patterns": [],
		"examples": [],
		"llmPrompt": "",
		"requiredParams": [],
		"riskLevel": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 编辑意图


**接口地址**:`/api/v1/intents/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "intentName": "",
  "patterns": [],
  "examples": [],
  "llmPrompt": "",
  "requiredParams": [],
  "riskLevel": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|updateIntentRequest|UpdateIntentRequest|body|true|UpdateIntentRequest|UpdateIntentRequest|
|&emsp;&emsp;intentName|||false|string||
|&emsp;&emsp;patterns|||false|array|string|
|&emsp;&emsp;examples|||false|array|string|
|&emsp;&emsp;llmPrompt|||false|string||
|&emsp;&emsp;requiredParams|||false|array|object|
|&emsp;&emsp;riskLevel|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentResponse|IntentResponse|
|&emsp;&emsp;intentId||string||
|&emsp;&emsp;intentCode||string||
|&emsp;&emsp;intentName||string||
|&emsp;&emsp;category||string||
|&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;examples||array|string|
|&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"intentId": "",
		"intentCode": "",
		"intentName": "",
		"category": "",
		"patterns": [],
		"examples": [],
		"llmPrompt": "",
		"requiredParams": [],
		"riskLevel": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除意图


**接口地址**:`/api/v1/intents/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 意图列表


**接口地址**:`/api/v1/intents`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseIntentResponse|PageResponseIntentResponse|
|&emsp;&emsp;records||array|IntentResponse|
|&emsp;&emsp;&emsp;&emsp;intentId||string||
|&emsp;&emsp;&emsp;&emsp;intentCode||string||
|&emsp;&emsp;&emsp;&emsp;intentName||string||
|&emsp;&emsp;&emsp;&emsp;category||string||
|&emsp;&emsp;&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;&emsp;&emsp;examples||array|string|
|&emsp;&emsp;&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"intentId": "",
				"intentCode": "",
				"intentName": "",
				"category": "",
				"patterns": [],
				"examples": [],
				"llmPrompt": "",
				"requiredParams": [],
				"riskLevel": "",
				"status": "",
				"createdAt": ""
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建意图


**接口地址**:`/api/v1/intents`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "intentCode": "",
  "intentName": "",
  "category": "",
  "patterns": [],
  "examples": [],
  "llmPrompt": "",
  "requiredParams": [],
  "riskLevel": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createIntentRequest|CreateIntentRequest|body|true|CreateIntentRequest|CreateIntentRequest|
|&emsp;&emsp;intentCode|||true|string||
|&emsp;&emsp;intentName|||true|string||
|&emsp;&emsp;category|||false|string||
|&emsp;&emsp;patterns|||false|array|string|
|&emsp;&emsp;examples|||false|array|string|
|&emsp;&emsp;llmPrompt|||false|string||
|&emsp;&emsp;requiredParams|||false|array|object|
|&emsp;&emsp;riskLevel|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentResponse|IntentResponse|
|&emsp;&emsp;intentId||string||
|&emsp;&emsp;intentCode||string||
|&emsp;&emsp;intentName||string||
|&emsp;&emsp;category||string||
|&emsp;&emsp;patterns||array|string|
|&emsp;&emsp;examples||array|string|
|&emsp;&emsp;llmPrompt||string||
|&emsp;&emsp;requiredParams||array|object|
|&emsp;&emsp;riskLevel||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"intentId": "",
		"intentCode": "",
		"intentName": "",
		"category": "",
		"patterns": [],
		"examples": [],
		"llmPrompt": "",
		"requiredParams": [],
		"riskLevel": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 测试意图识别


**接口地址**:`/api/v1/intents/{id}/test`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "input": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|intentTestRequest|IntentTestRequest|body|true|IntentTestRequest|IntentTestRequest|
|&emsp;&emsp;input|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIntentTestResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IntentTestResponse|IntentTestResponse|
|&emsp;&emsp;input||string||
|&emsp;&emsp;expectedIntentCode||string||
|&emsp;&emsp;actualIntentCode||string||
|&emsp;&emsp;matched||boolean||
|&emsp;&emsp;confidence||number(double)||
|&emsp;&emsp;matchedLayer||string||
|&emsp;&emsp;reasoning||string||
|&emsp;&emsp;costMs||number(double)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"input": "",
		"expectedIntentCode": "",
		"actualIntentCode": "",
		"matched": true,
		"confidence": 0,
		"matchedLayer": "",
		"reasoning": "",
		"costMs": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 批量测试


**接口地址**:`/api/v1/intents/batch-test`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "items": [
    {
      "input": "",
      "expectedIntentCode": ""
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|batchTestRequest|BatchTestRequest|body|true|BatchTestRequest|BatchTestRequest|
|&emsp;&emsp;items|||false|array|TestItem|
|&emsp;&emsp;&emsp;&emsp;input|||false|string||
|&emsp;&emsp;&emsp;&emsp;expectedIntentCode|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultBatchTestResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||BatchTestResponse|BatchTestResponse|
|&emsp;&emsp;total||integer(int32)||
|&emsp;&emsp;correct||integer(int32)||
|&emsp;&emsp;accuracy||number(double)||
|&emsp;&emsp;details||array|IntentTestResponse|
|&emsp;&emsp;&emsp;&emsp;input||string||
|&emsp;&emsp;&emsp;&emsp;expectedIntentCode||string||
|&emsp;&emsp;&emsp;&emsp;actualIntentCode||string||
|&emsp;&emsp;&emsp;&emsp;matched||boolean||
|&emsp;&emsp;&emsp;&emsp;confidence||number(double)||
|&emsp;&emsp;&emsp;&emsp;matchedLayer||string||
|&emsp;&emsp;&emsp;&emsp;reasoning||string||
|&emsp;&emsp;&emsp;&emsp;costMs||number(double)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"total": 0,
		"correct": 0,
		"accuracy": 0,
		"details": [
			{
				"input": "",
				"expectedIntentCode": "",
				"actualIntentCode": "",
				"matched": true,
				"confidence": 0,
				"matchedLayer": "",
				"reasoning": "",
				"costMs": 0
			}
		]
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启停意图


**接口地址**:`/api/v1/intents/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "status": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|string||
|toggleIntentStatusRequest|ToggleIntentStatusRequest|body|true|ToggleIntentStatusRequest|ToggleIntentStatusRequest|
|&emsp;&emsp;status|可用值:ACTIVE,DISABLED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 用户管理


## 用户详情


**接口地址**:`/api/v1/users/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUserResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||UserResponse|UserResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;userId|用户唯一标识|string||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"userId": "",
		"username": "",
		"email": "",
		"phone": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新用户信息


**接口地址**:`/api/v1/users/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "email": "newemail@acme.com",
  "phone": "13900139000"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户主键 ID|path|true|integer(int64)||
|updateUserRequest|UpdateUserRequest|body|true|UpdateUserRequest|UpdateUserRequest|
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;phone|手机号||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUserResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||UserResponse|UserResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;userId|用户唯一标识|string||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"userId": "",
		"username": "",
		"email": "",
		"phone": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 用户注册


**接口地址**:`/api/v1/users/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>公开接口，新用户注册并分配默认 VIEWER 角色</p>



**请求示例**:


```javascript
{
  "tenantId": 0,
  "username": "zhangsan",
  "password": "P@ssw0rd!",
  "email": "zhangsan@acme.com",
  "phone": "13800138000"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createUserRequest|CreateUserRequest|body|true|CreateUserRequest|CreateUserRequest|
|&emsp;&emsp;tenantId|所属租户标识||true|integer(int64)||
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码（至少 8 位）||true|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;phone|手机号||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|注册成功|ResultUserResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||UserResponse|UserResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;userId|用户唯一标识|string||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"userId": "",
		"username": "",
		"email": "",
		"phone": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启停用户


**接口地址**:`/api/v1/users/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "status": "DISABLED"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户主键 ID|path|true|integer(int64)||
|updateUserStatusRequest|UpdateUserStatusRequest|body|true|UpdateUserStatusRequest|UpdateUserStatusRequest|
|&emsp;&emsp;status|目标状态,可用值:ACTIVE,DISABLED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUserResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||UserResponse|UserResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;userId|用户唯一标识|string||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"userId": "",
		"username": "",
		"email": "",
		"phone": "",
		"status": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 修改密码


**接口地址**:`/api/v1/users/{id}/password`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:<p>当前用户修改自己的密码（需验证旧密码）</p>



**请求示例**:


```javascript
{
  "oldPassword": "",
  "newPassword": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户主键 ID|path|true|integer(int64)||
|changePasswordRequest|ChangePasswordRequest|body|true|ChangePasswordRequest|ChangePasswordRequest|
|&emsp;&emsp;oldPassword|旧密码||true|string||
|&emsp;&emsp;newPassword|新密码（至少 8 位）||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 用户列表


**接口地址**:`/api/v1/users`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码（从 0 开始）|query|false|integer(int32)||
|size|每页数量|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListUserResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|UserResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|所属租户|integer(int64)||
|&emsp;&emsp;userId|用户唯一标识|string||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"userId": "",
			"username": "",
			"email": "",
			"phone": "",
			"status": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 优化工单


## 提交解决方案


**接口地址**:`/api/v1/optimization-tickets/{ticketId}/resolve`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "resolution": "",
  "resolutionType": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|ticketId||path|true|string||
|resolveRequest|ResolveRequest|body|true|ResolveRequest|ResolveRequest|
|&emsp;&emsp;resolution|||true|string||
|&emsp;&emsp;resolutionType|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOptimizationTicketResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||OptimizationTicketResponse|OptimizationTicketResponse|
|&emsp;&emsp;ticketId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;severity||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;assignee||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;resolution||string||
|&emsp;&emsp;resolutionType||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"ticketId": "",
		"conversationId": "",
		"messageId": "",
		"issueType": "",
		"severity": "",
		"description": "",
		"assignee": "",
		"status": "",
		"resolution": "",
		"resolutionType": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新工单状态


**接口地址**:`/api/v1/optimization-tickets/{ticketId}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|ticketId||path|true|string||
|status||query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOptimizationTicketResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||OptimizationTicketResponse|OptimizationTicketResponse|
|&emsp;&emsp;ticketId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;severity||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;assignee||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;resolution||string||
|&emsp;&emsp;resolutionType||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"ticketId": "",
		"conversationId": "",
		"messageId": "",
		"issueType": "",
		"severity": "",
		"description": "",
		"assignee": "",
		"status": "",
		"resolution": "",
		"resolutionType": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 指派处理人


**接口地址**:`/api/v1/optimization-tickets/{ticketId}/assign`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "assignee": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|ticketId||path|true|string||
|assignRequest|AssignRequest|body|true|AssignRequest|AssignRequest|
|&emsp;&emsp;assignee|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOptimizationTicketResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||OptimizationTicketResponse|OptimizationTicketResponse|
|&emsp;&emsp;ticketId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;severity||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;assignee||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;resolution||string||
|&emsp;&emsp;resolutionType||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"ticketId": "",
		"conversationId": "",
		"messageId": "",
		"issueType": "",
		"severity": "",
		"description": "",
		"assignee": "",
		"status": "",
		"resolution": "",
		"resolutionType": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 工单列表


**接口地址**:`/api/v1/optimization-tickets`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListOptimizationTicketResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|OptimizationTicketResponse|
|&emsp;&emsp;ticketId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;severity||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;assignee||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;resolution||string||
|&emsp;&emsp;resolutionType||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"ticketId": "",
			"conversationId": "",
			"messageId": "",
			"issueType": "",
			"severity": "",
			"description": "",
			"assignee": "",
			"status": "",
			"resolution": "",
			"resolutionType": "",
			"createdAt": "",
			"updatedAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 工单详情


**接口地址**:`/api/v1/optimization-tickets/{ticketId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|ticketId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultOptimizationTicketResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||OptimizationTicketResponse|OptimizationTicketResponse|
|&emsp;&emsp;ticketId||string||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;issueType||string||
|&emsp;&emsp;severity||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;assignee||string||
|&emsp;&emsp;status||string||
|&emsp;&emsp;resolution||string||
|&emsp;&emsp;resolutionType||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"ticketId": "",
		"conversationId": "",
		"messageId": "",
		"issueType": "",
		"severity": "",
		"description": "",
		"assignee": "",
		"status": "",
		"resolution": "",
		"resolutionType": "",
		"createdAt": "",
		"updatedAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 反馈统计面板


**接口地址**:`/api/v1/optimization-tickets/feedback/stats`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|days||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultFeedbackStatsResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||FeedbackStatsResponse|FeedbackStatsResponse|
|&emsp;&emsp;totalTickets||integer(int64)||
|&emsp;&emsp;resolvedTickets||integer(int64)||
|&emsp;&emsp;resolveRate||string||
|&emsp;&emsp;periodDays||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"totalTickets": 0,
		"resolvedTickets": 0,
		"resolveRate": "",
		"periodDays": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 知识检索


## 混合检索（向量 + 关键词 + RRF 融合）


**接口地址**:`/api/v1/knowledge/search`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultSearchResultDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||SearchResultDTO|SearchResultDTO|
|&emsp;&emsp;query||string||
|&emsp;&emsp;hits||array|HitItem|
|&emsp;&emsp;&emsp;&emsp;chunkId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;documentId||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;chunkIndex||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;rrfScore||number||
|&emsp;&emsp;&emsp;&emsp;vectorScore||number||
|&emsp;&emsp;&emsp;&emsp;keywordScore||number||
|&emsp;&emsp;&emsp;&emsp;rankPosition||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;chunkMetadata||string||
|&emsp;&emsp;&emsp;&emsp;documentFilename||string||
|&emsp;&emsp;&emsp;&emsp;documentFileType||string||
|&emsp;&emsp;&emsp;&emsp;documentAccessUrl||string||
|&emsp;&emsp;&emsp;&emsp;documentUploadedAt||string(date-time)||
|&emsp;&emsp;documents||array|DocumentRef|
|&emsp;&emsp;&emsp;&emsp;documentId||string||
|&emsp;&emsp;&emsp;&emsp;filename||string||
|&emsp;&emsp;&emsp;&emsp;fileType||string||
|&emsp;&emsp;&emsp;&emsp;fileSize||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;accessUrl||string||
|&emsp;&emsp;&emsp;&emsp;uploadedAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"query": "",
		"hits": [
			{
				"chunkId": 0,
				"documentId": "",
				"content": "",
				"chunkIndex": 0,
				"rrfScore": 0,
				"vectorScore": 0,
				"keywordScore": 0,
				"rankPosition": 0,
				"chunkMetadata": "",
				"documentFilename": "",
				"documentFileType": "",
				"documentAccessUrl": "",
				"documentUploadedAt": ""
			}
		],
		"documents": [
			{
				"documentId": "",
				"filename": "",
				"fileType": "",
				"fileSize": 0,
				"accessUrl": "",
				"uploadedAt": ""
			}
		]
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 人工标注（EXCELLENT-NEEDS_FIX-SUPPLEMENT）


**接口地址**:`/api/v1/knowledge/hits/{id}/feedback`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查询可用检索策略预设列表


**接口地址**:`/api/v1/knowledge/precision-strategies`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListMapStringObject|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 命中记录列表


**接口地址**:`/api/v1/knowledge/hits`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|conversationId||query|true|string||
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListHitRecordDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|HitRecordDTO|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;conversationId||string||
|&emsp;&emsp;messageId||string||
|&emsp;&emsp;chunkId||integer(int64)||
|&emsp;&emsp;queryText||string||
|&emsp;&emsp;relevanceScore||number||
|&emsp;&emsp;rankPosition||integer(int32)||
|&emsp;&emsp;usedInPrompt||boolean||
|&emsp;&emsp;humanFeedback||string||
|&emsp;&emsp;feedbackNote||string||
|&emsp;&emsp;createdAt||string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"conversationId": "",
			"messageId": "",
			"chunkId": 0,
			"queryText": "",
			"relevanceScore": 0,
			"rankPosition": 0,
			"usedInPrompt": true,
			"humanFeedback": "",
			"feedbackNote": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 知识库
暂无接口文档


# 知识库管理


## 知识库详情


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultKnowledgeBaseDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||KnowledgeBaseDTO|KnowledgeBaseDTO|
|&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;embeddingModel||string||
|&emsp;&emsp;chunkSize||integer(int32)||
|&emsp;&emsp;chunkOverlap||integer(int32)||
|&emsp;&emsp;documentCount||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;createdBy||string||
|&emsp;&emsp;defaultChunkStrategy||string||
|&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;indexType||string||
|&emsp;&emsp;indexParamsJson||string||
|&emsp;&emsp;searchStrategy||string||
|&emsp;&emsp;searchParamsJson||string||
|&emsp;&emsp;multiStageParamsJson||string||
|&emsp;&emsp;monitoringParamsJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|&emsp;&emsp;createdByCurrentUser||boolean||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"knowledgeId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"embeddingModel": "",
		"chunkSize": 0,
		"chunkOverlap": 0,
		"documentCount": 0,
		"status": "",
		"statusLabel": "",
		"createdBy": "",
		"defaultChunkStrategy": "",
		"chunkConfigJson": "",
		"indexType": "",
		"indexParamsJson": "",
		"searchStrategy": "",
		"searchParamsJson": "",
		"multiStageParamsJson": "",
		"monitoringParamsJson": "",
		"createdAt": "",
		"updatedAt": "",
		"createdByCurrentUser": true
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新知识库名称-描述


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "description": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||
|updateKnowledgeBaseRequest|UpdateKnowledgeBaseRequest|body|true|UpdateKnowledgeBaseRequest|UpdateKnowledgeBaseRequest|
|&emsp;&emsp;name|||false|string||
|&emsp;&emsp;description|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 级联删除知识库（先弃用后删除）


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 设置知识库检索精度参数


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/precision-config`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "indexType": "",
  "indexParams": {},
  "searchStrategy": "",
  "searchParams": {},
  "multiStageParams": {},
  "monitoringParams": {}
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||
|setPrecisionConfigRequest|SetPrecisionConfigRequest|body|true|SetPrecisionConfigRequest|SetPrecisionConfigRequest|
|&emsp;&emsp;indexType|||false|string||
|&emsp;&emsp;indexParams|||false|object||
|&emsp;&emsp;searchStrategy|||false|string||
|&emsp;&emsp;searchParams|||false|object||
|&emsp;&emsp;multiStageParams|||false|object||
|&emsp;&emsp;monitoringParams|||false|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启用知识库


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/enable`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 弃用知识库


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/disable`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 设置知识库默认切片策略


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/chunk-config`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "defaultChunkStrategy": "",
  "chunkConfigJson": "",
  "chunkConfig": {}
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||
|updateChunkConfigRequest|UpdateChunkConfigRequest|body|true|UpdateChunkConfigRequest|UpdateChunkConfigRequest|
|&emsp;&emsp;defaultChunkStrategy|||false|string||
|&emsp;&emsp;chunkConfigJson|||false|string||
|&emsp;&emsp;chunkConfig|||false|object||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 知识库列表


**接口地址**:`/api/v1/knowledge-bases`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPageResponseKnowledgeBaseDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PageResponseKnowledgeBaseDTO|PageResponseKnowledgeBaseDTO|
|&emsp;&emsp;records||array|KnowledgeBaseDTO|
|&emsp;&emsp;&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;description||string||
|&emsp;&emsp;&emsp;&emsp;embeddingModel||string||
|&emsp;&emsp;&emsp;&emsp;chunkSize||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;chunkOverlap||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;documentCount||integer(int32)||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;&emsp;&emsp;createdBy||string||
|&emsp;&emsp;&emsp;&emsp;defaultChunkStrategy||string||
|&emsp;&emsp;&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;&emsp;&emsp;indexType||string||
|&emsp;&emsp;&emsp;&emsp;indexParamsJson||string||
|&emsp;&emsp;&emsp;&emsp;searchStrategy||string||
|&emsp;&emsp;&emsp;&emsp;searchParamsJson||string||
|&emsp;&emsp;&emsp;&emsp;multiStageParamsJson||string||
|&emsp;&emsp;&emsp;&emsp;monitoringParamsJson||string||
|&emsp;&emsp;&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updatedAt||string(date-time)||
|&emsp;&emsp;&emsp;&emsp;createdByCurrentUser||boolean||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;page||integer(int32)||
|&emsp;&emsp;size||integer(int32)||
|&emsp;&emsp;totalPages||integer(int32)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"records": [
			{
				"knowledgeId": "",
				"tenantId": 0,
				"name": "",
				"description": "",
				"embeddingModel": "",
				"chunkSize": 0,
				"chunkOverlap": 0,
				"documentCount": 0,
				"status": "",
				"statusLabel": "",
				"createdBy": "",
				"defaultChunkStrategy": "",
				"chunkConfigJson": "",
				"indexType": "",
				"indexParamsJson": "",
				"searchStrategy": "",
				"searchParamsJson": "",
				"multiStageParamsJson": "",
				"monitoringParamsJson": "",
				"createdAt": "",
				"updatedAt": "",
				"createdByCurrentUser": true
			}
		],
		"total": 0,
		"page": 0,
		"size": 0,
		"totalPages": 0
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建知识库


**接口地址**:`/api/v1/knowledge-bases`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "",
  "description": "",
  "embeddingModel": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createKnowledgeBaseRequest|CreateKnowledgeBaseRequest|body|true|CreateKnowledgeBaseRequest|CreateKnowledgeBaseRequest|
|&emsp;&emsp;name|||true|string||
|&emsp;&emsp;description|||false|string||
|&emsp;&emsp;embeddingModel|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultKnowledgeBaseDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||KnowledgeBaseDTO|KnowledgeBaseDTO|
|&emsp;&emsp;knowledgeId||string||
|&emsp;&emsp;tenantId||integer(int64)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;description||string||
|&emsp;&emsp;embeddingModel||string||
|&emsp;&emsp;chunkSize||integer(int32)||
|&emsp;&emsp;chunkOverlap||integer(int32)||
|&emsp;&emsp;documentCount||integer(int32)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;statusLabel||string||
|&emsp;&emsp;createdBy||string||
|&emsp;&emsp;defaultChunkStrategy||string||
|&emsp;&emsp;chunkConfigJson||string||
|&emsp;&emsp;indexType||string||
|&emsp;&emsp;indexParamsJson||string||
|&emsp;&emsp;searchStrategy||string||
|&emsp;&emsp;searchParamsJson||string||
|&emsp;&emsp;multiStageParamsJson||string||
|&emsp;&emsp;monitoringParamsJson||string||
|&emsp;&emsp;createdAt||string(date-time)||
|&emsp;&emsp;updatedAt||string(date-time)||
|&emsp;&emsp;createdByCurrentUser||boolean||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"knowledgeId": "",
		"tenantId": 0,
		"name": "",
		"description": "",
		"embeddingModel": "",
		"chunkSize": 0,
		"chunkOverlap": 0,
		"documentCount": 0,
		"status": "",
		"statusLabel": "",
		"createdBy": "",
		"defaultChunkStrategy": "",
		"chunkConfigJson": "",
		"indexType": "",
		"indexParamsJson": "",
		"searchStrategy": "",
		"searchParamsJson": "",
		"multiStageParamsJson": "",
		"monitoringParamsJson": "",
		"createdAt": "",
		"updatedAt": "",
		"createdByCurrentUser": true
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 知识库文档统计


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/stats`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 查询知识库当前生效的完整精度配置


**接口地址**:`/api/v1/knowledge-bases/{knowledgeId}/precision-config/resolved`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|knowledgeId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultPrecisionConfigDTO|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||PrecisionConfigDTO|PrecisionConfigDTO|
|&emsp;&emsp;indexType||string||
|&emsp;&emsp;indexParams||object||
|&emsp;&emsp;searchStrategy||string||
|&emsp;&emsp;searchParams||object||
|&emsp;&emsp;multiStageParams||object||
|&emsp;&emsp;monitoringParams||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"indexType": "",
		"indexParams": {},
		"searchStrategy": "",
		"searchParams": {},
		"multiStageParams": {},
		"monitoringParams": {}
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 租户管理


## 租户详情


**接口地址**:`/api/v1/tenants/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|租户不存在||
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新租户


**接口地址**:`/api/v1/tenants/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "ACME 科技有限公司（更新）",
  "tier": "PREMIUM",
  "configJson": "{\"maxUsers\":100}"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||
|updateTenantRequest|UpdateTenantRequest|body|true|UpdateTenantRequest|UpdateTenantRequest|
|&emsp;&emsp;name|租户名称||true|string||
|&emsp;&emsp;tier|套餐等级||false|string||
|&emsp;&emsp;configJson|租户配置 JSON||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除租户（逻辑删除）


**接口地址**:`/api/v1/tenants/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 租户列表


**接口地址**:`/api/v1/tenants`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码（从 0 开始）|query|false|integer(int32)||
|size|每页数量|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"name": "",
			"status": "",
			"tier": "",
			"configJson": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建租户


**接口地址**:`/api/v1/tenants`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "tenantId": 0,
  "name": "ACME 科技有限公司",
  "tier": "STANDARD"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createTenantRequest|CreateTenantRequest|body|true|CreateTenantRequest|CreateTenantRequest|
|&emsp;&emsp;tenantId|租户唯一标识||true|integer(int64)||
|&emsp;&emsp;name|租户名称||true|string||
|&emsp;&emsp;tier|套餐等级,可用值:STANDARD,PREMIUM,ENTERPRISE||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|创建成功|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|租户标识已存在||
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启停租户


**接口地址**:`/api/v1/tenants/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "status": "SUSPENDED"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||
|updateTenantStatusRequest|UpdateTenantStatusRequest|body|true|UpdateTenantStatusRequest|UpdateTenantStatusRequest|
|&emsp;&emsp;status|目标状态,可用值:ACTIVE,SUSPENDED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# 租户管理


## 租户详情


**接口地址**:`/api/v1/tenants/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|租户不存在||
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 更新租户


**接口地址**:`/api/v1/tenants/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "name": "ACME 科技有限公司（更新）",
  "tier": "PREMIUM",
  "configJson": "{\"maxUsers\":100}"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||
|updateTenantRequest|UpdateTenantRequest|body|true|UpdateTenantRequest|UpdateTenantRequest|
|&emsp;&emsp;name|租户名称||true|string||
|&emsp;&emsp;tier|套餐等级||false|string||
|&emsp;&emsp;configJson|租户配置 JSON||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 删除租户（逻辑删除）


**接口地址**:`/api/v1/tenants/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVoid|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 租户列表


**接口地址**:`/api/v1/tenants`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码（从 0 开始）|query|false|integer(int32)||
|size|每页数量|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"tenantId": 0,
			"name": "",
			"status": "",
			"tier": "",
			"configJson": "",
			"createdAt": ""
		}
	],
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 创建租户


**接口地址**:`/api/v1/tenants`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "tenantId": 0,
  "name": "ACME 科技有限公司",
  "tier": "STANDARD"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createTenantRequest|CreateTenantRequest|body|true|CreateTenantRequest|CreateTenantRequest|
|&emsp;&emsp;tenantId|租户唯一标识||true|integer(int64)||
|&emsp;&emsp;name|租户名称||true|string||
|&emsp;&emsp;tier|套餐等级,可用值:STANDARD,PREMIUM,ENTERPRISE||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|创建成功|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|租户标识已存在||
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## 启停租户


**接口地址**:`/api/v1/tenants/{id}/status`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`application/json`


**接口描述**:


**请求示例**:


```javascript
{
  "status": "SUSPENDED"
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|租户主键 ID|path|true|integer(int64)||
|updateTenantStatusRequest|UpdateTenantStatusRequest|body|true|UpdateTenantStatusRequest|UpdateTenantStatusRequest|
|&emsp;&emsp;status|目标状态,可用值:ACTIVE,SUSPENDED||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultTenantResponse|
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||TenantResponse|TenantResponse|
|&emsp;&emsp;id|主键 ID|integer(int64)||
|&emsp;&emsp;tenantId|租户唯一标识|integer(int64)||
|&emsp;&emsp;name|租户名称|string||
|&emsp;&emsp;status|状态|string||
|&emsp;&emsp;tier|套餐等级|string||
|&emsp;&emsp;configJson|配置 JSON|string||
|&emsp;&emsp;createdAt|创建时间|string(date-time)||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"tenantId": 0,
		"name": "",
		"status": "",
		"tier": "",
		"configJson": "",
		"createdAt": ""
	},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# Actuator


## Actuator root web endpoint


**接口地址**:`/actuator`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'prometheus'


**接口地址**:`/actuator/prometheus`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`text/plain;version=0.0.4;charset=utf-8,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'metrics'


**接口地址**:`/actuator/metrics`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'metrics-requiredMetricName'


**接口地址**:`/actuator/metrics/{requiredMetricName}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|requiredMetricName||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'info'


**接口地址**:`/actuator/info`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'health'


**接口地址**:`/actuator/health`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'health-path'


**接口地址**:`/actuator/health/**`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'env'


**接口地址**:`/actuator/env`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


## Actuator web endpoint 'env-toMatch'


**接口地址**:`/actuator/env/{toMatch}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`application/vnd.spring-boot.actuator.v3+json,application/json`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|toMatch||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ResultVoid|
|401|Unauthorized|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|409|Conflict|ResultVoid|
|429|Too Many Requests|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-429**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|timestamp||integer(int64)|integer(int64)|
|success||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"timestamp": 0,
	"success": true
}
```


# Agent 管理
暂无接口文档
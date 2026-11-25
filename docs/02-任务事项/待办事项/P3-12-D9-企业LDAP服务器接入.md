# D9 企业 LDAP 服务器接入实现方案

> **类别**: D | **优先级**: P3  
> **依赖**: C1 代码去掉 Stub

## 1. 场景

对接公司 AD，网络、证书、账号映射。

## 2. 设计要点

这是 **环境与账号运营**，不是再写一套 Provider。清单：

- VPN/专线到 DC  
- `ldaps://` 与信任证书  
- 服务账号只读搜人  
- 开通流程：HR 开 AD → 平台管理员开 User 行 → 员工 LDAP 登录  
- 测试用 docker `osixia/openldap` 仅 dev

## 3. 配置项

`spring.ldap.urls/base/username/password/user-dn-pattern` 走环境变量。

## 4. 验收

预发绑定真实测试 OU 成功；错误密码失败。

## 5. 风险

同步离职：定时 disable 本地 User，本方案二期。

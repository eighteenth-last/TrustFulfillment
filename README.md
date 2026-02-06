# 臻托 TrustFulfillment

<p align="center">
  <img src="fronted_admin/public/logo.png" alt="臻托 Logo" width="120" height="120">
</p>

<p align="center">
  <strong>让每一笔交易都值得托付</strong>
</p>

<p align="center">
  一站式软件外包信托担保平台，为甲方（雇主）和乙方（服务商）提供安全、透明的交易保障服务
</p>

---

## 项目简介

**臻托 TrustFulfillment** 是一个专为软件外包交易场景设计的信托担保平台。平台采用"资金托管 + 分阶段验收"的模式，有效解决软件外包交易中常见的信任问题，保护双方权益。

### 核心特性

- **资金托管**：用户发布任务时，资金托管至平台，确保服务商工作有保障
- **分阶段交付**：支持多阶段里程碑式交付，按阶段托管、按阶段验收
- **智能结算**：全部阶段验收完成后，平台一次性将托管资金结算给服务商
- **平台提成**：支持按商家配置的提成比例自动扣除平台服务费
- **在线支付**：集成支付宝/微信支付，支持账户充值、虚拟支付测试
- **电子合同**：支持在线起草、双方电子签名、合同生效的完整合同流程
- **实时沟通**：基于 WebSocket 的订单内实时聊天，买卖双方高效沟通
- **存证记录**：关键操作自动存证，合同签署、资金托管、交付验收全程可追溯
- **纠纷仲裁**：提供完整的申诉和仲裁机制，保障双方权益
- **风险监控**：大额交易预警、纠纷风险检测、异常行为监控
- **多端支持**：Web端、小程序端、管理后台全覆盖

## 技术栈

### 后端 (backend)

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.5.0 | 应用框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| Sa-Token | 1.39.0 | 权限认证框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存与消息订阅 |
| WebSocket | - | 实时通信 |
| MinIO | 8.5.7 | 对象存储服务 |
| Hutool | 5.8.25 | Java 工具类库 |

### 前端 - Web 用户端 (fronted_web)

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.5.x | 渐进式 JavaScript 框架 |
| Vite | 7.x | 下一代前端构建工具 |
| Naive UI | 2.43.x | Vue 3 组件库 |
| Tailwind CSS | 4.x | 原子化 CSS 框架 |
| Pinia | 3.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.13.x | HTTP 客户端 |

### 前端 - 管理后台 (fronted_admin)  

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.5.x | 渐进式 JavaScript 框架 |
| Vite | 7.x | 下一代前端构建工具 |
| Naive UI | 2.43.x | Vue 3 组件库 |
| Tailwind CSS | 4.x | 原子化 CSS 框架 |

### 前端 - 小程序端 (fronted_uniapp)

| 技术 | 版本 | 说明 |
|------|------|------|
| uni-app | 3.x | 跨平台开发框架 |
| Vue.js | 3.x | 渐进式 JavaScript 框架 |
| Vite | 4.x | 构建工具 |

## 项目结构

```
TrustFulfillment/
├── backend/                    # 后端服务 (Spring Boot)
│   ├── src/main/java/com/trustfulfillment/
│   │   ├── config/            # 配置类 (CORS, Redis, WebSocket, MinIO等)
│   │   ├── controller/        # 控制器层 (12个控制器)
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类 (20+个实体)
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── service/           # 服务层 (含 CommissionService, RiskService 等)
│   │   ├── websocket/         # WebSocket 处理器
│   │   └── common/            # 公共类
│   └── src/main/resources/    # 配置文件
│
├── fronted_web/               # Web 用户端 (Vue 3)
│   ├── src/
│   │   ├── api/               # API 接口 (10个模块)
│   │   ├── components/        # 公共组件
│   │   │   ├── ChatDrawer.vue     # 聊天抽屉组件
│   │   │   ├── EvidenceDrawer.vue # 存证抽屉组件
│   │   │   └── SignaturePad.vue   # 电子签名组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面视图
│   │       ├── user/          # 用户端页面
│   │       └── merchant/      # 商家端页面
│   └── public/                # 静态资源
│
├── fronted_admin/             # 管理后台 (Vue 3)
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   └── views/             # 页面视图
│   │       ├── Dashboard.vue      # 平台总览 (ECharts)
│   │       ├── Orders.vue         # 任务审批
│   │       ├── Funds.vue          # 信托资金
│   │       ├── Disputes.vue       # 纠纷调处
│   │       ├── Merchants.vue      # 商家审核
│   │       ├── Users.vue          # 用户管理
│   │       ├── Risk.vue           # 风险监控
│   │       ├── CustomerService.vue # 客户解答
│   │       ├── Commissions.vue    # 提成管理
│   │       ├── Contracts.vue      # 合同管理
│   │       ├── Banks.vue          # 银行信息
│   │       ├── Notifications.vue  # 系统通知
│   │       ├── Categories.vue     # 业务分类
│   │       └── Settings.vue       # 系统设置
│   └── public/                # 静态资源
│
├── fronted_uniapp/            # 小程序端 (uni-app)
│   ├── pages/                 # 页面
│   ├── components/            # 组件
│   ├── stores/                # 状态管理
│   ├── utils/                 # 工具函数
│   └── static/                # 静态资源
│
├── database/                  # 数据库脚本
│   └── trust_fulfillment.sql  # 完整数据库脚本
│
└── html/                      # 原型设计稿
```

## 功能模块

### 用户端功能

| 模块 | 功能 |
|------|------|
| 登录注册 | 手机号密码登录、手机验证码登录、微信一键登录 |
| 任务发布 | 发布任务需求、设置阶段付款计划、上传开发文档 |
| 订单管理 | 查看订单详情、阶段验收、申请退款 |
| 电子合同 | 查看合同详情、在线签署、合同生效 |
| 实时沟通 | 与商家实时聊天、消息已读状态、历史记录 |
| 存证记录 | 查看订单全流程存证、合同/资金/交付存证 |
| 信托钱包 | 余额管理、在线充值（支付宝/微信/虚拟支付）、提现、交易记录、银行卡管理 |
| 纠纷处理 | 申请仲裁、提交证据、查看处理进度 |
| 系统通知 | 订单通知、消息提醒、一键已读 |
| 个人中心 | 用户信息编辑、头像上传、商家申请（资质材料上传） |

### 商家端功能

| 模块 | 功能 |
|------|------|
| 任务市场 | 浏览可接任务、筛选任务、查看任务详情 |
| 接单管理 | 接受任务、提交阶段交付物、查看验收结果 |
| 合同管理 | 起草合同、编辑条款、提交合同、在线签署 |
| 实时沟通 | 与用户实时聊天、消息已读状态、历史记录 |
| 存证记录 | 查看订单全流程存证 |
| 财务结算 | 查看收入、提现申请、交易记录 |
| 纠纷处理 | 响应申诉、提交证据、查看裁决结果 |
| 系统通知 | 订单通知、消息提醒、一键已读 |

### 管理后台功能

| 模块 | 功能 |
|------|------|
| 平台总览 | 数据统计、ECharts 图表（订单分布、用户增长、商家排名、交易额分布、平台收入趋势） |
| 任务审批 | 任务审核、订单管理、订单详情 |
| 信托资金 | 资金流水、托管余额、结算管理 |
| 纠纷调处 | 纠纷列表、详情查看、人工介入调解、裁决执行（退款/结算/部分退款） |
| 商家审核 | 商家入驻审核、资质审核、提成比例设置 |
| 用户管理 | 用户列表、用户详情、状态管理、信用管理 |
| 风险监控 | 实时风险告警、风控规则配置、风险趋势分析、风险事件处理 |
| 客户解答 | 客服会话列表、实时聊天、主动联系用户、会话管理 |
| 提成管理 | 提成记录、提成统计、趋势图表、商家提成明细 |
| 合同管理 | 合同列表、合同详情、合同状态管理 |
| 银行信息 | 银行列表、银行信息维护 |
| 系统通知 | 通知列表、发送通知（支持用户搜索和多选）、通知管理 |
| 业务分类 | 分类列表、分类维护 |
| 系统设置 | 平台配置、费率设置、风控阈值配置 |

## 支付系统

平台集成了完整的在线支付功能，支持多种支付方式。

### 支付方式

| 支付方式 | 说明 | 状态 |
|----------|------|------|
| 虚拟支付 | 测试环境使用，3秒后自动完成支付 | ✅ 已实现 |
| 支付宝 | 扫码支付，生成二维码 | ✅ 已集成 |
| 微信支付 | 扫码支付，生成二维码 | ✅ 已集成 |

### 支付流程

```
用户发起充值 → 选择支付方式 → 创建支付订单
    ↓
[虚拟支付] → 3秒后自动完成 → 余额到账
[真实支付] → 显示二维码 → 用户扫码支付 → 支付回调 → 余额到账
```

### 支付配置

管理员可在后台配置支付方式的启用状态和参数：

**支付宝配置**：
- AppID
- 应用私钥
- 支付宝公钥
- 回调地址
- 返回地址

**微信支付配置**：
- 商户号
- AppID
- APIv3密钥
- 商户私钥
- 证书序列号
- 回调地址

### 支付状态轮询

小程序端支持实时轮询支付状态，支付完成后自动跳转：
- 轮询间隔：1秒
- 最大轮询次数：60次（60秒）
- 支付成功后自动返回钱包页面

## 平台提成系统

平台采用按交易金额收取服务费的模式，支持为每个商家设置独立的提成比例。

### 提成计算规则

```
平台提成 = 阶段金额 × 商家提成比例
商家实收 = 阶段金额 - 平台提成
```

### 提成扣除时机

| 阶段类型 | 扣除时机 | 说明 |
|----------|----------|------|
| 首付款 | 合同签署后自动释放时 | 合同生效后自动释放首付款并扣除提成 |
| 尾款 | 用户验收通过时 | 验收交付物后释放尾款并扣除提成 |
| 质保款 | 质保期结束后 | 质保期满自动释放质保款并扣除提成 |

### 提成比例配置

- 默认提成比例：通过系统配置 `default_commission_rate` 设置
- 商家独立比例：可在商家管理中为每个商家设置独立的提成比例
- 优先级：商家独立比例 > 系统默认比例

## 风险监控系统

平台内置完整的风控系统，实时监控交易风险，保障平台安全。

### 风险检测类型

| 类型 | 说明 | 阈值 |
|------|------|------|
| 大额交易预警 | 订单金额超过阈值时触发 | 默认 ≥10万 |
| 大额纠纷订单 | 涉及大额资金的纠纷 | 默认 ≥10万 |
| 频繁操作预警 | 短时间内频繁敏感操作 | 可配置 |
| 异常登录检测 | 异地登录等异常行为 | 自动检测 |

### 风险等级

- **高风险 (high)**：需要立即处理，可能涉及资金安全
- **中风险 (medium)**：需要关注，建议人工审核
- **低风险 (low)**：记录观察，暂不需要干预

### 风控规则配置

管理员可在后台配置以下风控规则的开关：
- 大额交易监控
- 欺诈行为检测
- 异常登录检测
- 大额交易审核
- 高频操作限制
- IP地址风控

## 订单生命周期

```
发布任务 → 待接单(status=0) → 商家接单 → 待签合同(status=8)
    ↓
商家起草合同 → 双方签署 → 待托管(status=1) → 用户托管资金
    ↓
进行中(status=2) → 商家提交交付 → 待验收(status=3)
    ↓
用户验收通过 → [还有下一阶段?]
    ├─ 是 → 继续下一阶段 → 进行中(status=2)
    └─ 否 → [有质保款?]
        ├─ 是 → 等待质保期 → 质保期满自动结算 → 已完成(status=4)
        └─ 否 → 直接结算 → 已完成(status=4)

注：每次资金释放时自动扣除平台提成
```

### 订单状态说明

| 状态码 | 状态名 | 说明 |
|--------|--------|------|
| 0 | 待接单 | 任务已发布，等待商家接单 |
| 1 | 待托管 | 合同已签署，等待用户托管资金 |
| 2 | 进行中 | 商家正在执行任务 |
| 3 | 待验收 | 商家已提交交付物，等待用户验收 |
| 4 | 已完成 | 全部阶段验收完成，已结算 |
| 5 | 已取消 | 订单已取消 |
| 6 | 纠纷中 | 存在争议，进入仲裁流程 |
| 7 | 待支付下阶段 | 当前阶段完成，等待支付下一阶段款项 |
| 8 | 待签合同 | 商家已接单，等待双方签署合同 |

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+ (用于缓存和WebSocket消息订阅)
- MinIO (可选，用于文件存储)

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE trust_fulfillment CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;"

# 导入初始化脚本
mysql -u root -p trust_fulfillment < database/trust_fulfillment.sql
```

### 2. 后端启动

```bash
cd backend

# 修改 application.yml 中的数据库配置
# 然后启动服务
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/tf-backend-1.0.0.jar
```

后端服务默认运行在 `http://localhost:8080`

### 3. Web 用户端启动

```bash
cd fronted_web

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产构建
npm run build
```

Web 用户端默认运行在 `http://localhost:5173`

### 4. 管理后台启动

```bash
cd fronted_admin

# 安装依赖
npm install

# 开发模式启动
npm run dev
```

管理后台默认运行在 `http://localhost:3001`

### 5. 小程序端启动

```bash
cd fronted_uniapp

# 安装依赖
npm install

# 微信小程序开发
npm run dev:mp-weixin

# H5 开发
npm run dev:h5
```

## 配置说明

### 后端配置 (application.yml)

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/trust_fulfillment
    username: root
    password: your_password

# Redis 配置 (用于缓存和WebSocket消息订阅)
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:        # 无密码留空
      database: 0

# 短信服务配置 (开发环境可开启模拟模式)
sms:
  mock-mode: true  # 开发环境设为 true

# 微信小程序配置
wechat:
  mp:
    mock-mode: true  # 开发环境设为 true
    appid: your_appid
    secret: your_secret

# MinIO 配置
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: trustfulfillment
```

### 测试账号

| 角色 | 手机号 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | 13272796154 | 123456 | 管理后台登录，拥有所有权限 |
| 用户 | 13800000001 | 123456 | 甲方(雇主)，可发布任务、托管资金 |
| 商家 | 13800000002 | 123456 | 乙方(服务商)，可接单、提交交付 |

> **提示**：开发环境下验证码固定为 `123456` 或查看后端控制台日志

## API 文档

### 认证接口

```
POST /api/auth/login          # 手机号密码登录
POST /api/auth/login/phone    # 手机验证码登录
POST /api/auth/login/wechat   # 微信登录
POST /api/auth/send-code      # 发送验证码
POST /api/auth/register       # 注册
POST /api/auth/logout         # 登出
```

### 订单接口

```
POST   /api/order             # 创建订单
GET    /api/order/list        # 订单列表
GET    /api/order/{id}        # 订单详情
POST   /api/order/{id}/deposit        # 托管资金
POST   /api/order/{id}/pay-next-stage # 支付下一阶段
POST   /api/order/{id}/accept         # 接单
POST   /api/order/{id}/cancel         # 取消订单
GET    /api/order/stats               # 订单统计
```

### 交付接口

```
POST /api/order/{orderId}/stage/{stageId}/start    # 开始阶段
POST /api/order/{orderId}/stage/{stageId}/deliver  # 提交交付
POST /api/order/{orderId}/stage/{stageId}/accept   # 验收通过
POST /api/order/{orderId}/stage/{stageId}/reject   # 验收拒绝
```

### 合同接口

```
POST /api/contract/create         # 创建/更新合同 (商家)
POST /api/contract/{id}/submit    # 提交合同 (商家)
POST /api/contract/{id}/sign      # 签署合同 (双方)
GET  /api/contract/{id}           # 合同详情
GET  /api/contract/order/{orderId} # 根据订单获取合同
```

### 消息接口

```
GET  /api/message/history/{orderId}  # 获取订单消息历史
POST /api/message/read/{orderId}     # 标记消息已读
GET  /api/message/unread/{orderId}   # 获取未读消息数
POST /api/message/send               # 发送消息 (HTTP备用)
```

### WebSocket 聊天接口

```
连接地址: ws://localhost:8080/ws/chat?token={token}

// 发送消息
{ "action": "send", "orderId": 1, "content": "消息内容", "type": 1 }

// 订阅订单消息
{ "action": "subscribe", "orderId": 1 }

// 标记已读
{ "action": "read", "orderId": 1 }
```

### 存证接口

```
GET /api/evidence/order/{orderId}   # 获取订单存证记录
GET /api/evidence/{id}              # 获取存证详情
GET /api/evidence/types             # 获取存证类型列表
```

### 通知接口

```
GET    /api/notifications            # 获取通知列表
GET    /api/notifications/unread-count # 获取未读数量
PUT    /api/notifications/{id}/read  # 标记单条已读
PUT    /api/notifications/read-all   # 标记全部已读
DELETE /api/notifications/{id}       # 删除通知
```

### 钱包接口

```
GET  /api/wallet/info         # 钱包信息（余额、冻结金额等）
GET  /api/wallet/transactions # 交易记录
POST /api/wallet/withdraw     # 提现申请
```

### 支付接口

```
POST /api/pay/recharge        # 创建充值订单（支持支付宝/微信/虚拟支付）
GET  /api/pay/status/{orderNo} # 查询支付状态（支持轮询）
GET  /api/pay/query/status    # 查询支付状态（旧接口）
POST /api/pay/notify/alipay   # 支付宝支付回调
POST /api/pay/notify/wechat   # 微信支付回调
```

### 银行接口

```
GET /api/banks/options        # 获取银行列表 (下拉选项)
GET /api/banks                # 获取所有银行信息
```

### 商家申请接口

```
GET  /api/merchant/status     # 获取商家状态（是否已是商家、是否有待审核申请）
POST /api/merchant/apply      # 提交商家申请（含资质材料）
GET  /api/merchant/apply/history # 申请历史记录
GET  /api/merchant/type-config   # 获取商户类型配置（提成比例等）
```

### 文件上传接口

```
POST /api/file/upload         # 上传单个文件（支持图片/文档/压缩包）
POST /api/file/upload/batch   # 批量上传文件
DELETE /api/file/delete       # 删除文件
```

### 纠纷接口

```
POST /api/dispute/create      # 发起申诉
GET  /api/dispute/list        # 申诉列表
GET  /api/dispute/order/{orderId} # 根据订单获取申诉详情
POST /api/dispute/{id}/message # 发送消息
POST /api/dispute/{id}/cancel  # 撤销申诉
```

### 客服接口

```
POST /api/service/session/create  # 创建客服会话
GET  /api/service/sessions        # 获取会话列表
GET  /api/service/messages/{sessionId} # 获取会话消息
POST /api/service/message/send    # 发送客服消息
```

### 管理后台接口

```
# 平台总览
GET  /admin/dashboard/stats       # 获取平台统计数据

# 风险监控
GET  /admin/risk/stats            # 风险统计
GET  /admin/risk/alerts           # 实时风险告警
GET  /admin/risk/trend            # 风险趋势数据
GET  /admin/risk/rules            # 获取风控规则配置
POST /admin/risk/rules            # 保存风控规则配置
GET  /admin/risk/events           # 风险事件列表
POST /admin/risk/events/{id}/process # 处理风险事件

# 提成管理
GET  /admin/commissions           # 提成记录列表
GET  /admin/commissions/stats     # 提成统计
GET  /admin/commissions/{id}      # 提成详情
PUT  /admin/merchants/{id}/commission-rate # 更新商家提成比例

# 纠纷调处
GET  /admin/disputes              # 纠纷列表
GET  /admin/disputes/stats        # 纠纷统计
GET  /admin/disputes/{id}         # 纠纷详情
POST /admin/disputes/{id}/resolve # 裁决纠纷

# 客服管理
GET  /admin/service/conversations # 客服会话列表
GET  /admin/service/messages/{sessionId} # 会话消息
POST /admin/service/message/send  # 发送客服消息
POST /admin/service/session/create # 主动创建会话

# 系统配置
GET  /admin/settings              # 获取系统配置
POST /admin/settings              # 保存系统配置
```

## 数据库表结构

### 核心业务表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表（含角色、余额、冻结金额、头像等） |
| `sys_user_oauth` | 第三方登录绑定表 |
| `tf_order` | 订单表 |
| `tf_order_stage` | 订单阶段表（首付款/尾款/质保款） |
| `tf_order_message` | 订单消息表 (实时聊天) |
| `tf_contract` | 电子合同表 |
| `tf_evidence` | 存证记录表 |
| `tf_transaction` | 交易记录表（充值/提现/托管/结算等） |
| `tf_bank_card` | 用户银行卡表 |
| `tf_dispute` | 纠纷表 |
| `tf_dispute_message` | 纠纷消息表 |
| `tf_commission` | 平台提成记录表 |
| `tf_risk_event` | 风险事件记录表 |
| `tf_merchant` | 商家扩展信息表（含提成比例等） |
| `tf_merchant_apply` | 商家申请表（含资质材料） |

### 系统配置表

| 表名 | 说明 |
|------|------|
| `sys_config` | 系统配置表（平台费率、风控阈值等） |
| `sys_bank` | 银行信息表 |
| `sys_notification` | 系统通知表 |
| `sys_category` | 业务分类表 |
| `sys_sms_code` | 验证码表 |
| `sys_sms_log` | 短信日志表 |
| `sys_login_log` | 登录日志表 |

### 客服系统表

| 表名 | 说明 |
|------|------|
| `tf_service_session` | 客服会话表 |
| `tf_service_message` | 客服消息表 |

## 部署指南

### Docker 部署 (推荐)

```bash
# 启动 Redis
docker run -d --name redis -p 6379:6379 redis:latest

# 启动 MySQL
docker run -d --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=trust_fulfillment \
  mysql:8.0

# 启动 MinIO (可选)
docker run -d --name minio -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  minio/minio server /data --console-address ":9001"

# 后端
docker build -t tf-backend ./backend
docker run -d -p 8080:8080 tf-backend

# 前端 (Nginx)
docker build -t tf-web ./fronted_web
docker run -d -p 80:80 tf-web
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/fronted_web/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # WebSocket 代理
    location /ws {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 3600s;
    }
}
```

## 开发规范

### Git 提交规范

```
feat: 新功能
fix: 修复 Bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建/工具相关
```

### 代码风格

- 后端：遵循阿里巴巴 Java 开发规范
- 前端：遵循 Vue 3 官方风格指南

## 常见问题

### Q: 验证码收不到？

开发环境下，`sms.mock-mode` 设置为 `true`，验证码会打印在后端控制台日志中。

### Q: 微信登录失败？

1. 确认已在微信开放平台配置小程序
2. 确认 `application.yml` 中的 `appid` 和 `secret` 配置正确
3. 开发环境可将 `wechat.mp.mock-mode` 设为 `true`

### Q: 文件上传失败？

确认 MinIO 服务已启动，且配置正确。如不使用 MinIO，可修改 `FileServiceImpl` 使用本地存储。

### Q: 小程序上传提示"未登录"？

1. 确认已登录且 token 有效
2. 检查 `request.js` 中的 header 配置是否为 `Authorization`
3. 确认后端 `application.yml` 中 `sa-token.token-name` 配置为 `Authorization`

### Q: 支付功能如何测试？

1. 开发环境使用"虚拟支付"，3秒后自动完成
2. 真实支付需要在管理后台配置支付宝/微信支付参数
3. 支付宝/微信支付需要配置回调地址，确保外网可访问

### Q: Redis 连接失败？

1. 确认 Redis 服务已启动：`redis-cli ping` 应返回 `PONG`
2. 检查 `application.yml` 中的 Redis 配置是否正确
3. 如有密码，请在配置中填写 `password` 字段

### Q: WebSocket 聊天连接失败？

1. 确认 Redis 服务正常运行（WebSocket 消息订阅依赖 Redis）
2. 检查连接 URL 是否正确：`ws://localhost:8080/ws/chat?token={token}`
3. 确认 token 有效且未过期

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 更新日志

### v1.3.0 (2026-02-06)
- 新增：小程序端完整支付系统（支持支付宝/微信/虚拟支付）
- 新增：小程序端用户信息编辑功能（头像上传到MinIO）
- 新增：小程序端商家申请功能（资质材料上传）
- 新增：支付状态实时轮询和二维码展示
- 优化：任务发布流程页面布局统一
- 优化：数据即时刷新，无需手动刷新
- 修复：Token认证问题，统一使用Authorization header

### v1.2.0 (2026-02-03)
- 新增：平台提成系统，支持按商家配置提成比例
- 新增：风险监控系统，大额交易/纠纷预警
- 新增：管理后台 ECharts 数据可视化
- 新增：客服主动联系用户功能
- 优化：纠纷详情页面展示
- 优化：通知系统支持用户搜索和多选

### v1.1.0 (2026-01-15)
- 新增：电子合同签署流程
- 新增：实时聊天 WebSocket 支持
- 新增：存证记录全流程追溯
- 优化：订单状态机完善

### v1.0.0 (2026-01-01)
- 初始版本发布
- 基础订单流程
- 资金托管功能

## 联系方式
- 作者：程序员Eighteen
- 邮箱：eighteenthstuai@gmail.com

---

<p align="center">
  <sub>Built with ❤️ by TrustFulfillment Team</sub>
</p>
<p align="center">
  <sub>Last updated: 2026-02-06</sub>
</p>

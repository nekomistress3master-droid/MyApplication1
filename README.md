# 个人日记账APP

## 📱 项目简介
一款基于 Android 开发的个人记账应用，帮助用户轻松管理日常收支，养成良好理财习惯。

## ✨ 功能特点

### 1. 用户管理
- 用户注册/登录
- 记住密码功能
- 安全退出

### 2. 账目管理
- **收入记录**：工资、奖金、兼职、利息等
- **支出记录**：餐饮、购物、交通、娱乐、教育等
- **自定义类别**：可自由添加/编辑/删除账目类别

### 3. 数据统计
- 月度收支总览
- 收支明细列表
- 按类别筛选查看

### 4. 数据存储
- 使用 Room 数据库本地存储
- 数据安全可靠，无需联网

## 🛠 技术栈

| 技术 | 说明 |
|------|------|
| 开发语言 | Kotlin |
| UI 框架 | Jetpack Compose |
| 数据库 | Room |
| 架构模式 | MVVM |
| 依赖注入 | 手动管理 |
| 最低 SDK | Android 7.0 (API 24) |

## 📸 界面截图

<img width="300" alt="Screenshot" src="https://github.com/user-attachments/assets/1b9100ac-1933-4d5a-b859-236914cc0075" />
<img width="300" alt="添加记录界面截图" src="https://github.com/user-attachments/assets/cb299a6e-e34a-4ab1-9070-ebfd66527a9b" />

## 📦 安装方式

### 方式一：直接安装 APK
1. 下载 Releases 中的 APK 文件
2. 在手机设置中允许"安装未知来源应用"
3. 点击 APK 文件进行安装

### 方式二：源码运行
1. 克隆本仓库
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接手机或启动模拟器
5. 点击 Run 按钮运行

## 🗂 项目结构
app/
├── src/main/java/com/example/myapplication/
│ ├── ui/ # UI 界面
│ │ ├── LoginScreen.kt # 登录界面
│ │ ├── RegisterScreen.kt # 注册界面
│ │ ├── HomeScreen.kt # 主页
│ │ └── AddRecordScreen.kt # 记账界面
│ ├── data/ # 数据层
│ │ ├── database/ # Room 数据库
│ │ └── repository/ # 数据仓库
│ └── MainActivity.kt # 主活动
└── src/main/res/ # 资源文件

## 📋 数据库设计

| 表名 | 字段 | 说明 |
|------|------|------|
| User | id, username, password | 用户表 |
| Category | id, name, type, userId | 类别表 |
| Record | id, amount, categoryId, date, note, userId | 收支记录表 |

## 👨‍💻 作者

| 项目 | 信息 |
|------|------|
| 姓名 | 秋 |
| 学校 | 成都文理学院|

## 📄 要求完成情况

- ✅ 用户登录验证和免密码登录
- ✅ 账目类别管理
- ✅ 收支账目管理
- ✅ 包含3张以上核心功能表
- ✅ 界面美观，良好交互
- ✅ Activity 生命周期和组件间通信
- ✅ 轻量化存储（SharedPreferences/Room）

## 📞 联系方式

如有问题请联系：3014682346@qq.com

---

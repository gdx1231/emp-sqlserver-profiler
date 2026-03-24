# emp-sqlserver-profiler 代码说明文档

## 项目概述

`emp-sqlserver-profiler` 是一个 Java 实现的 SQL Server 性能分析工具，它能够监控 SQL Server 数据库的活动，收集查询执行信息，并将这些信息存储到本地 HSQLDB 数据库中。

## 核心架构

### 1. 主要组件

#### ProfilerControl.java
- **作用**: 应用程序主入口点，处理命令行参数和控制台交互
- **关键功能**:
  - 解析命令行参数 (-h, -u, -p, -d, -w, -P)
  - 提供交互式模式和参数化模式
  - 处理控制命令 (start, pause, resume, stop, etc.)
  - 管理应用程序生命周期

#### SqlServerProfiler.java
- **作用**: 核心分析器类，负责与 SQL Server 的交互
- **关键功能**:
  - 管理与 SQL Server 的连接
  - 创建和管理跟踪会话
  - 控制跟踪的开始、暂停、恢复和停止
  - 处理跟踪数据的接收和存储
  - 状态管理 (psStopped, psPaused, psProfiling)

#### RawTraceReader.java
- **作用**: SQL Server 跟踪读取器，负责实际的数据读取
- **关键功能**:
  - 创建 SQL Server 跟踪
  - 启动、暂停和停止跟踪
  - 读取跟踪数据
  - 管理跟踪的生命周期

#### HSqlDbServer.java
- **作用**: 本地 HSQLDB 服务器管理器
- **关键功能**:
  - 初始化 HSQLDB 数据库
  - 管理本地存储路径
  - 提供数据持久化服务

#### ProfilerThread.java
- **作用**: 负责持续读取跟踪数据的后台线程
- **关键功能**:
  - 持续从 SQL Server 读取跟踪数据
  - 将数据传递给存储层

### 2. 数据模型

#### ProfilerEvent.java
- **作用**: 表示单个跟踪事件的数据模型
- **包含字段**:
  - EventClass: 事件类型
  - TextData: SQL 文本
  - StartTime/EndTime: 事件开始/结束时间
  - Duration: 持续时间
  - CPU: CPU 使用情况
  - Reads/Writes: 读写操作数
  - SPID: 会话进程 ID
  - DatabaseName: 数据库名称
  - ApplicationName: 应用程序名称
  - LoginName: 登录用户名
  - 以及其他多种跟踪属性

#### ProfilingStateEnum.java
- **作用**: 定义分析器的状态枚举
- **状态值**:
  - psStopped: 已停止
  - psPaused: 已暂停
  - psProfiling: 正在分析

### 3. 数据库设计

#### 主要表结构
- `TRACE_SERVER`: 存储 SQL Server 连接配置信息
- `TRACE_LOG`: 存储详细的跟踪日志记录
- `TRACE_EVENTS`: 跟踪事件定义
- `TRACE_COLUMNS`: 跟踪列定义
- `TRACE_CATEGORIES`: 跟踪类别定义

#### TRACE_LOG 表字段说明
- `TL_ID`: 跟踪记录唯一标识符
- `TS_ID`: 服务器配置标识符
- `TL_TEXTDATA`: SQL 文本数据
- `TL_DURATION`: 执行持续时间
- `TL_STARTTIME/TL_ENDTIME`: 开始/结束时间
- `TL_READS/TL_WRITES`: 读/写操作数
- `TL_CPU`: CPU 使用量
- `TL_SPID`: 会话进程 ID
- `TL_DATABASENAME`: 数据库名称
- `TL_APPLICATIONNAME`: 应用程序名称
- `TL_LOGINNAME`: 登录用户名
- 以及其他多个跟踪相关的字段

### 4. 关键流程

#### 启动流程
1. 解析命令行参数或进入交互模式
2. 测试与 SQL Server 的连接
3. 初始化 SqlServerProfiler 实例
4. 启动 HSQLDB 服务器
5. 显示帮助信息并等待用户命令

#### 跟踪流程
1. 调用 `startProfiling()` 方法
2. 创建 RawTraceReader 实例
3. 启动 ProfilerThread 线程
4. 线程持续读取跟踪数据
5. 将数据存储到 HSQLDB 中

#### 命令处理流程
1. 用户输入命令 (start, pause, resume, stop, etc.)
2. ProfilerControl.control() 方法处理命令
3. 根据命令类型调用相应的 SqlServerProfiler 方法
4. 返回结果给用户

### 5. 配置管理

#### ewa_conf_console.xml
- 应用程序配置文件
- 包含雪花算法配置
- 安全配置 (AES 加密)
- 初始化参数配置
- 数据库连接池配置

#### HSQLDB 初始化
- 从资源文件复制初始数据库脚本
- 自动创建必要的表结构
- 设置数据库连接参数

### 6. 错误处理和日志

- 使用 SLF4J 进行日志记录
- 异常处理机制确保应用稳定性
- 连接测试验证数据库连通性
- 状态检查防止无效操作

### 7. 特殊功能

#### 数据截断优化
- 避免使用 BLOB 和 CLOB 类型导致的大文件问题
- 对长文本进行截断处理 (16KB 限制)

#### AES 加密
- 敏感信息 (如密码) 使用 AES 加密存储

#### 并发安全
- 使用 ConcurrentLinkedQueue 存储事件队列
- 使用 ConcurrentHashMap 管理多实例

## 使用示例

### 代码集成示例
```java
// 创建分析器实例
SqlServerProfiler profiler = SqlServerProfiler.getInstance("localhost", 1433, "master", "sa", "password");

// 开始跟踪
profiler.startProfiling();

// 暂停跟踪
profiler.pauseProfiling();

// 恢复跟踪
profiler.resumeProfiling();

// 停止跟踪
profiler.stopProfiling();

// 导出记录
String exportFile = profiler.exportRecords();
```

## 扩展性考虑

1. **多实例支持**: 通过哈希连接参数支持多个分析器实例
2. **插件架构**: 可以扩展事件处理逻辑
3. **配置灵活**: 支持不同的数据库过滤选项
4. **存储可替换**: HSQLDB 可以替换为其他数据库

## 注意事项

1. 需要 SQL Server 的适当权限来创建和管理跟踪
2. 跟踪可能对数据库性能产生影响，请谨慎使用
3. 确保有足够的磁盘空间存储跟踪数据
4. 定期清理旧的跟踪记录以避免存储空间耗尽
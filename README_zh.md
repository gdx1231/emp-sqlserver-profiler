# emp-sqlserver-profiler

一个基于 Java 的 SQL Server Profiler 工具，用于监控和分析 SQL Server 数据库活动，参考 .NET 项目 [ExpressProfiler](https://github.com/ststeiger/ExpressProfiler)。

## 目录
- [概述](#概述)
- [功能特性](#功能特性)
- [安装](#安装)
- [使用方法](#使用方法)
- [参数](#参数)
- [控制命令](#控制命令)
- [架构](#架构)
- [项目目标](#项目目标)
- [运行原理](#运行原理)
- [Maven 依赖](#maven-依赖)

## 概述

`emp-sqlserver-profiler` 是一个轻量级、跨平台的 SQL Server 性能分析工具，旨在监控数据库活动、捕获查询执行详细信息并分析性能指标。它利用 SQL Server 的内置跟踪功能来收集有关数据库操作的详细信息。

## 功能特性

- 实时监控 SQL Server 活动
- 性能分析，包括 CPU 使用率、读写次数和执行时间等指标
- 诊断慢查询和资源密集型操作
- 在本地 HSQLDB 中持久化数据以进行离线分析
- 通过 Java 实现的跨平台兼容性
- 简单的命令行界面，便于轻松部署
- 支持多个并发分析器实例
- 可配置的事件过滤和数据收集

## 安装

1. 克隆仓库：
```bash
git clone https://github.com/gdx1231/emp-sqlserver-profiler.git
```

2. 构建项目：
```bash
mvn clean package
```

3. 构建的 JAR 文件将位于 `target/` 目录中。

## 使用方法

### Linux/Mac:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar:target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

### Windows:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar;target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

### 交互模式:
不带参数运行以进入交互模式：
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar:target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl
```

## 参数

    -h SQL Server 主机或 IP 地址 (默认 localhost)
    -u 用户名 (默认 sa)
    -p 密码* (必须输入)
    -d 过滤数据库 (默认为空)
    -w HSQLDB 路径 (默认为 /home/admin/com.gdxsoft.sqlProfiler.hsqldb_server)
    -P 端口号 (默认 1433)

## 控制命令

    start: 启动分析器。
    pause: 暂停分析器。
    resume: 恢复分析器。
    stop: 停止分析器。
    status: 获取分析器状态。
    state: 获取分析器状态。
    clear: 清除所有 SQL Server 跟踪。
    export: 导出所有跟踪记录，保存在 HSQLDB 工作路径中。
    truncate: 截断 HSQLDB 本地记录(TRACE_LOG)。
    quit: 停止并退出。
    help: 显示此帮助。

## 架构

### 核心组件

#### ProfilerControl
处理命令行参数和控制台交互的主入口点。

#### SqlServerProfiler
核心分析器类，负责管理与 SQL Server 的连接并控制跟踪会话。

#### RawTraceReader
直接从 SQL Server 读取跟踪数据的组件。

#### HSqlDbServer
用于数据持久化的本地 HSQLDB 服务器管理器。

#### ProfilerThread
连续读取和处理跟踪数据的后台线程。

### 数据流
```
SQL Server → RawTraceReader → ProfilerEvent → ProfilerThread → HSqlDbServer → TRACE_LOG
```

## 项目目标

### 主要目标
- **实时监控**: 实时捕获 SQL Server 数据库活动
- **性能分析**: 分析 SQL 查询性能指标，如 CPU 使用率、读写次数、执行时间
- **问题诊断**: 识别慢查询、资源消耗大的操作和潜在的性能瓶颈
- **数据收集**: 收集详细的数据库操作日志，用于后续分析和审计
- **跨平台兼容**: 提供支持多种操作系统的 Java 实现

### 设计目标
- **易用性**: 提供简单的命令行界面，便于快速部署和使用
- **低侵入性**: 最小化对目标 SQL Server 实例的影响
- **数据持久化**: 将收集的跟踪数据存储在本地数据库中，便于离线分析
- **可扩展性**: 支持多种跟踪事件和自定义过滤条件
- **开源友好**: 采用 MIT 许可证，便于社区贡献和企业使用

## 运行原理

### 核心技术原理

#### SQL Server 跟踪机制
- 利用 SQL Server 的内置跟踪功能 (SQL Trace)
- 通过系统存储过程如 `sp_trace_create`、`sp_trace_start`、`sp_trace_stop` 管理跟踪会话
- 创建轻量级跟踪，仅收集必要的性能数据

#### 连接建立过程
1. **参数验证**: 验证提供的连接参数 (主机、端口、用户名、密码)
2. **连接测试**: 使用 Microsoft SQL Server JDBC Driver 建立连接
3. **权限验证**: 验证用户是否有创建和管理跟踪的权限
4. **连接池管理**: 使用内部连接池管理与 SQL Server 的连接

#### 跟踪会话管理
- **创建跟踪**: 调用 `sp_trace_create` 创建新的跟踪会话
- **设置事件**: 配置要跟踪的事件类型和列
- **启动跟踪**: 调用 `sp_trace_setevent` 和 `sp_trace_start` 启动跟踪
- **状态管理**: 维护跟踪生命周期 (开始、暂停、停止)

### 数据处理流程

#### 数据收集过程
1. **数据读取**: RawTraceReader 从 SQL Server 读取跟踪数据
2. **事件封装**: 将原始数据封装为 ProfilerEvent 对象
3. **异步处理**: ProfilerThread 异步处理事件队列
4. **数据存储**: 将事件数据存储到本地 HSQLDB 的 TRACE_LOG 表中

#### 数据过滤和处理
- **事件过滤**: 根据配置过滤特定事件类型
- **数据截断**: 对过长的文本数据进行截断以优化存储
- **格式转换**: 将 SQL Server 数据类型转换为适合存储的格式

### 本地存储机制

#### HSQLDB 集成
- **嵌入式数据库**: 使用 HSQLDB 作为本地数据存储
- **自动初始化**: 首次运行时自动创建数据库和表结构
- **持久化存储**: 将跟踪数据保存到本地文件系统

#### 数据表结构
- **TRACE_SERVER**: 存储 SQL Server 连接配置
- **TRACE_LOG**: 存储详细的跟踪记录
- **TRACE_EVENTS**: 定义可用的跟踪事件
- **TRACE_COLUMNS**: 定义跟踪列信息

## Maven 依赖

```xml
<dependency>
    <groupId>com.gdxsoft</groupId>
    <artifactId>emp-sqlserver-profiler</artifactId>
    <version>1.0.3</version>
</dependency>
```

## 启动项目

[emp-sqlserver-profiler-boot](https://github.com/gdx1231/emp-sqlserver-profiler-boot)

## 许可证

本项目采用 MIT 许可证。
# emp-sqlserver-profiler 项目说明

## 项目概述

`emp-sqlserver-profiler` 是一个基于 Java 的 SQL Server 性能分析工具，参考了 .NET 项目 [ExpressProfiler](https://github.com/ststeiger/ExpressProfiler)。该项目允许用户监控和分析 SQL Server 数据库的活动，包括查询执行、性能指标等。

主要功能包括：
- 实时监控 SQL Server 跟踪事件
- 记录 SQL 查询执行详情（如 CPU 使用、读写次数、持续时间等）
- 支持启动、暂停、恢复、停止等控制命令
- 将跟踪数据存储到本地 HSQLDB 数据库中
- 支持导出跟踪记录为 JSON 文件

## 技术栈

- **语言**: Java 8+
- **构建工具**: Maven
- **数据库驱动**: Microsoft SQL Server JDBC Driver
- **嵌入式数据库**: HSQLDB (用于本地存储跟踪数据)
- **日志框架**: SLF4J + Logback
- **JSON处理**: JSON-java
- **Apache Commons**: 提供各种实用工具类

## 项目结构

```
emp-sqlserver-profiler/
├── pom.xml                 # Maven 构建配置
├── README.md              # 项目说明文档
├── LICENSE                # 许可证文件
├── run.sh.txt             # Linux/Mac 启动脚本示例
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gdxsoft/sqlProfiler/
│   │   │       ├── ProfilerControl.java     # 主入口点和控制逻辑
│   │   │       ├── SqlServerProfiler.java   # 核心分析器实现
│   │   │       ├── RawTraceReader.java      # SQL Server 跟踪读取器
│   │   │       ├── HSqlDbServer.java        # HSQLDB 服务器管理
│   │   │       ├── ProfilerEvent.java       # 分析事件模型
│   │   │       ├── ProfilerThread.java      # 分析线程
│   │   │       ├── ProfilingStateEnum.java  # 分析状态枚举
│   │   │       └── ...                      # 其他相关类
│   │   └── resources/
│   │       ├── ewa_conf_console.xml         # 应用程序配置
│   │       ├── logback.xml                  # 日志配置
│   │       └── com/gdxsoft/sqlProfiler/hsqldb/ # HSQLDB 初始数据文件
│   └── test/
│       └── java/
└── ...
```

## 核心组件

### 1. ProfilerControl
主入口点类，负责处理命令行参数和控制台交互，支持交互式和参数化两种运行模式。

### 2. SqlServerProfiler
核心分析器类，负责与 SQL Server 建立连接、创建和管理跟踪会话、处理跟踪数据。

### 3. RawTraceReader
SQL Server 跟踪读取器，负责实际从 SQL Server 读取跟踪数据。

### 4. HSqlDbServer
管理本地 HSQLDB 数据库，用于存储从 SQL Server 获取的跟踪数据。

### 5. 数据库表结构
- `TRACE_SERVER`: 存储 SQL Server 连接配置
- `TRACE_LOG`: 存储跟踪记录
- `TRACE_EVENTS`: 跟踪事件定义
- `TRACE_COLUMNS`: 跟踪列定义
- `TRACE_CATEGORIES`: 跟踪类别定义

## 构建和运行

### 构建项目
```bash
mvn clean package
```

### 运行方式

#### Linux/Mac:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar:target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

#### Windows:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar;target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

### 参数说明
- `-h`: SQL Server 主机或 IP (默认 localhost)
- `-u`: 用户名 (默认 sa)
- `-p`: 密码 (必需)
- `-d`: 过滤数据库 (默认为空)
- `-w`: HSQLDB 路径 (默认为 /home/admin/com.gdxsoft.sqlProfiler.hsqldb_server)

### 控制命令
- `start`: 启动分析器
- `pause`: 暂停分析器
- `resume`: 恢复分析器
- `stop`: 停止分析器
- `status`: 获取分析器状态
- `state`: 获取分析器状态
- `clear`: 清除所有跟踪记录
- `export`: 导出所有跟踪记录
- `truncate`: 截断本地记录
- `quit`: 停止并退出
- `help`: 显示帮助信息

### 单包构建
如果需要构建包含所有依赖的单个 JAR 包，可以使用以下命令：
```bash
mvn -P onepackage package
```

这将生成一个名为 `target/{project-name}.onepackage.jar` 的可执行 JAR 文件。

## 开发约定

- 代码遵循 Java 编码规范
- 使用 SLF4J 进行日志记录
- 通过 Maven 管理依赖和构建过程
- 使用 HSQLDB 作为嵌入式数据库存储跟踪数据
- 支持 AES 加密存储敏感信息（如密码）

## 许可证

该项目采用 MIT 许可证。
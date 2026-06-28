# GraalVM Native Image 构建指南

## 概述

本项目支持通过 GraalVM Native Image 将 Java 应用编译为原生可执行文件（Mach-O / ELF / PE），无需 JVM 即可运行。

## 前置条件

- **GraalVM JDK 21**（Oracle GraalVM 或 GraalVM Community Edition）
  - 安装路径示例：`/Library/Java/JavaVirtualMachines/graalvm-jdk-21/Contents/Home`
- **native-image** 工具（GraalVM JDK 自带）
- **Maven 3.8+**

验证环境：
```bash
$JAVA_HOME/bin/native-image --version
```

## 构建

```bash
# 设置 GraalVM 为当前 JDK
export JAVA_HOME=/path/to/graalvm-jdk-21/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# 执行 native 构建
mvn -P native clean package
```

成功后输出：
```
target/emp-sqlserver-profiler-1.0.3    # 原生可执行文件（约 66MB）
```

## 运行

```bash
./target/emp-sqlserver-profiler-1.0.3 -h <host> -u <user> -p <password> -d <database>
```

参数和交互命令与 JVM 模式完全一致，详见 [README.md](README.md)。

## Native Image 配置详解

### Maven Profile (`pom.xml`)

```xml
<profile>
    <id>native</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>0.10.2</version>
                <extensions>true</extensions>
                <executions>
                    <execution>
                        <id>build-native</id>
                        <goals>
                            <goal>compile-no-fork</goal>
                        </goals>
                        <phase>package</phase>
                    </execution>
                </executions>
                <configuration>
                    <imageName>${project.build.finalName}</imageName>
                    <mainClass>com.gdxsoft.sqlProfiler.ProfilerControl</mainClass>
                    <buildArgs>
                        <!-- 详见下节 -->
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

### buildArgs 参数说明

| 参数 | 用途 |
|------|------|
| `-H:+ReportExceptionStackTraces` | 构建失败时输出完整堆栈，便于排查 |
| `--initialize-at-run-time=sun.security.util.Password$ConsoleHolder` | 解决 `ProxyingConsole` 与 image heap 的初始化冲突 |
| `-H:+AddAllCharsets` | 包含全部字符集，支持 MS936（GBK）等中文编码 |
| `-H:IncludeResources=ewa_conf_console\.xml` | 包含应用配置文件 |
| `-H:IncludeResources=logback\.xml` | 包含 Logback 日志配置 |
| `-H:IncludeResources=com/gdxsoft/sqlProfiler/hsqldb/.*` | 包含 HSQLDB 初始数据文件 |
| `-H:IncludeResources=org/hsqldb/.*` | 包含 HSQLDB 内部 SQL 脚本等资源文件 |
| `--enable-url-protocols=http` | 允许 HTTP 协议的 URL 连接 |
| `-H:IncludeResourceBundles=org.hsqldb.resources.` | 包含 HSQLDB 国际化资源包 |

### 反射配置 (`reflect-config.json`)

路径：`src/main/resources/META-INF/native-image/com.gdxsoft/emp-sqlserver-profiler/reflect-config.json`

Native Image 的封闭世界分析无法覆盖所有反射调用，需显式注册以下类：

**Logback 类：**
- `ch.qos.logback.core.ConsoleAppender` — 控制台日志输出
- `ch.qos.logback.classic.encoder.PatternLayoutEncoder` — 日志格式编码器
- `ch.qos.logback.classic.PatternLayout` — 日志格式布局
- `ch.qos.logback.core.status.InfoStatus` / `WarnStatus` — 状态信息

**HSQLDB 类：**
- `org.hsqldb.dbinfo.DatabaseInformationFull` / `DatabaseInformationMain` — 数据库 schema 信息（反射实例化）
- `org.hsqldb.jdbc.JDBCDriver` — JDBC 驱动注册
- `org.hsqldb.Database` — 反射调用构造函数的参数类型

## 文件结构

```
src/main/resources/META-INF/native-image/com.gdxsoft/emp-sqlserver-profiler/
└── reflect-config.json    # 反射类注册

pom.xml
└── profiles/native        # Native 构建配置

target/
└── emp-sqlserver-profiler-1.0.3    # 原生可执行文件
```

## 常见问题

### 1. `ProxyingConsole` image heap 冲突

**错误信息：**
```
Error: An object of type 'java.io.ProxyingConsole' was found in the image heap.
This type is marked for initialization at image run time.
```

**原因：** `sun.security.util.Password$ConsoleHolder` 在 build-time 初始化时将 `java.io.ProxyingConsole` 对象写入了 image heap，而 JDK JNI 注册将该类标记为 runtime 初始化，两者冲突。

**解决：** `--initialize-at-run-time=sun.security.util.Password$ConsoleHolder`

### 2. 中文编码 MS936 / GBK 不支持

**错误信息：**
```
java.io.UnsupportedEncodingException: Codepage MS936 is not supported by the Java environment.
```

**原因：** Native Image 默认不包含所有字符集编码。

**解决：** `-H:+AddAllCharsets`

### 3. HSQLDB `MissingResourceException`

**错误信息：**
```
java.util.MissingResourceException: Can't find bundle for base name
org.hsqldb.resources.sql-state-messages, locale zh_CN_#Hans
```

**原因：** HSQLDB 的资源包未被包含。

**解决：** `-H:IncludeResourceBundles=org.hsqldb.resources.`

### 4. HSQLDB `NoSuchMethodException: DatabaseInformationFull.<init>`

**错误信息：**
```
java.lang.NoSuchMethodException:
org.hsqldb.dbinfo.DatabaseInformationFull.<init>(org.hsqldb.Database)
```

**原因：** HSQLDB 通过反射实例化 `DatabaseInformationFull`，Native Image 无法自动发现该构造函数。

**解决：** 在 `reflect-config.json` 中注册 `org.hsqldb.dbinfo.DatabaseInformationFull`。

### 5. Logback `ClassNotFoundException: ConsoleAppender`

**错误信息：**
```
java.lang.ClassNotFoundException: ch.qos.logback.core.ConsoleAppender
```

**原因：** Logback 通过反射加载 appender 类。

**解决：** 在 `reflect-config.json` 中注册 logback 相关类。

## 与 JVM 模式的差异

| 特性 | JVM (JAR) | Native Image |
|------|-----------|--------------|
| 启动时间 | ~2s | ~0.05s |
| 内存占用 | ~200MB+ | ~50MB |
| 文件大小 | ~15MB (JAR) | ~66MB |
| 动态类加载 | ✅ | ❌ |
| 反射 | 自动 | 需 `reflect-config.json` 注册 |
| 资源加载 | 自动 | 需 `-H:IncludeResources` 声明 |
| 构建时间 | ~10s | ~2min |

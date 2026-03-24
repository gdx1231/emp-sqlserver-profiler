# emp-sqlserver-profiler

A Java-based SQL Server Profiler tool that monitors and analyzes SQL Server database activities, referencing the .NET project [ExpressProfiler](https://github.com/ststeiger/ExpressProfiler).

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Parameters](#parameters)
- [Control Commands](#control-commands)
- [Architecture](#architecture)
- [Project Goals](#project-goals)
- [Operational Principles](#operational-principles)
- [Maven Dependency](#maven-dependency)

## Overview

`emp-sqlserver-profiler` is a lightweight, cross-platform SQL Server performance analysis tool designed to monitor database activities, capture query execution details, and analyze performance metrics. It leverages SQL Server's built-in tracing capabilities to collect detailed information about database operations.

## Features

- Real-time monitoring of SQL Server activities
- Performance analysis with metrics like CPU usage, read/write counts, and execution time
- Issue diagnosis for slow queries and resource-intensive operations
- Data persistence in local HSQLDB for offline analysis
- Cross-platform compatibility via Java implementation
- Simple command-line interface for easy deployment
- Support for multiple concurrent profiler instances
- Configurable event filtering and data collection

## Installation

1. Clone the repository:
```bash
git clone https://github.com/gdx1231/emp-sqlserver-profiler.git
```

2. Build the project:
```bash
mvn clean package
```

3. The built JAR file will be located in the `target/` directory.

## Usage

### Linux/Mac:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar:target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

### Windows:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar;target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

### Interactive Mode:
Run without parameters to enter interactive mode:
```bash
java -cp target/emp-sqlserver-profiler-1.0.3.jar:target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl
```

## Parameters

    -h SQLServer host or ip (default localhost)
    -u Username (default sa)
    -p Password* (must input)
    -d Filter database (default blank)
    -w HSQLDB path (default /home/admin/com.gdxsoft.sqlProfiler.hsqldb_server)
    -P Port number (default 1433)

## Control Commands

    start: start the profiler.
    pause: pause the profiler.
    resume: resume the profiler.
    stop: stop the profiler.
    status: get status of the profiler.
    state:  get state of the profiler.
    clear: clear all SQLServer traces.
    export: export all trace records, saved in the HSQLDB work path.
    truncate: truncate the HSQLDB local records(TRACE_LOG).
    quit: to stop and quit.
    help: to show this.

## Architecture

### Core Components

#### ProfilerControl
Main entry point that handles command-line arguments and console interaction.

#### SqlServerProfiler
Core profiler class responsible for managing connections to SQL Server and controlling trace sessions.

#### RawTraceReader
Component that reads trace data directly from SQL Server.

#### HSqlDbServer
Local HSQLDB server manager for data persistence.

#### ProfilerThread
Background thread that continuously reads and processes trace data.

### Data Flow
```
SQL Server → RawTraceReader → ProfilerEvent → ProfilerThread → HSqlDbServer → TRACE_LOG
```

## Project Goals

### Primary Objectives
- **Real-time Monitoring**: Capture SQL Server database activities in real-time
- **Performance Analysis**: Analyze SQL query performance metrics like CPU usage, read/write counts, execution time
- **Issue Diagnosis**: Identify slow queries, resource-consuming operations, and potential performance bottlenecks
- **Data Collection**: Collect detailed database operation logs for subsequent analysis and auditing
- **Cross-Platform Compatibility**: Provide Java implementation supporting multiple operating systems

### Design Goals
- **Usability**: Provide a simple command-line interface for quick deployment and use
- **Low Intrusiveness**: Minimize impact on the target SQL Server instance
- **Data Persistence**: Store collected trace data in a local database for offline analysis
- **Extensibility**: Support multiple trace events and custom filtering conditions
- **Open Source Friendly**: Use MIT license for community contributions and enterprise use

## Operational Principles

### Core Technology Principles

#### SQL Server Tracing Mechanism
- Utilizes SQL Server's built-in tracing functionality (SQL Trace)
- Manages trace sessions through system stored procedures like `sp_trace_create`, `sp_trace_start`, `sp_trace_stop`
- Creates lightweight traces collecting only necessary performance data

#### Connection Establishment Process
1. **Parameter Validation**: Validates provided connection parameters (host, port, username, password)
2. **Connection Testing**: Establishes connection using Microsoft SQL Server JDBC Driver
3. **Permission Verification**: Verifies user has permissions to create and manage traces
4. **Connection Pool Management**: Uses internal connection pool to manage connections to SQL Server

#### Trace Session Management
- **Creating Traces**: Calls `sp_trace_create` to create new trace sessions
- **Setting Events**: Configures event types and columns to be traced
- **Starting Traces**: Calls `sp_trace_setevent` and `sp_trace_start` to start tracing
- **State Management**: Maintains trace lifecycle (start, pause, stop)

### Data Processing Flow

#### Data Collection Process
1. **Data Reading**: RawTraceReader reads trace data from SQL Server
2. **Event Encapsulation**: Wraps raw data into ProfilerEvent objects
3. **Asynchronous Processing**: ProfilerThread asynchronously processes event queues
4. **Data Storage**: Stores event data in local HSQLDB's TRACE_LOG table

#### Data Filtering and Processing
- **Event Filtering**: Filters specific event types based on configuration
- **Data Truncation**: Truncates overly long text data to optimize storage
- **Format Conversion**: Converts SQL Server data types to storage-friendly formats

### Local Storage Mechanism

#### HSQLDB Integration
- **Embedded Database**: Uses HSQLDB as local data storage
- **Automatic Initialization**: Automatically creates database and table structure on first run
- **Persistent Storage**: Saves trace data to local file system

#### Data Table Structure
- **TRACE_SERVER**: Stores SQL Server connection configurations
- **TRACE_LOG**: Stores detailed trace records
- **TRACE_EVENTS**: Defines available trace events
- **TRACE_COLUMNS**: Defines trace column information

## Maven Dependency

```xml
<dependency>
    <groupId>com.gdxsoft</groupId>
    <artifactId>emp-sqlserver-profiler</artifactId>
    <version>1.0.3</version>
</dependency>
```

## Boot Project

[emp-sqlserver-profiler-boot](https://github.com/gdx1231/emp-sqlserver-profiler-boot)

## License

This project is licensed under the MIT License.

# About emp-sqlserver-profiler

Java sqlserver profiler, ref the .NET project [ExpressProfiler](https://github.com/ststeiger/ExpressProfiler)

## Usage (linux/mac)

```bash
java -cp target/emp-sqlserver-profiler-1.0.1.jar:target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

## Usage (windows)

```bash
java -cp target/emp-sqlserver-profiler-1.0.1.jar;target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl -h 192.168.1.100 -u sa -p yourpassword -d master -P 1433
```

## Parameters

    -h SQLServer host or ip (default localhost)
    -u Username (default sa)
    -p Password* (must input)
    -d Filter database (default blank)
    -w HSQLDB path (default /home/admin/com.gdxsoft.sqlProfiler.hsqldb_server)

## Control commands

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

## Boot project

[emp-sqlserver-profiler-boot](https://github.com/gdx1231/emp-sqlserver-profiler-boot)

## Maven

```xml
<dependency>
    <groupId>com.gdxsoft</groupId>
    <artifactId>emp-sqlserver-profiler</artifactId>
    <version>1.0.1</version>
</dependency>
```

package com.gdxsoft.sqlProfiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.gdxsoft.easyweb.utils.UJSon;
import com.gdxsoft.easyweb.utils.UPath;
import com.gdxsoft.easyweb.utils.msnet.MStr;

public class ProfilerControl {
	private static SqlServerProfiler consoleSqlServerProfiler;

	private static SqlServerProfiler interactive(BufferedReader console) throws Exception {
		System.out.print("SQLServer host (localhost): ");
		String host = readConsoleLine(console);
		if (StringUtils.isBlank(host)) {
			host = "localhost";
		}
		System.out.print("SQLServer port (1433): ");
		String sport = readConsoleLine(console);
		int port = 1433;
		if (!StringUtils.isBlank(sport)) {
			try {
				port = Integer.parseInt(sport);
			} catch (Exception err) {
				System.out.println("Invalid port, " + err.getMessage());
				System.exit(0);
			}
		}
		System.out.print("Database (blank): ");
		String database = readConsoleLine(console);

		System.out.print("Username (sa): ");
		String username = readConsoleLine(console);
		if (StringUtils.isBlank(username)) {
			username = "sa";
		}
		System.out.print("Password*: ");
		String password = readConsoleLine(console);
		while (StringUtils.isBlank(password)) {
			System.out.print("Password must input");
			password = readConsoleLine(console);
		}
		String tempDir = HSqlDbServer.getDefaultWorkPath();

		System.out.print("HSQLDB database path(" + tempDir + "): ");
		String workPath = readConsoleLine(console);
		if (StringUtils.isBlank(workPath)) {
			workPath = tempDir;
		}
		HSqlDbServer.WORK_PATH = workPath;

		System.out.println("Try connection to the SQLServer, " + host + ":" + port);
		SqlServerProfiler profiler = SqlServerProfiler.getInstance(host, port, database, username, password);
		return profiler;
	}

	private static SqlServerProfiler byArgs(String[] args) throws Exception {
		int length = args.length;
		String host = "localhost";
		String sport = "";
		String username = "sa";
		String password = "";
		String database = "";
		String tempDir = "";

		MStr help = new MStr();
		// help.al("Usage: --host localhost --port 1433 --username sa --password xxx
		// --database mydb");
		help.al("https://github.com/gdx1231/emp-sqlserver-profiler");
		help.al("Usage: -h localhost -u sa -p yourPassword -d yourDatabase -P 1433");
		help.al("Details: ");
		help.al("    -h SQLServer host or ip (default localhost)");
		help.al("    -u Username (default sa)");
		help.al("    -p Password* (must input)");
		help.al("    -d Filter database (default blank)");
		help.al("    -w HSQLDB path (default " + HSqlDbServer.getDefaultWorkPath() + ")");
		
		if (length % 2 != 0) {
			System.out.println(help);
			System.exit(0);
		}

		for (int i = 0; i < length; i += 2) {
			String cmd = args[i];
			String cmd1 = args[i + 1];
			if (cmd.equals("--host") || cmd.equals("-h")) {
				host = cmd1;
			} else if (cmd.equals("--port") || cmd.equals("-P")) {
				sport = cmd1;
			} else if (cmd.equals("--username") || cmd.equals("-u")) {
				username = cmd1;
			} else if (cmd.equals("--password") || cmd.equals("-p")) {
				password = cmd1;
			} else if (cmd.equals("--database") || cmd.equals("-d")) {
				database = cmd1;
			} else if (cmd.equals("--workpath") || cmd.equals("-w")) {
				tempDir = cmd1;
				HSqlDbServer.WORK_PATH = tempDir; // hsqldb的工作目录
			} else {
				System.out.println("Invalid parameter: " + cmd);
			}
		}
		int port = 1433;
		if (!StringUtils.isBlank(sport)) {
			try {
				port = Integer.parseInt(sport);
			} catch (Exception err) {
				throw new Exception("端口为数字" + err.getMessage());
			}
		}
		if (StringUtils.isBlank(password)) {
			throw new Exception("参数-password需要提供");
		}

		System.out.println("Try connection to the SQLServer, " + host + ":" + port);
		SqlServerProfiler profiler = SqlServerProfiler.getInstance(host, port, database, username, password);
		return profiler;
	}

	private static void showHelp() {
		MStr sb = new MStr();
		sb.al("Enter 'start' to start.");
		sb.al("Enter 'pause' to pause.");
		sb.al("Enter 'resume' to resume.");
		sb.al("Enter 'stop' to stop.");
		sb.al("Enter 'status' to get status.");
		sb.al("Enter 'state' to get state.");
		sb.al("Enter 'clear' to clear all traces.");
		sb.al("Enter 'export' to export all records.");
		sb.al("Enter 'truncate' to truncate local records(TRACE_LOG).");
		sb.al("Enter 'quit' to stop and quit.");
		sb.al("Enter 'help' to show this.");

		System.out.println(sb.toString());
	}

	public static void console(String[] args) {
		UPath.initPath();

		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		SqlServerProfiler profiler = null;

		try {
			if (args.length == 0) {
				profiler = interactive(console);
			} else {
				profiler = byArgs(args);
			}

			HSqlDbServer.getInstance();

			profiler.clearProfilers();

			profiler.setConsoleMode(true);

			consoleSqlServerProfiler = profiler;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
			return;
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("shutdown");
				try {
					consoleSqlServerProfiler.stopProfiling();
				} catch (Exception ioEx) {
					ioEx.printStackTrace();
				}
			}
		});
		showHelp();
		try {
			while (true) {
				String str = readConsoleLine(console);
				if (str.equals("help")) {
					showHelp();
				} else {
					JSONObject result = ProfilerControl.control(profiler, str);
					System.out.println(result.toString(2));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String readConsoleLine(BufferedReader obj) {
		String str;
		try {
			str = obj.readLine();
			return str;
		} catch (IOException e) {
			return e.getLocalizedMessage();
		}

	}

	public static JSONObject control(SqlServerProfiler sp, String method) throws Exception {
		JSONObject result = UJSon.rstTrue();
		result.put("method", method);
		result.put("ts_id", sp.getTsId());

		if ("start".equals(method)) {
			if (sp.getProfilingState() == null || sp.getProfilingState() == ProfilingStateEnum.psStopped) {
				sp.startProfiling();
				result.put("newstart", true);
			} else {
				JSONObject stat = sp.getTraceStatusFromSysTraces();
				if (stat.optInt("status") < 0) {
					sp.startProfiling();
					result.put("newstart", true);
				}
				UJSon.rstSetFalse(result, "已经启动了");
			}
			result.put("trace_id", sp.getRdr().getTraceId());
		} else if ("pause".equals(method)) {
			if (sp.getProfilingState() == ProfilingStateEnum.psStopped) {
				UJSon.rstSetFalse(result, "停止了");
			} else if (sp.getProfilingState() == ProfilingStateEnum.psPaused) {
				UJSon.rstSetFalse(result, "已经暂停了");
			} else {
				sp.pauseProfiling();
			}
		} else if ("resume".equals(method)) {
			if (sp.getProfilingState() == ProfilingStateEnum.psStopped) {
				UJSon.rstSetFalse(result, "停止了，需要重新启动");
			} else if (sp.getProfilingState() == ProfilingStateEnum.psProfiling) {
				UJSon.rstSetFalse(result, "跟踪中，不需要启动");
			} else {
				sp.resumeProfiling();
			}
		} else if ("stop".equals(method)) {
			if (sp.getProfilingState() == ProfilingStateEnum.psStopped) {
				UJSon.rstSetFalse(result, "已经停止了");
			} else {
				sp.stopProfiling();
			}
		} else if ("quit".equals(method)) { // 清除表记录(TRACE_LOG)
			if (sp.getProfilingState() != ProfilingStateEnum.psStopped) {
				sp.stopProfiling();
			}
			System.exit(0);
		} else if ("status".equals(method)) {
			return sp.getTraceStatusFromSysTraces();
		} else if ("clear".equals(method)) {
			sp.clearProfilers();
		} else if ("state".equals(method)) {
			if (sp.getRdr() != null) {
				result.put("reader_next", sp.getRdr().isTraceIsActive());
				result.put("reader_closed", sp.getRdr().getReader().isClosed());
				/*
				 * result.put("reader_isFirst", sp.getRdr().getReader().isFirst());
				 * result.put("reader_isLast", sp.getRdr().getReader().isLast());
				 */
				result.put("thread_name", sp.getThr().getName());
				result.put("thread_alive", sp.getThr().isAlive());
				result.put("thread_state", sp.getThr().getState());
			}
		} else if ("export".equals(method)) { // 导出记录，创建json文件
			try {
				String file = sp.exportRecords();
				result.put("export_file", file);
			} catch (Exception err) {
				UJSon.rstSetFalse(result, err.getLocalizedMessage());
				result.put("export_file", err.getLocalizedMessage());
			}
		} else if ("truncate".equals(method)) { // 清除表记录(TRACE_LOG)
			sp.truncateRecords();
		} else {
			UJSon.rstSetFalse(result, "Invalid method");
		}
		result.put("profiling_state", sp.getProfilingState());
		if (sp.isConsoleMode()) {
			result.put("work_path", HSqlDbServer.WORK_PATH);
			result.put("hsqldb", HSqlDbServer.HSQLDB_URL);
		}
		return result;
	}

	public static void main(String[] args) {
		console(args);
	}
}

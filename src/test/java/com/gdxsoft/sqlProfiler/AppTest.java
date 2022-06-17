package com.gdxsoft.sqlProfiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.LogManager;

import org.json.JSONObject;

import com.gdxsoft.easyweb.utils.UJSon;
import com.gdxsoft.easyweb.utils.msnet.MStr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class AppTest extends TestCase {
	static {
		InputStream stream = TestCase.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MStr sb = new MStr();
		sb.al("Enter 'start' to start.");
		sb.al("Enter 'pause' to pause.");
		sb.al("Enter 'resume' to resume.");
		sb.al("Enter 'stop' to stop.");
		sb.al("Enter 'status' to get status.");
		sb.al("Enter 'quit' to stop and quit.");
		sb.al("Enter 'help' to show this.");

		int tsId = 1;
		SqlServerProfiler profiler = null;
		try {
			HSqlDbServer.getInstance();
		} catch (Exception e1) {
			System.err.println(e1);
			return;
		}
		try {
			profiler = SqlServerProfiler.getInstance(tsId);
			profiler.setConsoleMode(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println(sb.toString());
		try {
			BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String str = obj.readLine();
				if (str.equals("quit")) {
					profiler.stopProfiling();
					System.exit(0);
					break;
				} else if (str.equals("help")) {
					System.out.println(sb.toString());
				} else {
					JSONObject result = control(profiler, str);
					System.out.println(result.toString(2));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JSONObject control(SqlServerProfiler sp, String method) throws Exception {
		JSONObject result = UJSon.rstTrue();
		result.put("method", method);
		result.put("ts_id", sp.getTsId());

		if ("start".equals(method)) {
			if (sp.getProfilingState() == null || sp.getProfilingState() == ProfilingStateEnum.psStopped) {
				sp.startProfiling();
				result.put("newstart", true);
			} else {
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
		} else if ("next".equals(method)) {
			if (sp.getRdr() != null) {
				sp.getRdr().next();
				result.put("reader_next", sp.getRdr().isTraceIsActive());
			}
		}
		result.put("profiling_state", sp.getProfilingState());
		return result;
	}

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}
}

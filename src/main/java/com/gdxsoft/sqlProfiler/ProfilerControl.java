package com.gdxsoft.sqlProfiler;

import org.json.JSONObject;

import com.gdxsoft.easyweb.utils.UJSon;

public class ProfilerControl {
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
				if(stat.optInt("status") < 0) {
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
		}  else {
			UJSon.rstSetFalse(result, "Invalid method");
		}
		result.put("profiling_state", sp.getProfilingState());
		return result;
	}
}

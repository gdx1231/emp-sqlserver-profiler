package com.gdxsoft.sqlProfiler;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilerThread extends Thread {
	private static Logger LOGGER = LoggerFactory.getLogger(ProfilerThread.class);
	private SqlServerProfiler sqlServerProfiler;

	public ProfilerThread(SqlServerProfiler sqlServerProfiler) {
		this.sqlServerProfiler = sqlServerProfiler;
	}

	public void run() {
		LOGGER.info("Start the ProfilerThread");
		profilerThread();
	}

	private void profilerThread() {
		while (true) {
			if( ProfilingStateEnum.psStopped == sqlServerProfiler.m_ProfilingState) {
				LOGGER.info("Profile stoped, exit profilerThread");
				break;
			}
			if (sqlServerProfiler.m_NeedStop || sqlServerProfiler.m_ProfilingState == null
					|| sqlServerProfiler.m_ProfilingState != ProfilingStateEnum.psProfiling
					|| !sqlServerProfiler.m_Rdr.isTraceIsActive()) {
				try {
					Thread.sleep(100L);
					continue;
				} catch (InterruptedException e) {
					LOGGER.warn(e.getMessage());
				}
			}
			// 有新的数据了
			try {
				this.profilerThread1();
			} catch (SQLException e) {
				LOGGER.warn(e.getMessage());
			}
		}
	}

	private void profilerThread1() throws SQLException {
		// 有新的数据了
		ProfilerEvent evt = sqlServerProfiler.m_Rdr.next();
		if (evt != null) {
			if (sqlServerProfiler.isConsoleMode()) {
				sqlServerProfiler.m_events.add(evt);
			}
			sqlServerProfiler.recordToDb(evt);
		}

	}
}

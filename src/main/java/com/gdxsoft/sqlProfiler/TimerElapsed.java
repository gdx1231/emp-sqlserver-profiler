/**
 * 
 */
package com.gdxsoft.sqlProfiler;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author admin
 *
 */
public class TimerElapsed extends java.util.TimerTask {
	long minute = 1000L * 60;
	private SqlServerProfiler sqlServerProfiler;

	/**
	 * 
	 */
	public TimerElapsed(SqlServerProfiler sqlServerProfiler) {
		this.sqlServerProfiler = sqlServerProfiler;
	}

	@Override
	public void run() {
		ConcurrentLinkedQueue<ProfilerEvent> events = this.sqlServerProfiler.m_events;
		if(events == null) {
			return;
		}
		int i = events.size();
		try {
			while (i > 0) {
				ProfilerEvent evt = events.poll();
				i = events.size();
				if (evt != null)
					sqlServerProfiler.newEventArrived(evt, i == 1);
				else
					Thread.sleep(50);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	 
}

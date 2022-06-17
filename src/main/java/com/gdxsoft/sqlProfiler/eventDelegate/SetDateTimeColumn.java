package com.gdxsoft.sqlProfiler.eventDelegate;

import java.sql.SQLException;

import com.gdxsoft.sqlProfiler.ProfilerEvent;

public class SetDateTimeColumn extends SetBaseColumn implements SetEventDelegate {

	public void setColumn(ProfilerEvent evt, int columnid) throws SQLException {
		super.setDateTimeColumn(evt, columnid);
	}
}

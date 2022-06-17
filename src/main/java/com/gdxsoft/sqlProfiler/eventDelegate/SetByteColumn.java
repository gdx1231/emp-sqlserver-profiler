package com.gdxsoft.sqlProfiler.eventDelegate;

import java.sql.SQLException;

import com.gdxsoft.sqlProfiler.ProfilerEvent;

public class SetByteColumn extends SetBaseColumn implements SetEventDelegate {
 

	public void setColumn(ProfilerEvent evt, int columnid) throws SQLException {
		 super.setByteColumn(evt, columnid);
		
	}
}

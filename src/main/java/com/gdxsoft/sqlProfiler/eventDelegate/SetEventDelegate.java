package com.gdxsoft.sqlProfiler.eventDelegate;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.gdxsoft.sqlProfiler.ProfilerEvent;

public interface SetEventDelegate {
	void setColumn(ProfilerEvent evt, int columnid) throws SQLException ; 

	public ResultSet getReader();

	/**
	 * @param m_Reader the m_Reader to set
	 */
	public void setReader(ResultSet reader) ;

}

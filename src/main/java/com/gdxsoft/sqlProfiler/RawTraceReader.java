package com.gdxsoft.sqlProfiler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.datasource.DataConnection;
import com.gdxsoft.easyweb.utils.UConvert;
import com.gdxsoft.easyweb.utils.msnet.MStr;
import com.gdxsoft.sqlProfiler.SqlServerProfiler.IntFilterCondition;
import com.gdxsoft.sqlProfiler.SqlServerProfiler.StringFilterCondition;
import com.gdxsoft.sqlProfiler.eventDelegate.*;
import com.gdxsoft.sqlProfiler.rawTraceReader.ComparisonOperators;
import com.gdxsoft.sqlProfiler.rawTraceReader.LogicalOperators;
import com.gdxsoft.sqlProfiler.rawTraceReader.StoredProcedures;
import com.gdxsoft.sqlProfiler.rawTraceReader.TSQL;

public class RawTraceReader {
	private static Logger LOGGER = LoggerFactory.getLogger(RawTraceReader.class);
	// private delegate void SetEventDelegate(ProfilerEvent evt, int columnid);
	private ResultSet m_Reader;
	private DataConnection m_Conn;
	private int m_TraceId;

	SqlServerProfiler profiler;

	SetEventDelegate evtInt = new SetIntColumn();
	SetEventDelegate evtLong = new SetLongColumn();
	SetEventDelegate evtString = new SetStringColumn();
	SetEventDelegate evtByte = new SetByteColumn();
	SetEventDelegate evtDateTime = new SetDateTimeColumn();
	SetEventDelegate evtGuid = new SetGuidColumn();

	final SetEventDelegate[] m_Delegates = new SetEventDelegate[66];
	private boolean m_LastRead;

	public RawTraceReader(DataConnection con) {
		m_Conn = con;
		initProfilerEventColumns();
	}

	/**
	 * 不断读取 sp_trace_getdata返回的resultSet（纵向表），根据columnid获取整体数据<br>
	 * 通过线程ProfilerThread不断调用此方法
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ProfilerEvent next() throws SQLException {
		boolean TraceIsActive = this.isTraceIsActive();
		if (!TraceIsActive)
			return null;
		// int columnid = (int)m_Reader[0];
		int columnid = m_Reader.getInt(1);
		// skip to begin of new event
		while (columnid != 65526 && this.readNext1() && TraceIsActive) {
			columnid = m_Reader.getInt(1);
		}
		// start of new event
		if (columnid != 65526)
			return null;
		if (!TraceIsActive)
			return null;
		// get potential event class
		// m_Reader.GetBytes(2, 0, m_B2, 0, 2);
		byte[] m_B2 = new byte[2];
		byte[] bytes = m_Reader.getBytes(3);
		System.arraycopy(bytes, 0, m_B2, 0, 2);
		int eventClass = SetBaseColumn.toInt16(m_B2);

		// we got new event
		if (eventClass >= 0 && eventClass < 255) {
			ProfilerEvent evt = new ProfilerEvent();
			evt.m_Events[27] = eventClass;
			while (this.readNext1()) {
				columnid = m_Reader.getInt(1);
				if (columnid > evt.m_Events.length)
					return evt;

				m_Delegates[columnid].setReader(m_Reader);
				m_Delegates[columnid].setColumn(evt, columnid);
			}
		}
		this.readNext1();
		return null;
	}

	private boolean tryReadNext() {
		boolean r = this.readNext1();
		if (r) {
			return true;
		}
		int inc = 0;
		while (!r) {
			inc++;
			if (inc == 5) {
				System.out.println(inc);
				return false;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				LOGGER.error("tryReadNext, {}", e.getMessage());
				return false;
			}
			try {
				this.m_Reader.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			this.createTraceGetdata();
			r = this.readNext1();
		}
		return r;
	}

	private boolean readNext1() {
		try {
			if (this.m_Reader.isClosed()) {
				LOGGER.info("m_Reader closed. ");
				this.m_LastRead = false;
				return m_LastRead;
			}
			m_LastRead = this.m_Reader.next();
			if (!m_LastRead) {
				LOGGER.info("readNext no next. {}", m_LastRead);
			}
		} catch (SQLException e) {
			m_LastRead = false;
			LOGGER.warn("readNext", e.getLocalizedMessage());
		}
		return m_LastRead;
	}

	/**
	 * 关闭连接和ResultSet
	 * 
	 * @throws SQLException
	 */
	public void close() {
		if (this.m_Reader != null) {
			LOGGER.info("Close m_reader, {},{}", this.m_Reader, this.m_Conn);
			try {
				if (!this.m_Reader.isClosed()) {
					this.m_Reader.close();
				}
			} catch (SQLException e) {
				LOGGER.warn("Close m_reader, ", e.getMessage());
			}
		}
		if (this.m_Conn != null) {
			LOGGER.info("Close m_conn, {}", this.m_Conn);
			this.m_Conn.close();
		}
		m_LastRead = false;
	}

	public void setEvent(int eventId, int... columns) {
		// sp_trace_setevent [ @traceid = ] trace_id, [ @eventid = ] event_id, [
		// @columnid = ] column_id, [ @on = ] on
		MStr sb = new MStr();
		String sql = "exec sp_trace_setevent " + m_TraceId + ", " + eventId + ", ";
		for (int i = 0; i < columns.length; i++) {
			sb.al(sql + columns[i] + ", 1;");
		}
		LOGGER.info("{}, {}", sb, this.m_Conn);
		this.m_Conn.executeUpdateNoParameter(sb.toString());
	}

	public void setFilter(int columnId, int logicalOperator, int comparisonOperator, Long value) throws Exception {
//            System.Data.SqlClient.SqlCommand cmd = new System.Data.SqlClient.SqlCommand { 
//                  Connection = m_Conn
//                , CommandText = "sp_trace_setfilter"
//                , CommandType = System.Data.CommandType.StoredProcedure 
//            };
//            cmd.Parameters.Add("@traceid", System.Data.SqlDbType.Int).Value = m_TraceId;
//            cmd.Parameters.Add("@columnid", System.Data.SqlDbType.Int).Value = columnId;
//            cmd.Parameters.Add("@logical_operator", System.Data.SqlDbType.Int).Value = 
//                logicalOperator;
//            cmd.Parameters.Add("@comparison_operator", System.Data.SqlDbType.Int).Value = 
//                comparisonOperator;

		String sql = "exec  sp_trace_setfilter " + m_TraceId + ", " + columnId + ", " + logicalOperator + ", "
				+ comparisonOperator;

		if (value == null) {
			// cmd.Parameters.Add("@value", System.Data.SqlDbType.Int).Value =
			// System.DBNull.Value;
			sql += ", null";
		} else {
			switch (columnId) {
			case ProfilerEventColumns.BigintData1:
			case ProfilerEventColumns.BigintData2:
			case ProfilerEventColumns.Duration:
			case ProfilerEventColumns.EventSequence:
			case ProfilerEventColumns.ObjectID2:
			case ProfilerEventColumns.Permissions:
			case ProfilerEventColumns.Reads:
			case ProfilerEventColumns.RowCounts:
			case ProfilerEventColumns.TransactionID:
			case ProfilerEventColumns.Writes:
			case ProfilerEventColumns.XactSequence:
				// cmd.Parameters.Add("@value", System.Data.SqlDbType.BigInt).Value =
				// value;
				sql += ", " + value;
				break;
			case ProfilerEventColumns.ClientProcessID:
			case ProfilerEventColumns.ColumnPermissions:
			case ProfilerEventColumns.CPU:
			case ProfilerEventColumns.DatabaseID:
			case ProfilerEventColumns.Error:
			case ProfilerEventColumns.EventClass:
			case ProfilerEventColumns.EventSubClass:
			case ProfilerEventColumns.Handle:
			case ProfilerEventColumns.IndexID:
			case ProfilerEventColumns.IntegerData:
			case ProfilerEventColumns.IntegerData2:
			case ProfilerEventColumns.IsSystem:
			case ProfilerEventColumns.LineNumber:
			case ProfilerEventColumns.Mode:
			case ProfilerEventColumns.NestLevel:
			case ProfilerEventColumns.ObjectID:
			case ProfilerEventColumns.ObjectType:
			case ProfilerEventColumns.Offset:
			case ProfilerEventColumns.OwnerID:
			case ProfilerEventColumns.RequestID:
			case ProfilerEventColumns.Severity:
			case ProfilerEventColumns.SourceDatabaseID:
			case ProfilerEventColumns.SPID:
			case ProfilerEventColumns.State:
			case ProfilerEventColumns.Success:
			case ProfilerEventColumns.Type:
				// cmd.Parameters.Add("@value", System.Data.SqlDbType.Int).Value = value;
				sql += ", " + value;
				break;
			default:
				throw new Exception(String.format("Unsupported column_id: {0}", columnId));
			}
		}
		sql += "";
		LOGGER.info("{}, {}", sql, this.m_Conn);
		this.m_Conn.executeUpdateNoParameter(sql);
	}

	public void setFilter(int columnId, int logicalOperator, int comparisonOperator, String value) {
		String sql = "exec sp_trace_setfilter " + m_TraceId + ", " + columnId + ", " + logicalOperator + ", "
				+ comparisonOperator + ", N'" + value.replace("'", "''") + "'";

		LOGGER.info("{}, {}", sql, this.m_Conn);
		this.m_Conn.executeUpdateNoParameter(sql);
	}

	/**
	 * 无法直接跟踪数据了，只能通过sql读取日志文件
	 * 
	 * @throws Exception
	 */
	public void createFileTrace() throws Exception {
		String sql0 = "select  * from sys.traces where is_default = 1";
		DTTable tb0 = DTTable.getJdbcTable(sql0, m_Conn);
		if (tb0.getCount() == 0) {
			throw new Exception("Can't find the default trace.");
		}
		String fileDefault = tb0.getCell(0, "path").toString();
		String separatorChar = fileDefault.indexOf("\\") >= 0 ? "\\" : "/";
		int index = fileDefault.lastIndexOf(separatorChar);
		String rootPath = fileDefault.substring(0, index);
		String traceFile = rootPath + separatorChar + profiler.getTraceFileName();

		String sql1 = "select top 1 * from sys.traces where is_default=0 and path ='" + traceFile
				+ ".trc' order by id desc";
		DTTable tb = DTTable.getJdbcTable(sql1, m_Conn);
		if (tb.getCount() > 0) {
			m_TraceId = tb.getCell(0, "id").toInt();
			LOGGER.info("Use exists traceId = {}", this.m_TraceId);
		} else {
			// [ @tracefile = ] Specifies the location and file name to which the trace will
			// be written.
			// trace_file is nvarchar(245) with no default.
			// trace_file can be either a local directory (such as
			// N'C:\MSSQL\Trace\trace.trc')
			// or a UNC to a share or path (N'\\Servername\Sharename\Directory\trace.trc').
			// SQL Server will append a .trc extension to all trace file names.

			// If the TRACE_FILE_ROLLOVER option and a max_file_size are specified,
			// SQL Server creates a new trace file when the original trace file grows to its
			// maximum size.
			// The new file has the same name as the original file, but _n is appended to
			// indicate its sequence,
			// starting with 1. For example, if the first trace file is named filename.trc,
			// the second trace file is named filename_1.trc.
			this.m_Conn.getRequestValue().addOrUpdateValue("trace_file", traceFile);

			// TRACE_FILE_ROLLOVER 2
			// Specifies that when the max_file_size is reached, the current trace file is
			// closed and a new file is created. All new records will be written to the new
			// file. The new file will have the same name as the previous file, but an
			// integer will be appended to indicate its sequence. For example, if the
			// original trace file is named filename.trc, the next trace file is named
			// filename_1.trc, the following trace file is filename_2.trc, and so on.
			// As more rollover trace files are created, the integer value appended to the
			// file name increases sequentially.
			// SQL Server uses the default value of max_file_size (5 MB) if this option is
			// specified without specifying a value for max_file_size.
			this.m_Conn.getRequestValue().addOrUpdateValue("options", 2, "int", 200);

			// [ @maxfilesize = ] max_file_size Specifies the maximum size in megabytes (MB)
			// a trace file can grow. max_file_size is bigint, with a default value of 5.
			// If this parameter is specified without the TRACE_FILE_ROLLOVER option, the
			// trace stops recording to the file when the disk space used exceeds the amount
			// specified by max_file_size.
			this.m_Conn.getRequestValue().addOrUpdateValue("maxfilesize", 10, "bigint", 200);// 10m

			// [ @stoptime = ] 'stop_time' Specifies the date and time the trace will be
			// stopped. stop_time is datetime, with a default of NULL. If NULL, the trace
			// runs until it is manually stopped or until the server shuts down.
			// If both stop_time and max_file_size are specified, and TRACE_FILE_ROLLOVER is
			// not specified, the trace tops when either the specified stop time or maximum
			// file size is reached. If stop_time, max_file_size, and TRACE_FILE_ROLLOVER
			// are specified, the trace stops at the specified stop time, assuming the trace
			// does not fill up the drive.
			long oneMinute = 60 * 1000;
			long oneHour = 60 * oneMinute;
			long oneDay = 24 * oneHour;
			Date stopTime = new Date(System.currentTimeMillis() + oneDay);
			this.m_Conn.getRequestValue().addOrUpdateValue("stoptime", stopTime, "date", 200);
			// [ @filecount = ] 'max_rollover_files' Specifies the maximum number or trace
			// files to be maintained with the same base filename. max_rollover_files is
			// int, greater than one. This parameter is valid only if the
			// TRACE_FILE_ROLLOVER option is specified. When max_rollover_files is
			// specified, SQL Server tries to maintain no more than max_rollover_files trace
			// files by deleting the oldest trace file before opening a new trace file. SQL
			// Server tracks the age of trace files by appending a number to the base file
			// name.
			this.m_Conn.getRequestValue().addOrUpdateValue("filecount", 3, "int", 200);

			String sql = "{@result_int_out = call sp_trace_create(@traceid_int_out, @options, @trace_file"
					+ ", @maxfilesize, @stoptime, @filecount)}";
			HashMap<String, Object> map = this.m_Conn.executeProcdure(sql);

			LOGGER.info("{}", sql);

			int result = UConvert.ToInt32(map.get("result_int_out".toUpperCase()).toString());
			if (result != 0) { // 失败
				throw new Exception("Failed to create trace(sp_trace_create), result = " + result);
			}
			m_TraceId = UConvert.ToInt32(map.get("traceid_int_out".toUpperCase()).toString());
			LOGGER.info("Create new traceId = {}", this.m_TraceId);
		}
	}

	/**
	 * 创建跟踪，live模式，通过resultSet不断读取返回数据
	 * 
	 * @throws Exception
	 */
	public void createTrace() throws Exception {
		// [ @tracefile = ] Specifies the location and file name to which the trace will
		// be written.
		// trace_file is nvarchar(245) with no default.
		// trace_file can be either a local directory (such as
		// N'C:\MSSQL\Trace\trace.trc')
		// or a UNC to a share or path (N'\\Servername\Sharename\Directory\trace.trc').
		// SQL Server will append a .trc extension to all trace file names.

		// If the TRACE_FILE_ROLLOVER option and a max_file_size are specified,
		// SQL Server creates a new trace file when the original trace file grows to its
		// maximum size.
		// The new file has the same name as the original file, but _n is appended to
		// indicate its sequence,
		// starting with 1. For example, if the first trace file is named filename.trc,
		// the second trace file is named filename_1.trc.
		this.m_Conn.getRequestValue().addOrUpdateValue("trace_file", null);
		// TRACE_FILE_ROLLOVER 2
		// Specifies that when the max_file_size is reached, the current trace file is
		// closed and a new file is created. All new records will be written to the new
		// file. The new file will have the same name as the previous file, but an
		// integer will be appended to indicate its sequence. For example, if the
		// original trace file is named filename.trc, the next trace file is named
		// filename_1.trc, the following trace file is filename_2.trc, and so on.
		// As more rollover trace files are created, the integer value appended to the
		// file name increases sequentially.
		// SQL Server uses the default value of max_file_size (5 MB) if this option is
		// specified without specifying a value for max_file_size.
		this.m_Conn.getRequestValue().addOrUpdateValue("options", 1, "int", 200);
		// [ @maxfilesize = ] max_file_size Specifies the maximum size in megabytes (MB)
		// a trace file can grow. max_file_size is bigint, with a default value of 5.
		// If this parameter is specified without the TRACE_FILE_ROLLOVER option, the
		// trace stops recording to the file when the disk space used exceeds the amount
		// specified by max_file_size.
		this.m_Conn.getRequestValue().addOrUpdateValue("maxfilesize", null, "bigint", 200);// 10m
		// [ @stoptime = ] 'stop_time' Specifies the date and time the trace will be
		// stopped. stop_time is datetime, with a default of NULL. If NULL, the trace
		// runs until it is manually stopped or until the server shuts down.
		// If both stop_time and max_file_size are specified, and TRACE_FILE_ROLLOVER is
		// not specified, the trace tops when either the specified stop time or maximum
		// file size is reached. If stop_time, max_file_size, and TRACE_FILE_ROLLOVER
		// are specified, the trace stops at the specified stop time, assuming the trace
		// does not fill up the drive.
		long oneMinute = 60 * 1000;
		long oneHour = 60 * oneMinute;
		long oneDay = 24 * oneHour;
		Date stopTime = new Date(System.currentTimeMillis() + oneDay);
		this.m_Conn.getRequestValue().addOrUpdateValue("stoptime", stopTime, "date", 200);
		// [ @filecount = ] 'max_rollover_files' Specifies the maximum number or trace
		// files to be maintained with the same base filename. max_rollover_files is
		// int, greater than one. This parameter is valid only if the
		// TRACE_FILE_ROLLOVER option is specified. When max_rollover_files is
		// specified, SQL Server tries to maintain no more than max_rollover_files trace
		// files by deleting the oldest trace file before opening a new trace file. SQL
		// Server tracks the age of trace files by appending a number to the base file
		// name.
		this.m_Conn.getRequestValue().addOrUpdateValue("filecount", null, "int", 200);

		String sql = "{@result_int_out = call sp_trace_create(@traceid_int_out, @options, @trace_file"
				+ ", @maxfilesize, @stoptime, @filecount)}";
		HashMap<String, Object> map = this.m_Conn.executeProcdure(sql);

		LOGGER.info("{} {}", sql, this.m_Conn);

		int result = UConvert.ToInt32(map.get("result_int_out".toUpperCase()).toString());
		if (result != 0) { // 失败
			throw new Exception("Failed to create trace(sp_trace_create), result = " + result);
		}
		m_TraceId = UConvert.ToInt32(map.get("traceid_int_out".toUpperCase()).toString());
		LOGGER.info("Create new traceId = {}", this.m_TraceId);
	}

	/**
	 * sp_trace_setstatus 设定跟踪状态
	 * 
	 * @param con
	 * @param status 0: stop, 1: start, 2:close
	 */
	private void controlTrace(DataConnection con, int status) {
		String sql = "exec sp_trace_setstatus " + m_TraceId + ", " + status;
		LOGGER.info("{} {}", sql, con);
		con.executeUpdateNoParameter(sql);
	}

	/**
	 * sp_trace_setstatus state = 2
	 * 
	 * @param con
	 */
	public void closeTrace(DataConnection con) {
		LOGGER.info("CLOSE trace, {} {}", this.m_TraceId, 2);
		controlTrace(con, 2);
		this.m_TraceId = 0;
		try {
			this.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * sp_trace_setstatus state = 0
	 * 
	 * @param con
	 */
	public void pauseTrace(DataConnection con) {
		LOGGER.info("PAUSE trace, {} {}", this.m_TraceId, 3);
		controlTrace(con, 3);
		try {
			this.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * sp_trace_setstatus state = 1
	 */
	public void startTrace() throws SQLException {
		LOGGER.info("START trace, {} {}", this.m_TraceId, 1);
		// if (m_currentsettings.EventsColumns.BatchCompleted)
		this.setEvent(TSQL.SQLBatchCompleted, ProfilerEventColumns.TextData, ProfilerEventColumns.LoginName,
				ProfilerEventColumns.CPU, ProfilerEventColumns.Reads, ProfilerEventColumns.Writes,
				ProfilerEventColumns.Duration, ProfilerEventColumns.SPID, ProfilerEventColumns.StartTime,
				ProfilerEventColumns.EndTime, ProfilerEventColumns.DatabaseName, ProfilerEventColumns.ApplicationName,
				ProfilerEventColumns.HostName);

		// if (m_currentsettings.EventsColumns.RPCCompleted)
		this.setEvent(StoredProcedures.RPCCompleted, ProfilerEventColumns.TextData, ProfilerEventColumns.LoginName,
				ProfilerEventColumns.CPU, ProfilerEventColumns.Reads, ProfilerEventColumns.Writes,
				ProfilerEventColumns.Duration, ProfilerEventColumns.SPID, ProfilerEventColumns.StartTime,
				ProfilerEventColumns.EndTime, ProfilerEventColumns.DatabaseName, ProfilerEventColumns.ObjectName,
				ProfilerEventColumns.ApplicationName, ProfilerEventColumns.HostName);

		if (!StringUtils.isBlank(this.profiler.getDatabase())) {
			setStringFilter(this.profiler.getDatabase(), StringFilterCondition.Like, ProfilerEventColumns.DatabaseName);
		}
		this.setFilter(ProfilerEventColumns.ApplicationName, LogicalOperators.AND, ComparisonOperators.NotLike,
				SqlServerProfiler.APPNAME);

		this.controlTrace(m_Conn, 1);
		this.createTraceGetdata();
		this.tryReadNext();
	}

	/**
	 * 返回trace的resultSet（纵向表），不会停止，一直输出数据
	 */
	private void createTraceGetdata() {
		// @traceid, @records
		String sql = "exec sp_trace_getdata " + m_TraceId + ", 0";
		LOGGER.info("{} {}", sql, m_Conn);
		this.m_Conn.executeQuery(sql);
		this.m_Reader = this.m_Conn.getLastResult().getResultSet();
	}

	public void setIntFilter(Integer value, IntFilterCondition condition, int column) throws Exception {
		if ((null != value)) {
			Long v = Long.parseLong(value.toString());
			this.setFilter(column, LogicalOperators.AND, condition.ordinal(), v);
		} // End if ((null != value))

	}

	public void setStringFilter(String value, StringFilterCondition condition, int column) {
		if (!StringUtils.isBlank(value)) {
			this.setFilter(column, LogicalOperators.AND,
					condition == StringFilterCondition.Like ? ComparisonOperators.Like : ComparisonOperators.NotLike,
					value);
		}
	}

	public boolean isTraceIsActive() {
		return m_LastRead;
	}

	public int getTraceId() {
		return m_TraceId;
	}

	private void initProfilerEventColumns() {
		/*
		 * select 'm_Delegates[ProfilerEventColumns.'+Name+'] = evt'+ case Type_Name
		 * when 'text' then 'String' when 'int' then 'Int' when 'bigint' then 'Long'
		 * when 'nvarchar' then 'String' when 'datetime' then 'DateTime' when 'image'
		 * then 'Byte' when 'uniqueidentifier' then 'Guid' end+';'
		 * 
		 * from sys.trace_columns order by trace_column_id
		 * 
		 */
		m_Delegates[ProfilerEventColumns.TextData] = evtString;
		m_Delegates[ProfilerEventColumns.BinaryData] = evtByte;
		m_Delegates[ProfilerEventColumns.DatabaseID] = evtInt;
		m_Delegates[ProfilerEventColumns.TransactionID] = evtLong;
		m_Delegates[ProfilerEventColumns.LineNumber] = evtInt;
		m_Delegates[ProfilerEventColumns.NTUserName] = evtString;
		m_Delegates[ProfilerEventColumns.NTDomainName] = evtString;
		m_Delegates[ProfilerEventColumns.HostName] = evtString;
		m_Delegates[ProfilerEventColumns.ClientProcessID] = evtInt;
		m_Delegates[ProfilerEventColumns.ApplicationName] = evtString;
		m_Delegates[ProfilerEventColumns.LoginName] = evtString;
		m_Delegates[ProfilerEventColumns.SPID] = evtInt;
		m_Delegates[ProfilerEventColumns.Duration] = evtLong;
		m_Delegates[ProfilerEventColumns.StartTime] = evtDateTime;
		m_Delegates[ProfilerEventColumns.EndTime] = evtDateTime;
		m_Delegates[ProfilerEventColumns.Reads] = evtLong;
		m_Delegates[ProfilerEventColumns.Writes] = evtLong;
		m_Delegates[ProfilerEventColumns.CPU] = evtInt;
		m_Delegates[ProfilerEventColumns.Permissions] = evtLong;
		m_Delegates[ProfilerEventColumns.Severity] = evtInt;
		m_Delegates[ProfilerEventColumns.EventSubClass] = evtInt;
		m_Delegates[ProfilerEventColumns.ObjectID] = evtInt;
		m_Delegates[ProfilerEventColumns.Success] = evtInt;
		m_Delegates[ProfilerEventColumns.IndexID] = evtInt;
		m_Delegates[ProfilerEventColumns.IntegerData] = evtInt;
		m_Delegates[ProfilerEventColumns.ServerName] = evtString;
		m_Delegates[ProfilerEventColumns.EventClass] = evtInt;
		m_Delegates[ProfilerEventColumns.ObjectType] = evtInt;
		m_Delegates[ProfilerEventColumns.NestLevel] = evtInt;
		m_Delegates[ProfilerEventColumns.State] = evtInt;
		m_Delegates[ProfilerEventColumns.Error] = evtInt;
		m_Delegates[ProfilerEventColumns.Mode] = evtInt;
		m_Delegates[ProfilerEventColumns.Handle] = evtInt;
		m_Delegates[ProfilerEventColumns.ObjectName] = evtString;
		m_Delegates[ProfilerEventColumns.DatabaseName] = evtString;
		m_Delegates[ProfilerEventColumns.FileName] = evtString;
		m_Delegates[ProfilerEventColumns.OwnerName] = evtString;
		m_Delegates[ProfilerEventColumns.RoleName] = evtString;
		m_Delegates[ProfilerEventColumns.TargetUserName] = evtString;
		m_Delegates[ProfilerEventColumns.DBUserName] = evtString;
		m_Delegates[ProfilerEventColumns.LoginSid] = evtByte;
		m_Delegates[ProfilerEventColumns.TargetLoginName] = evtString;
		m_Delegates[ProfilerEventColumns.TargetLoginSid] = evtByte;
		m_Delegates[ProfilerEventColumns.ColumnPermissions] = evtInt;
		m_Delegates[ProfilerEventColumns.LinkedServerName] = evtString;
		m_Delegates[ProfilerEventColumns.ProviderName] = evtString;
		m_Delegates[ProfilerEventColumns.MethodName] = evtString;
		m_Delegates[ProfilerEventColumns.RowCounts] = evtLong;
		m_Delegates[ProfilerEventColumns.RequestID] = evtInt;
		m_Delegates[ProfilerEventColumns.XactSequence] = evtLong;
		m_Delegates[ProfilerEventColumns.EventSequence] = evtLong;
		m_Delegates[ProfilerEventColumns.BigintData1] = evtLong;
		m_Delegates[ProfilerEventColumns.BigintData2] = evtLong;
		m_Delegates[ProfilerEventColumns.GUID] = evtGuid;
		m_Delegates[ProfilerEventColumns.IntegerData2] = evtInt;
		m_Delegates[ProfilerEventColumns.ObjectID2] = evtLong;
		m_Delegates[ProfilerEventColumns.Type] = evtInt;
		m_Delegates[ProfilerEventColumns.OwnerID] = evtInt;
		m_Delegates[ProfilerEventColumns.ParentName] = evtString;
		m_Delegates[ProfilerEventColumns.IsSystem] = evtInt;
		m_Delegates[ProfilerEventColumns.Offset] = evtInt;
		m_Delegates[ProfilerEventColumns.SourceDatabaseID] = evtInt;
		m_Delegates[ProfilerEventColumns.SqlHandle] = evtByte;
		m_Delegates[ProfilerEventColumns.SessionLoginName] = evtString;
		m_Delegates[ProfilerEventColumns.PlanHandle] = evtByte;
	}

	/**
	 * @return the m_Reader
	 */
	public ResultSet getReader() {
		return m_Reader;
	}

	/**
	 * @return the m_Conn
	 */
	public DataConnection getConn() {
		return m_Conn;
	}

	/**
	 * @param m_Conn the m_Conn to set
	 */
	public void setConn(DataConnection m_Conn) {
		this.m_Conn = m_Conn;
	}
}

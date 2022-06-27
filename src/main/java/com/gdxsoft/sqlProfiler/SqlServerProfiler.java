package com.gdxsoft.sqlProfiler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.gdxsoft.easyweb.conf.ConfSecurities;
import com.gdxsoft.easyweb.conf.ConnectionConfig;
import com.gdxsoft.easyweb.conf.ConnectionConfigs;
import com.gdxsoft.easyweb.data.DTTable;
import com.gdxsoft.easyweb.datasource.DataConnection;
import com.gdxsoft.easyweb.script.RequestValue;
import com.gdxsoft.easyweb.utils.UAes;
import com.gdxsoft.easyweb.utils.UFile;
import com.gdxsoft.easyweb.utils.UJSon;
import com.gdxsoft.easyweb.utils.UPath;
import com.gdxsoft.easyweb.utils.USnowflake;
import com.gdxsoft.easyweb.utils.Utils;
import com.gdxsoft.easyweb.utils.msnet.MTableStr;
import com.gdxsoft.sqlProfiler.helpers.HorizontalAlignment;
import com.gdxsoft.sqlProfiler.helpers.PerfColumn;
import com.gdxsoft.sqlProfiler.rawTraceReader.*;

public class SqlServerProfiler {
	public static final String SQL_LOG_NEW = "INSERT INTO TRACE_LOG("
			+ "  TL_ID, TS_ID, TL_TEXTDATA, TL_BINARYDATA, TL_DATABASEID"
			+ ", TL_TRANSACTIONID, TL_LINENUMBER, TL_NTUSERNAME, TL_NTDOMAINNAME, TL_HOSTNAME"
			+ ", TL_CLIENTPROCESSID, TL_APPLICATIONNAME, TL_LOGINNAME, TL_SPID, TL_DURATION, TL_STARTTIME"
			+ ", TL_ENDTIME, TL_READS, TL_WRITES, TL_CPU, TL_PERMISSIONS, TL_SEVERITY, TL_EVENTSUBCLASS"
			+ ", TL_OBJECTID, TL_SUCCESS, TL_INDEXID, TL_INTEGERDATA, TL_SERVERNAME, TL_EVENTCLASS"
			+ ", TL_OBJECTTYPE, TL_NESTLEVEL, TL_STATE, TL_ERROR, TL_MODE, TL_HANDLE, TL_OBJECTNAME"
			+ ", TL_DATABASENAME, TL_FILENAME, TL_OWNERNAME, TL_ROLENAME, TL_TARGETUSERNAME, TL_DBUSERNAME"
			+ ", TL_LOGINSID, TL_TARGETLOGINNAME, TL_TARGETLOGINSID, TL_COLUMNPERMISSIONS, TL_LINKEDSERVERNAME"
			+ ", TL_PROVIDERNAME, TL_METHODNAME, TL_ROWCOUNTS, TL_REQUESTID, TL_XACTSEQUENCE, TL_EVENTSEQUENCE"
			+ ", TL_BIGINTDATA1, TL_BIGINTDATA2, TL_GUID, TL_INTEGERDATA2, TL_OBJECTID2, TL_TYPE, TL_OWNERID"
			+ ", TL_PARENTNAME, TL_ISSYSTEM, TL_OFFSET, TL_SOURCEDATABASEID, TL_SQLHANDLE, TL_SESSIONLOGINNAME"
			+ ", TL_PLANHANDLE, TL_GROUPID)\n " // values
			+ "VALUES(@TL_ID, @TS_ID, @TL_TEXTDATA, @TL_BINARYDATA, @TL_DATABASEID"
			+ ", @TL_TRANSACTIONID, @TL_LINENUMBER, @TL_NTUSERNAME, @TL_NTDOMAINNAME, @TL_HOSTNAME"
			+ ", @TL_CLIENTPROCESSID, @TL_APPLICATIONNAME, @TL_LOGINNAME, @TL_SPID, @TL_DURATION, @TL_STARTTIME"
			+ ", @TL_ENDTIME, @TL_READS, @TL_WRITES, @TL_CPU, @TL_PERMISSIONS, @TL_SEVERITY, @TL_EVENTSUBCLASS"
			+ ", @TL_OBJECTID, @TL_SUCCESS, @TL_INDEXID, @TL_INTEGERDATA, @TL_SERVERNAME, @TL_EVENTCLASS"
			+ ", @TL_OBJECTTYPE, @TL_NESTLEVEL, @TL_STATE, @TL_ERROR, @TL_MODE, @TL_HANDLE, @TL_OBJECTNAME"
			+ ", @TL_DATABASENAME, @TL_FILENAME, @TL_OWNERNAME, @TL_ROLENAME, @TL_TARGETUSERNAME, @TL_DBUSERNAME"
			+ ", @TL_LOGINSID, @TL_TARGETLOGINNAME, @TL_TARGETLOGINSID, @TL_COLUMNPERMISSIONS, @TL_LINKEDSERVERNAME"
			+ ", @TL_PROVIDERNAME, @TL_METHODNAME, @TL_ROWCOUNTS, @TL_REQUESTID, @TL_XACTSEQUENCE, @TL_EVENTSEQUENCE"
			+ ", @TL_BIGINTDATA1, @TL_BIGINTDATA2, @TL_GUID, @TL_INTEGERDATA2, @TL_OBJECTID2, @TL_TYPE, @TL_OWNERID"
			+ ", @TL_PARENTNAME, @TL_ISSYSTEM, @TL_OFFSET, @TL_SOURCEDATABASEID, @TL_SQLHANDLE, @TL_SESSIONLOGINNAME"
			+ ", @TL_PLANHANDLE, @TL_GROUPID)";
	public static final String APPNAME = "com.gdxsoft.sqlProfiler";
	private static Logger LOGGER = LoggerFactory.getLogger(SqlServerProfiler.class);
	private static Map<Integer, SqlServerProfiler> instances = new ConcurrentHashMap<>();

	public static void removeInstance(int tsId) {
		if (!instances.containsKey(tsId)) {
			return;
		}
		SqlServerProfiler sp = instances.remove(tsId);
		sp.stopProfiling();

	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static SqlServerProfiler getInstance(String host, int port, String database, String username,
			String password) throws Exception {
		int id = (host + "_" + port + "_" + username + "_" + password + "_" + database).hashCode();
		if (instances.containsKey(id)) {
			return instances.get(id);
		}
		synchronized (instances) {
			if (instances.containsKey(id)) {
				return instances.get(id);
			}
			SqlServerProfiler sp = new SqlServerProfiler();
			sp.tsId = id;
			sp.connStr = "sqlprofiler_" + id;

			sp.init(host, port, database, username, password);

			instances.put(id, sp);
			return sp;
		}
	}

	public static String createConnStr(String host, int port, String database) {
		return "jdbc:sqlserver://" + host + ":" + port + ";TrustServerCertificate=True;DatabaseName=" + database
				+ ";applicationName=" + APPNAME;

	}

	/**
	 * 测试连接
	 * 
	 * @param host
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 * @return
	 */
	public static JSONObject testConnection(String host, int port, String database, String username, String password) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			return UJSon.rstFalse(e.getMessage());
		}
		String connStr = SqlServerProfiler.createConnStr(host, port, database);
		Connection con;
		try {
			con = DriverManager.getConnection(connStr, username, password);
		} catch (SQLException e) {
			return UJSon.rstFalse(e.getMessage());
		}

		
		 
		String cfgName ="test_sqlserver_connection";
		ConnectionConfig poolCfg = new ConnectionConfig();
		poolCfg.setName(cfgName);
		poolCfg.setType("MSSQL");
		poolCfg.setConnectionString(connStr);
		poolCfg.setSchemaName("dbo");

		ConnectionConfigs c1;
		try {
			c1 = ConnectionConfigs.instance();
			c1.put(cfgName, poolCfg);
		} catch ( Exception e) {
			return UJSon.rstFalse(e.getMessage());
		}  

		DataConnection cnn = new DataConnection();
		cnn.setConfigName(cfgName);
		cnn.setRequestValue(new RequestValue());
		cnn.getDataHelper().setConnection(con);

		return testConnection(cnn);
	}

	private static JSONObject testConnection(DataConnection cnn) {
		cnn.executeQueryNoParameter("select 1");
		if (cnn.getErrorMsg() != null) {
			cnn.close();
			return UJSon.rstFalse(cnn.getErrorMsg());
		}

		JSONObject result = UJSon.rstTrue();
		DatabaseMetaData dbmd;
		try {
			dbmd = cnn.getConnection().getMetaData();
			result.put("url", dbmd.getURL());
			result.put("product", dbmd.getDatabaseProductName());
			result.put("product_version", dbmd.getDatabaseProductVersion());
			result.put("driver", dbmd.getDriverName());
			result.put("driver_version", dbmd.getDriverName());
		} catch (SQLException e) {
			return UJSon.rstFalse(cnn.getErrorMsg());
		} finally {
			cnn.close();
		}
		return result;
	}

	/**
	 * 测试连接
	 * 
	 * @param tsId
	 * @return
	 * @throws Exception
	 */
	public static JSONObject testConnection(int tsId) {
		ConfSecurities.getInstance();
		// 启动HsqlDb
		try {
			HSqlDbServer.getInstance();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			return UJSon.rstFalse(e.getMessage());
		}

		String sql = "select * from TRACE_SERVER where ts_id = " + tsId;
		DTTable tb = DTTable.getJdbcTable(sql, HSqlDbServer.CONN_STR);
		if (tb.getCount() == 0) {
			LOGGER.error("配置信息不存在");
			return UJSon.rstFalse("配置信息不存在");

		}
		String password, server, database, username;
		int port;
		try {
			server = tb.getCell(0, "TS_HOST").toString();
			port = tb.getCell(0, "TS_PORT").toInt();
			database = tb.getCell(0, "TS_DATABASE").toString();
			username = tb.getCell(0, "TS_UID").toString();
			password = tb.getCell(0, "TS_PWD").toString();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			return UJSon.rstFalse(e.getMessage());
		}

		if (password != null && password.trim().length() > 0) {
			try {
				password = UAes.defaultDecrypt(password);
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage());
				return UJSon.rstFalse(e.getMessage());
			}
		}

		return SqlServerProfiler.testConnection(server, port, database, username, password);

	}

	/**
	 * 
	 * @param tsId
	 * @return
	 * @throws Exception
	 */
	public static SqlServerProfiler getInstance(int tsId) throws Exception {
		if (instances.containsKey(tsId)) {
			return instances.get(tsId);
		}
		ConfSecurities.getInstance();
		// 启动HsqlDb
		HSqlDbServer.getInstance();

		String sql = "select * from TRACE_SERVER where ts_id = " + tsId;
		DTTable tb = DTTable.getJdbcTable(sql, HSqlDbServer.CONN_STR);
		if (tb.getCount() == 0) {
			LOGGER.error("配置信息不存在");
			throw new Exception("配置信息不存在");
		}
		String server = tb.getCell(0, "TS_HOST").toString();
		int port = tb.getCell(0, "TS_PORT").toInt();
		String database = tb.getCell(0, "TS_DATABASE").toString();
		String username = tb.getCell(0, "TS_UID").toString();
		String password = tb.getCell(0, "TS_PWD").toString();
		if (password != null && password.trim().length() > 0) {
			password = UAes.defaultDecrypt(password);
		}

		synchronized (instances) {
			if (instances.containsKey(tsId)) {
				return instances.get(tsId);
			}
			SqlServerProfiler sp = new SqlServerProfiler();
			sp.tsId = tsId;
			sp.connStr = "sqlprofiler_" + tsId;

			sp.init(server, port, database, username, password);

			instances.put(tsId, sp);
			return sp;
		}
	}

	private String connStr = "sqlprofiler";
	protected ProfilingStateEnum m_ProfilingState;

	protected DataConnection m_Conn;
	protected RawTraceReader m_Rdr;
	protected boolean m_NeedStop;
	protected Timer m_timer;
	protected ConcurrentLinkedQueue<ProfilerEvent> m_events;
	protected List<PerfColumn> m_columns;

	protected Thread m_Thr;

	// protected YukonLexer m_Lex;
	protected boolean m_isWindows; //
	protected boolean consoleMode; // 是否为窗口模式

	protected ProfilerEvent m_EventStarted = new ProfilerEvent();
	protected ProfilerEvent m_EventStopped = new ProfilerEvent();
	protected ProfilerEvent m_EventPaused = new ProfilerEvent();

	private String server;
	private String username;
	private String password;
	private String database;
	private int port = 1433;
	private int tsId;

	private boolean initialized = false;
	private String traceFileName;
	private String connUrl;

	public enum StringFilterCondition {
		Like, NotLike
	} // End Enum StringFilterCondition

	public enum IntFilterCondition {
		Equal, NotEqual, GreaterThan, LessThan
	} // End Enum IntFilterCondition

	public SqlServerProfiler() {
		UPath.initPath();
	}

	public void init(String server, int port, String database, String username, String password) throws Exception {
		if (initialized) {
			return;
		}

		this.server = server;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;

		if (!this.initSqlServerTraceConnPool()) {
			throw new Exception("无法初始化数据库连接");
		}
		this.initEventColumns();

		initialized = true;
	}

	private void initEventColumns() {
		this.m_columns = new ArrayList<PerfColumn>();
		this.m_columns.add(new PerfColumn("Event Class", ProfilerEventColumns.EventClass, 122));
		this.m_columns.add(new PerfColumn("Text Data", ProfilerEventColumns.TextData, 255));
		this.m_columns.add(new PerfColumn("Login Name", ProfilerEventColumns.LoginName, 79));
		this.m_columns.add(new PerfColumn("CPU", ProfilerEventColumns.CPU, 82, HorizontalAlignment.Right, "#,0"));
		this.m_columns.add(new PerfColumn("Reads", ProfilerEventColumns.Reads, 78, HorizontalAlignment.Right, "#,0"));
		this.m_columns.add(new PerfColumn("Writes", ProfilerEventColumns.Writes, 78, HorizontalAlignment.Right, "#,0"));
		this.m_columns.add(
				new PerfColumn("Duration, ms", ProfilerEventColumns.Duration, 82, HorizontalAlignment.Right, "#,0"));
		this.m_columns.add(new PerfColumn("SPID", ProfilerEventColumns.SPID, 50, HorizontalAlignment.Right, null));

		// if (m_currentsettings.EventsColumns.StartTime)
		m_columns.add(
				new PerfColumn("Start time", ProfilerEventColumns.StartTime, 140, null, "yyyy-MM-ddThh:mm:ss.ffff"));
		// if (m_currentsettings.EventsColumns.EndTime)
		m_columns.add(new PerfColumn("End time", ProfilerEventColumns.EndTime, 140, null, "yyyy-MM-ddThh:mm:ss.ffff"));
		// if (m_currentsettings.EventsColumns.DatabaseName)
		m_columns.add(new PerfColumn("DatabaseName", ProfilerEventColumns.DatabaseName, 70));
		// if (m_currentsettings.EventsColumns.ObjectName)
		m_columns.add(new PerfColumn("Object name", ProfilerEventColumns.ObjectName, 70));
		// if (m_currentsettings.EventsColumns.ApplicationName)
		m_columns.add(new PerfColumn("Application name", ProfilerEventColumns.ApplicationName, 70));
		// if (m_currentsettings.EventsColumns.HostName)
		m_columns.add(new PerfColumn("Host name", ProfilerEventColumns.HostName, 70));
	}

	/**
	 * Start trace
	 * 
	 * @throws SQLException
	 */
	public void startProfiling() throws Exception {
		this.m_events = new ConcurrentLinkedQueue<ProfilerEvent>();

		// this.m_Conn = getConnection();
		this.m_Conn = this.createSqlServerReaderConnection();

		this.m_Rdr = new RawTraceReader(m_Conn);
		this.m_Rdr.profiler = this;

		this.m_Rdr.createTrace();

		this.m_Rdr.startTrace();
		this.m_NeedStop = false;

		// 启动读取线程，不断调用 RawTraceReader.next()，获取跟踪数据
		this.m_Thr = new ProfilerThread(this);
		this.m_Thr.start();

		this.m_ProfilingState = ProfilingStateEnum.psProfiling;

		if (this.consoleMode && this.m_timer == null) {
			this.m_timer = new Timer();
			TimerElapsed te = new TimerElapsed(this);
			this.m_timer.schedule(te, 1000L, 1000L);
		}

		newEventArrived(m_EventStarted, true);
	}

	/**
	 * Pause trace
	 */
	public void pauseProfiling() {
		if (this.m_ProfilingState != ProfilingStateEnum.psProfiling)
			return;

		try {
			DataConnection cnn = getConnection();
			this.m_Rdr.pauseTrace(cnn);
			cnn.close();
		} catch (Exception err) {
			LOGGER.warn("close cnn {}", err.getMessage());
		}
		this.m_ProfilingState = ProfilingStateEnum.psPaused;
		newEventArrived(m_EventPaused, true);
	}

	/**
	 * Resume
	 * 
	 * @throws SQLException
	 */
	public void resumeProfiling() throws Exception {
		// 停止或跟踪中，不需要启动
		if (this.m_ProfilingState == ProfilingStateEnum.psStopped
				|| this.m_ProfilingState == ProfilingStateEnum.psProfiling)
			return;
		// 暂停后，关闭所有连接，所以要创建新的连接
		this.m_Conn = this.createSqlServerReaderConnection();

		this.m_Rdr.setConn(m_Conn);
		this.m_Rdr.startTrace();
		this.m_ProfilingState = ProfilingStateEnum.psProfiling;
		newEventArrived(m_EventPaused, true);
	}

	/**
	 * Stop trace
	 */
	public void stopProfiling() {
		if (this.m_ProfilingState == ProfilingStateEnum.psStopped)
			return;
		this.m_NeedStop = true;

		if (this.m_Rdr == null) {
			this.m_ProfilingState = ProfilingStateEnum.psStopped;
			newEventArrived(m_EventStopped, true);

			return;
		}

		try {
			DataConnection cnn = getConnection();
			this.m_Rdr.pauseTrace(cnn);
			this.m_Rdr.closeTrace(cnn);
			cnn.close();

			this.m_Rdr.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (this.m_Conn != null) {
				this.m_Conn.close();
			}
		}

		this.m_ProfilingState = ProfilingStateEnum.psStopped;
		newEventArrived(m_EventStopped, true);
	}

	/**
	 * 从 sqlserver数据库的 sys.traces获取状态
	 * 
	 * @return
	 */
	public JSONObject getTraceStatusFromSysTraces() {
		if (this.getRdr() == null || this.getRdr().getTraceId() == 0) {
			return UJSon.rstTrue("实例没有启动").put("status", -2);
		}
		String sql = "select * from sys.traces where id=" + this.getRdr().getTraceId();
		DTTable tb = DTTable.getJdbcTable(sql, this.connStr);

		if (tb.getCount() == 0) {
			return UJSon.rstTrue("跟踪已经删除id=" + this.getRdr().getTraceId()).put("status", -1);
		} else {
			JSONObject result = tb.getRow(0).toJson();
			UJSon.rstSetTrue(result, null);
			return result;
		}
	}

	protected String getEventCaption(ProfilerEvent evt) {
		if (evt == m_EventStarted)
			return "Trace started";

		if (evt == m_EventPaused)
			return "Trace paused";

		if (evt == m_EventStopped) {

			return "Trace stopped";
		}

		return ProfilerEvents.Names[evt.getEventClass()];
	}

	public String exportRecords() throws JSONException, IOException {
		RequestValue rv = new RequestValue();
		rv.addOrUpdateValue("TS_ID", this.tsId, "int", 100);
		String sql = "select * from TRACE_LOG where ts_id=@ts_id order by tl_id";
		DTTable tb = DTTable.getJdbcTable(sql, HSqlDbServer.CONN_STR, rv);

		String file = HSqlDbServer.WORK_PATH + File.separator + "export_" + this.tsId + ".json";
		UFile.createNewTextFile(file, tb.toJSONArray().toString(2));
		return file;

	}

	public void truncateRecords() throws JSONException, IOException {
		String sql = "truncate table TRACE_LOG";
		DataConnection.updateAndClose(sql, HSqlDbServer.CONN_STR, null);
	}

	public void recordToDb(ProfilerEvent evt) {
		// Blobs and Clobs
		// HSQLDB is the only SQL open source database that supports a dedicated LOB store. Blobs and clobs can be very
		// large and benefit from a separate store that avoids mixing their data with row data which is not too large.
		// Internal database tables are used for the LOB catalog. Therefore each access to a LOB has the overhead of
		// catalog lookup. This overhead is justified when the stored LOBs are large. HSQLDB supports long VARCHAR and
		// VARBINARY columns that can be used instead of CLOB and BLOB especially when the average lob size is below 32
		// KB. These types do not have the LOB catalog overhead.

		int k32 = 32 * 1024; // 32k - 避免使用 Blob和Clob，会造成 .lob文件很大 ，普普通通就4-5个G
		RequestValue rv = new RequestValue();
		rv.addOrUpdateValue("TS_ID", this.tsId, "int", 100);
		rv.addOrUpdateValue("TL_ID", USnowflake.nextId(), "bigint", 100);

		rv.addOrUpdateValue("TL_TEXTDATA", evt.getTextData(), "String", k32);

		rv.addOrUpdateValue("TL_DURATION", evt.getDuration(), "bigint", 100);
		rv.addOrUpdateValue("TL_READS", evt.getReads(), "bigint", 100);
		rv.addOrUpdateValue("TL_Writes", evt.getWrites(), "bigint", 100);
		rv.addOrUpdateValue("TL_ROWCOUNTS", evt.getRowCounts(), "bigint", 100);
		rv.addOrUpdateValue("TL_TRANSACTIONID", evt.getTransactionID(), "bigint", 100);
		rv.addOrUpdateValue("TL_BigintData1", evt.getBigintData1(), "bigint", 100);
		rv.addOrUpdateValue("TL_BigintData2", evt.getBigintData2(), "bigint", 100);
		rv.addOrUpdateValue("TL_EventSequence", evt.getEventSequence(), "bigint", 100);
		rv.addOrUpdateValue("TL_XactSequence", evt.getXactSequence(), "bigint", 100);
		rv.addOrUpdateValue("TL_Permissions", evt.getPermissions(), "bigint", 100);
		rv.addOrUpdateValue("TL_ObjectID2", evt.getObjectID2(), "bigint", 100);

		rv.addOrUpdateValue("TL_STARTTIME", evt.getStartTime(), "date", 100);
		rv.addOrUpdateValue("TL_ENDTIME", evt.getEndTime(), "date", 100);

		rv.addOrUpdateValue("TL_ApplicationName", evt.getApplicationName());
		rv.addOrUpdateValue("TL_HostName", evt.getHostName());
		rv.addOrUpdateValue("TL_NTDomainName", evt.getNTDomainName());
		rv.addOrUpdateValue("TL_NTUserName", evt.getNTUserName());
		rv.addOrUpdateValue("TL_ServerName", evt.getServerName());
		rv.addOrUpdateValue("TL_LoginName", evt.getLoginName());
		rv.addOrUpdateValue("TL_DatabaseName", evt.getDatabaseName());
		rv.addOrUpdateValue("TL_FileName", evt.getFileName());
		rv.addOrUpdateValue("TL_TargetLoginName", evt.getTargetLoginName());
		rv.addOrUpdateValue("TL_TargetUserName", evt.getTargetUserName());
		rv.addOrUpdateValue("TL_LinkedServerName", evt.getLinkedServerName());
		rv.addOrUpdateValue("TL_SessionLoginName", evt.getSessionLoginName());
		rv.addOrUpdateValue("TL_RoleName", evt.getRoleName());
		rv.addOrUpdateValue("TL_ProviderName", evt.getProviderName());
		rv.addOrUpdateValue("TL_ParentName", evt.getParentName());
		rv.addOrUpdateValue("TL_OwnerName", evt.getOwnerName());
		rv.addOrUpdateValue("TL_ObjectName", evt.getObjectName());
		rv.addOrUpdateValue("TL_MethodName", evt.getMethodName());
		rv.addOrUpdateValue("TL_LoginName", evt.getLoginName());
		rv.addOrUpdateValue("TL_DBUserName", evt.getDBUserName());

		rv.addOrUpdateValue("TL_CPU", evt.getCPU(), "int", 100);
		rv.addOrUpdateValue("TL_SPID", evt.getSPID(), "int", 100);
		rv.addOrUpdateValue("TL_DatabaseID", evt.getDatabaseID(), "int", 100);
		rv.addOrUpdateValue("TL_EVENTCLASS", evt.getEventClass(), "int", 100);
		rv.addOrUpdateValue("TL_EventSubClass", evt.getEventSubClass(), "int", 100);
		rv.addOrUpdateValue("TL_IsSystem", evt.getIsSystem(), "int", 100);
		rv.addOrUpdateValue("TL_Error", evt.getError(), "int", 100);
		rv.addOrUpdateValue("TL_STATE", evt.getState(), "int", 100);
		rv.addOrUpdateValue("TL_SUCCESS", evt.getSuccess(), "int", 100);
		rv.addOrUpdateValue("TL_INDEXID", evt.getIndexID(), "int", 100);
		rv.addOrUpdateValue("TL_NESTLEVEL", evt.getNestLevel(), "int", 100);
		rv.addOrUpdateValue("TL_TYPE", evt.getType(), "int", 100);
		rv.addOrUpdateValue("TL_ClientProcessID", evt.getClientProcessID(), "int", 100);
		rv.addOrUpdateValue("TL_IntegerData", evt.getIntegerData(), "int", 100);
		rv.addOrUpdateValue("TL_IntegerData2", evt.getIntegerData2(), "int", 100);
		rv.addOrUpdateValue("TL_Handle", evt.getHandle(), "int", 100);
		rv.addOrUpdateValue("TL_LineNumber", evt.getLineNumber(), "int", 100);
		rv.addOrUpdateValue("TL_SourceDatabaseID", evt.getSourceDatabaseID(), "int", 100);
		rv.addOrUpdateValue("TL_Severity", evt.getSeverity(), "int", 100);
		rv.addOrUpdateValue("TL_RequestID", evt.getRequestID(), "int", 100);
		rv.addOrUpdateValue("TL_OwnerID", evt.getOwnerID(), "int", 100);
		rv.addOrUpdateValue("TL_Offset", evt.getOffset(), "int", 100);
		rv.addOrUpdateValue("TL_ObjectType", evt.getObjectType(), "int", 100);
		rv.addOrUpdateValue("TL_ObjectID", evt.getObjectID(), "int", 100);
		rv.addOrUpdateValue("TL_Mode", evt.getMode(), "int", 100);
		rv.addOrUpdateValue("TL_ColumnPermissions", evt.getColumnPermissions(), "int", 100);

		rv.addOrUpdateValue("TL_LOGINSID", evt.getLoginSid(), "binary", k32);
		rv.addOrUpdateValue("TL_BinaryData", evt.getBinaryData(), "binary", k32);
		rv.addOrUpdateValue("TL_TargetLoginSid", evt.getTargetLoginSid(), "binary", k32);
		rv.addOrUpdateValue("TL_PlanHandle", evt.getPlanHandle(), "binary", k32);

		rv.addOrUpdateValue("TL_GUID", evt.getGUID());

		DataConnection.updateAndClose(SQL_LOG_NEW, HSqlDbServer.CONN_STR, rv);
	}

	protected void newEventArrived(ProfilerEvent evt, boolean last) {
		if (!this.isConsoleMode()) {
			return;
		}
		String caption = getEventCaption(evt);
		// LOGGER.debug(caption);
		System.out.println(caption);
		String td = evt.getTextData();
		if (StringUtils.isNotBlank(td)) {
			// LOGGER.debug(td);
			System.out.println(td);
		}

	}

	protected DataConnection getConnection() throws Exception {
		DataConnection cnn = new DataConnection();
		cnn.setConfigName(connStr);
		cnn.setRequestValue(new RequestValue());
		if (cnn.connect()) {
			LOGGER.debug("Create a connection {}", cnn.getConnection());
			return cnn;
		} else {
			throw new Exception("Get connection fail");
		}
	}

	private DataConnection createSqlServerReaderConnection() throws Exception {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		Connection con = DriverManager.getConnection(connUrl + "_data", this.username, this.password);
		DataConnection cnn = new DataConnection();
		cnn.setConfigName(connStr);
		cnn.setRequestValue(new RequestValue());
		cnn.getDataHelper().setConnection(con);

		LOGGER.debug("Create none pool connection {}", con);
		return cnn;
	}

	public void clearProfilers() {
		StringBuilder sb = new StringBuilder();
		sb.append("DECLARE @trace_id AS integer; \n");
		sb.append("DECLARE @trace_iterator AS CURSOR; \n");
		sb.append("\n");
		sb.append("SET @trace_iterator = CURSOR FOR \n");
		sb.append("( \n");
		sb.append("	SELECT id FROM sys.traces WHERE is_default <> 1 \n");
		sb.append("); \n");
		sb.append("\n");
		sb.append("OPEN @trace_iterator; \n");
		sb.append("FETCH NEXT FROM @trace_iterator INTO @trace_id; \n");
		sb.append("\n");
		sb.append("WHILE @@FETCH_STATUS = 0 \n");
		sb.append("BEGIN \n");
		// -- 0: Stops the specified trace.
		// -- 1: Starts the specified trace.
		// -- 2: Closes the specified trace and deletes its definition from the server
		sb.append("	EXEC sp_trace_setstatus @trace_id, 0; \n");
		sb.append("	EXEC sp_trace_setstatus @trace_id, 2; \n");
		sb.append("\n");
		sb.append("	FETCH NEXT FROM @trace_iterator INTO @trace_id; \n");
		sb.append("END \n");
		sb.append(" \n");
		sb.append("CLOSE @trace_iterator; \n");
		sb.append("DEALLOCATE @trace_iterator; \n");

		DataConnection cnn = null;
		try {
			cnn = getConnection();
			cnn.executeUpdateNoParameter(sb.toString());
		} catch (Exception e) {
			LOGGER.error("clearProfilers {}", e.getMessage());
		} finally {
			if (cnn != null) {
				cnn.close();
			}
		}

	}

	private boolean initSqlServerTraceConnPool() throws ParserConfigurationException, SAXException, IOException {
		ConnectionConfigs c1 = ConnectionConfigs.instance();
		// 避免 unable to find valid certification path to requested target
		// TrustServerCertificate=True
		this.connUrl = SqlServerProfiler.createConnStr(server, port, database);

		this.traceFileName = APPNAME + "." + this.tsId + "." + Utils.md5(connUrl);

		ConnectionConfig poolCfg = new ConnectionConfig();
		poolCfg.setName(connStr);
		poolCfg.setType("MSSQL");
		poolCfg.setConnectionString(connStr);
		poolCfg.setSchemaName("dbo");

		MTableStr poolParams = new MTableStr();
		poolParams.put("driverClassName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		poolParams.put("url", connUrl);

		poolParams.put("username", this.username);
		poolParams.put("password", this.password);

		poolParams.put("maxActive", 10);
		poolParams.put("maxIdle", 100);

		poolCfg.setPool(poolParams);
		c1.put(connStr, poolCfg);
		LOGGER.debug("Create pool {} {}", connStr, connUrl);

		try {
			DataConnection cnn = this.getConnection();
			JSONObject result = testConnection(cnn);
			LOGGER.info("Test connection: {}", result.toString(2));
			return true;
		} catch (Exception err) {
			LOGGER.error(err.getLocalizedMessage());
			return false;
		}

	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the database
	 */
	public String getDdatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 是否初始化完毕
	 * 
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * @return the tsId
	 */
	public int getTsId() {
		return tsId;
	}

	/**
	 * @param tsId the tsId to set
	 */
	public void setTsId(int tsId) {
		this.tsId = tsId;
	}

	/**
	 * @return the lOGGER
	 */
	public static Logger getLOGGER() {
		return LOGGER;
	}

	/**
	 * @return the instances
	 */
	public static Map<Integer, SqlServerProfiler> getInstances() {
		return instances;
	}

	/**
	 * @return the connStr
	 */
	public String getConnStr() {
		return connStr;
	}

	/**
	 * @return the m_ProfilingState
	 */
	public ProfilingStateEnum getProfilingState() {
		return m_ProfilingState;
	}

	/**
	 * @return the m_Rdr
	 */
	public RawTraceReader getRdr() {
		return m_Rdr;
	}

	/**
	 * @return the m_NeedStop
	 */
	public boolean isNeedStop() {
		return m_NeedStop;
	}

	/**
	 * @return the m_timer
	 */
	public Timer getTimer() {
		return m_timer;
	}

	/**
	 * @return the m_events
	 */
	public ConcurrentLinkedQueue<ProfilerEvent> getEvents() {
		return m_events;
	}

	/**
	 * @return the m_columns
	 */
	public List<PerfColumn> getColumns() {
		return m_columns;
	}

	/**
	 * @return the m_Thr
	 */
	public Thread getThr() {
		return m_Thr;
	}

	/**
	 * @return the m_isWindows
	 */
	public boolean isWindows() {
		return m_isWindows;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param m_NeedStop the m_NeedStop to set
	 */
	public void setNeedStop(boolean m_NeedStop) {
		this.m_NeedStop = m_NeedStop;
	}

	/**
	 * @return the traceFileName
	 */
	public String getTraceFileName() {
		return traceFileName;
	}

	/**
	 * @return the consoleMode
	 */
	public boolean isConsoleMode() {
		return consoleMode;
	}

	/**
	 * @param consoleMode the consoleMode to set
	 */
	public void setConsoleMode(boolean consoleMode) {
		this.consoleMode = consoleMode;
	}
}

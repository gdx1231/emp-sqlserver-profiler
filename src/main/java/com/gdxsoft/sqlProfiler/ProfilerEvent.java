package com.gdxsoft.sqlProfiler;

import java.util.Date;

public class ProfilerEvent {
	public Object[] m_Events = new Object[65];
	public long m_ColumnMask;
	// ReSharper disable UnusedMember.Global
	// ReSharper disable InconsistentNaming

	/*
	 * select 'case ProfilerEventColumns.'+Name + ': ' + case when row_number()
	 * over(partition by Type_Name order by trace_column_id desc) = 1 then
	 * 
	 * ' return Get'+ case Type_Name when 'text' then 'String' when 'int' then 'Int'
	 * when 'bigint' then 'Long' when 'nvarchar' then 'String' when 'datetime' then
	 * 'DateTime' when 'image' then 'Byte' when 'uniqueidentifier' then 'Guid' end
	 * +'(idx); ' else '' end from sys.trace_columns order by
	 * Type_Name,trace_column_id
	 */

	public String getFormattedData(int idx, String format) {
		switch (ProfilerEventColumns.ProfilerColumnDataTypes[idx]) {
		case Long:
			return getLong(idx) + "";
		case DateTime:
			Date d = getDateTime(idx);
			return 1 == d.getYear() ? "" : d.toString();
		case Byte:
			return getByte(idx).toString();
		case Int:
			return getInt(idx) + "";
		case String:
			return getString(idx);
		case Guid:
			return GetGuid(idx).toString();
		}
		return null;
	}

	private int getInt(int idx) {
		if (!ColumnIsSet(idx))
			return 0;
		return m_Events[idx] == null ? 0 : Integer.parseInt(m_Events[idx].toString());
	}

	private long getLong(int idx) {
		if (!ColumnIsSet(idx))
			return 0;
		return m_Events[idx] == null ? 0 : Long.parseLong(m_Events[idx].toString());
	}

	private String getString(int idx) {
		if (!ColumnIsSet(idx))
			return "";
		return m_Events[idx] == null ? "" : (String) m_Events[idx];
	}

	private byte[] getByte(int idx) {
		if (!ColumnIsSet(idx))
			return null;
		return (byte[]) m_Events[idx];
	}

	private Date getDateTime(int idx) {
		if (!ColumnIsSet(idx))
			return null;
		return (Date) m_Events[idx];
	}

	private java.util.UUID GetGuid(int idx) {
		if (!ColumnIsSet(idx))
			return null;
		return (java.util.UUID) m_Events[idx];
	}

	// ReSharper disable MemberCanBePrivate.Global
	public boolean ColumnIsSet(int columnId)
	// ReSharper restore MemberCanBePrivate.Global
	{
		return (m_ColumnMask & (1L << columnId)) != 0;
	}

	/*
	 * select 'public '+case Type_Name when 'text' then 'String' when 'int' then
	 * 'int' when 'bigint' then 'long' when 'nvarchar' then 'String' when 'datetime'
	 * then 'DateTime' when 'image' then 'byte[]' when 'uniqueidentifier' then
	 * 'GUID' end +' '+Name + '{ return Get'+ case Type_Name when 'text' then
	 * 'String' when 'int' then 'Int' when 'bigint' then 'Long' when 'nvarchar' then
	 * 'String' when 'datetime' then 'DateTime' when 'image' then 'Byte' when
	 * 'uniqueidentifier' then 'Guid' end +'(ProfilerEventColumns.'+Name+');}' from
	 * sys.trace_columns order by trace_column_id
	 * 
	 * 
	 */

	public String getTextData() {
		return getString(ProfilerEventColumns.TextData);
	}

	public byte[] getBinaryData() {
		return getByte(ProfilerEventColumns.BinaryData);
	}

	public int getDatabaseID() {
		return getInt(ProfilerEventColumns.DatabaseID);
	}

	public long getTransactionID() {
		return getLong(ProfilerEventColumns.TransactionID);
	}

	public int getLineNumber() {
		return getInt(ProfilerEventColumns.LineNumber);
	}

	public String getNTUserName() {
		return getString(ProfilerEventColumns.NTUserName);
	}

	public String getNTDomainName() {
		return getString(ProfilerEventColumns.NTDomainName);
	}

	public String getHostName() {
		return getString(ProfilerEventColumns.HostName);
	}

	public int getClientProcessID() {
		return getInt(ProfilerEventColumns.ClientProcessID);
	}

	public String getApplicationName() {
		return getString(ProfilerEventColumns.ApplicationName);
	}

	public String getLoginName() {
		return getString(ProfilerEventColumns.LoginName);
	}

	public int getSPID() {
		return getInt(ProfilerEventColumns.SPID);
	}

	public long getDuration() {
		return getLong(ProfilerEventColumns.Duration);
	}

	public Date getStartTime() {
		return getDateTime(ProfilerEventColumns.StartTime);
	}

	public Date getEndTime() {
		return getDateTime(ProfilerEventColumns.EndTime);
	}

	public long getReads() {
		return getLong(ProfilerEventColumns.Reads);
	}

	public long getWrites() {
		return getLong(ProfilerEventColumns.Writes);
	}

	public int getCPU() {
		return getInt(ProfilerEventColumns.CPU);
	}

	public long getPermissions() {
		return getLong(ProfilerEventColumns.Permissions);
	}

	public int getSeverity() {
		return getInt(ProfilerEventColumns.Severity);
	}

	public int getEventSubClass() {
		return getInt(ProfilerEventColumns.EventSubClass);
	}

	public int getObjectID() {
		return getInt(ProfilerEventColumns.ObjectID);
	}

	public int getSuccess() {
		return getInt(ProfilerEventColumns.Success);
	}

	public int getIndexID() {
		return getInt(ProfilerEventColumns.IndexID);
	}

	public int getIntegerData() {
		return getInt(ProfilerEventColumns.IntegerData);
	}

	public String getServerName() {
		return getString(ProfilerEventColumns.ServerName);
	}

	public int getEventClass() {
		return getInt(ProfilerEventColumns.EventClass);
	}

	public int getObjectType() {
		return getInt(ProfilerEventColumns.ObjectType);
	}

	public int getNestLevel() {
		return getInt(ProfilerEventColumns.NestLevel);
	}

	public int getState() {
		return getInt(ProfilerEventColumns.State);
	}

	public int getError() {
		return getInt(ProfilerEventColumns.Error);
	}

	public int getMode() {
		return getInt(ProfilerEventColumns.Mode);
	}

	public int getHandle() {
		return getInt(ProfilerEventColumns.Handle);
	}

	public String getObjectName() {
		return getString(ProfilerEventColumns.ObjectName);
	}

	public String getDatabaseName() {
		return getString(ProfilerEventColumns.DatabaseName);
	}

	public String getFileName() {
		return getString(ProfilerEventColumns.FileName);
	}

	public String getOwnerName() {
		return getString(ProfilerEventColumns.OwnerName);
	}

	public String getRoleName() {
		return getString(ProfilerEventColumns.RoleName);
	}

	public String getTargetUserName() {
		return getString(ProfilerEventColumns.TargetUserName);
	}

	public String getDBUserName() {
		return getString(ProfilerEventColumns.DBUserName);
	}

	public byte[] getLoginSid() {
		return getByte(ProfilerEventColumns.LoginSid);
	}

	public String getTargetLoginName() {
		return getString(ProfilerEventColumns.TargetLoginName);
	}

	public byte[] getTargetLoginSid() {
		return getByte(ProfilerEventColumns.TargetLoginSid);
	}

	public int getColumnPermissions() {
		return getInt(ProfilerEventColumns.ColumnPermissions);
	}

	public String getLinkedServerName() {
		return getString(ProfilerEventColumns.LinkedServerName);
	}

	public String getProviderName() {
		return getString(ProfilerEventColumns.ProviderName);
	}

	public String getMethodName() {
		return getString(ProfilerEventColumns.MethodName);
	}

	public long getRowCounts() {
		return getLong(ProfilerEventColumns.RowCounts);
	}

	public int getRequestID() {
		return getInt(ProfilerEventColumns.RequestID);
	}

	public long getXactSequence() {
		return getLong(ProfilerEventColumns.XactSequence);
	}

	public long getEventSequence() {
		return getLong(ProfilerEventColumns.EventSequence);
	}

	public long getBigintData1() {
		return getLong(ProfilerEventColumns.BigintData1);
	}

	public long getBigintData2() {
		return getLong(ProfilerEventColumns.BigintData2);
	}

	public java.util.UUID getGUID() {
		return GetGuid(ProfilerEventColumns.GUID);
	}

	public int getIntegerData2() {
		return getInt(ProfilerEventColumns.IntegerData2);
	}

	public long getObjectID2() {
		return getLong(ProfilerEventColumns.ObjectID2);
	}

	public int getType() {
		return getInt(ProfilerEventColumns.Type);
	}

	public int getOwnerID() {
		return getInt(ProfilerEventColumns.OwnerID);
	}

	public String getParentName() {
		return getString(ProfilerEventColumns.ParentName);
	}

	public int getIsSystem() {
		return getInt(ProfilerEventColumns.IsSystem);
	}

	public int getOffset() {
		return getInt(ProfilerEventColumns.Offset);
	}

	public int getSourceDatabaseID() {
		return getInt(ProfilerEventColumns.SourceDatabaseID);
	}

	public byte[] getSqlHandle() {
		return getByte(ProfilerEventColumns.SqlHandle);
	}

	public String getSessionLoginName() {
		return getString(ProfilerEventColumns.SessionLoginName);
	}

	public byte[] getPlanHandle() {
		return getByte(ProfilerEventColumns.PlanHandle);
	}
}
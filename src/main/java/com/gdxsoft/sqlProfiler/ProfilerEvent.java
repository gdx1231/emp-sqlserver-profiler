package com.gdxsoft.sqlProfiler;

import java.util.Date;

public class ProfilerEvent {
	public Object[] m_Events = new Object[ProfilerEventColumns.ColumnNames.length];

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

	private Integer getInt(int idx) {
		return m_Events[idx] == null ? null : Integer.parseInt(m_Events[idx].toString());
	}

	private Long getLong(int idx) {
		Object val = m_Events[idx];
		if(val == null) {
			return null;
		}
		return Long.parseLong(val.toString());
	}

	private String getString(int idx) {
		return m_Events[idx] == null ? "" : (String) m_Events[idx];
	}

	private byte[] getByte(int idx) {
		return m_Events[idx] == null ? null : (byte[]) m_Events[idx];
	}

	private Date getDateTime(int idx) {
		return m_Events[idx] == null ? null : (Date) m_Events[idx];
	}

	private java.util.UUID GetGuid(int idx) {
		return m_Events[idx] == null ? null : (java.util.UUID) m_Events[idx];
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

	public Integer getDatabaseID() {
		return getInt(ProfilerEventColumns.DatabaseID);
	}

	public Long getTransactionID() {
		return getLong(ProfilerEventColumns.TransactionID);
	}

	public Integer getLineNumber() {
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

	public Integer getClientProcessID() {
		return getInt(ProfilerEventColumns.ClientProcessID);
	}

	public String getApplicationName() {
		return getString(ProfilerEventColumns.ApplicationName);
	}

	public String getLoginName() {
		return getString(ProfilerEventColumns.LoginName);
	}

	public Integer getSPID() {
		return getInt(ProfilerEventColumns.SPID);
	}

	public Long getDuration() {
		return getLong(ProfilerEventColumns.Duration);
	}

	public Date getStartTime() {
		return getDateTime(ProfilerEventColumns.StartTime);
	}

	public Date getEndTime() {
		return getDateTime(ProfilerEventColumns.EndTime);
	}

	public Long getReads() {
		return getLong(ProfilerEventColumns.Reads);
	}

	public Long getWrites() {
		return getLong(ProfilerEventColumns.Writes);
	}

	public Integer getCPU() {
		return getInt(ProfilerEventColumns.CPU);
	}

	public Long getPermissions() {
		return getLong(ProfilerEventColumns.Permissions);
	}

	public Integer getSeverity() {
		return getInt(ProfilerEventColumns.Severity);
	}

	public Integer getEventSubClass() {
		return getInt(ProfilerEventColumns.EventSubClass);
	}

	public Integer getObjectID() {
		return getInt(ProfilerEventColumns.ObjectID);
	}

	public Integer getSuccess() {
		return getInt(ProfilerEventColumns.Success);
	}

	public Integer getIndexID() {
		return getInt(ProfilerEventColumns.IndexID);
	}

	public Integer getIntegerData() {
		return getInt(ProfilerEventColumns.IntegerData);
	}

	public String getServerName() {
		return getString(ProfilerEventColumns.ServerName);
	}

	public Integer getEventClass() {
		return getInt(ProfilerEventColumns.EventClass);
	}

	public Integer getObjectType() {
		return getInt(ProfilerEventColumns.ObjectType);
	}

	public Integer getNestLevel() {
		return getInt(ProfilerEventColumns.NestLevel);
	}

	public Integer getState() {
		return getInt(ProfilerEventColumns.State);
	}

	public Integer getError() {
		return getInt(ProfilerEventColumns.Error);
	}

	public Integer getMode() {
		return getInt(ProfilerEventColumns.Mode);
	}

	public Integer getHandle() {
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

	public Integer getColumnPermissions() {
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

	public Long getRowCounts() {
		return getLong(ProfilerEventColumns.RowCounts);
	}

	public Integer getRequestID() {
		return getInt(ProfilerEventColumns.RequestID);
	}

	public Long getXactSequence() {
		return getLong(ProfilerEventColumns.XactSequence);
	}

	public Long getEventSequence() {
		return getLong(ProfilerEventColumns.EventSequence);
	}

	public Long getBigintData1() {
		return getLong(ProfilerEventColumns.BigintData1);
	}

	public Long getBigintData2() {
		return getLong(ProfilerEventColumns.BigintData2);
	}

	public java.util.UUID getGUID() {
		return GetGuid(ProfilerEventColumns.GUID);
	}

	public Integer getIntegerData2() {
		return getInt(ProfilerEventColumns.IntegerData2);
	}

	public Long getObjectID2() {
		return getLong(ProfilerEventColumns.ObjectID2);
	}

	public Integer getType() {
		return getInt(ProfilerEventColumns.Type);
	}

	public Integer getOwnerID() {
		return getInt(ProfilerEventColumns.OwnerID);
	}

	public String getParentName() {
		return getString(ProfilerEventColumns.ParentName);
	}

	public Integer getIsSystem() {
		return getInt(ProfilerEventColumns.IsSystem);
	}

	public Integer getOffset() {
		return getInt(ProfilerEventColumns.Offset);
	}

	public Integer getSourceDatabaseID() {
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
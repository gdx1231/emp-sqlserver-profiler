package com.gdxsoft.sqlProfiler;

public  class ProfilerEventColumns
{
    public static final String[] ColumnNames =
        {
             "Dumy"
            ,"TextData"
            ,"BinaryData"
            ,"DatabaseID"
            ,"TransactionID"
            ,"LineNumber"
            ,"NTUserName"
            ,"NTDomainName"
            ,"HostName"
            ,"ClientProcessID"
            ,"ApplicationName"
            ,"LoginName"
            ,"SPID"
            ,"Duration"
            ,"StartTime"
            ,"EndTime"
            ,"Reads"
            ,"Writes"
            ,"CPU"
            ,"Permissions"
            ,"Severity"
            ,"EventSubClass"
            ,"ObjectID"
            ,"Success"
            ,"IndexID"
            ,"IntegerData"
            ,"ServerName"
            ,"EventClass"
            ,"ObjectType"
            ,"NestLevel"
            ,"State"
            ,"Error"
            ,"Mode"
            ,"Handle"
            ,"ObjectName"
            ,"DatabaseName"
            ,"FileName"
            ,"OwnerName"
            ,"RoleName"
            ,"TargetUserName"
            ,"DBUserName"
            ,"LoginSid"
            ,"TargetLoginName"
            ,"TargetLoginSid"
            ,"ColumnPermissions"
            ,"LinkedServerName"
            ,"ProviderName"
            ,"MethodName"
            ,"RowCounts"
            ,"RequestID"
            ,"XactSequence"
            ,"EventSequence"
            ,"BigintData1"
            ,"BigintData2"
            ,"GUID"
            ,"IntegerData2"
            ,"ObjectID2"
            ,"Type"
            ,"OwnerID"
            ,"ParentName"
            ,"IsSystem"
            ,"Offset"
            ,"SourceDatabaseID"
            ,"SqlHandle"
            ,"SessionLoginName"
            ,"PlanHandle"
        };

    public static final ProfilerColumnDataType[] ProfilerColumnDataTypes =
        {
            // dummy
            ProfilerColumnDataType.String
            // TextData
            ,ProfilerColumnDataType.String
            // BinaryData
            ,ProfilerColumnDataType.Byte
            // DatabaseID
            ,ProfilerColumnDataType.Int
            // TransactionID
            ,ProfilerColumnDataType.Long
            // LineNumber
            ,ProfilerColumnDataType.Int
            // NTUserName
            ,ProfilerColumnDataType.String
            // NTDomainName
            ,ProfilerColumnDataType.String
            // HostName
            ,ProfilerColumnDataType.String
            // ClientProcessID
            ,ProfilerColumnDataType.Int
            // ApplicationName
            ,ProfilerColumnDataType.String
            // LoginName
            ,ProfilerColumnDataType.String
            // SPID
            ,ProfilerColumnDataType.Int
            // Duration
            ,ProfilerColumnDataType.Long
            // StartTime
            ,ProfilerColumnDataType.DateTime
            // EndTime
            ,ProfilerColumnDataType.DateTime
            // Reads
            ,ProfilerColumnDataType.Long
            // Writes
            ,ProfilerColumnDataType.Long
            // CPU
            ,ProfilerColumnDataType.Int
            // Permissions
            ,ProfilerColumnDataType.Long
            // Severity
            ,ProfilerColumnDataType.Int
            // EventSubClass
            ,ProfilerColumnDataType.Int
            // ObjectID
            ,ProfilerColumnDataType.Int
            // Success
            ,ProfilerColumnDataType.Int
            // IndexID
            ,ProfilerColumnDataType.Int
            // IntegerData
            ,ProfilerColumnDataType.Int
            // ServerName
            ,ProfilerColumnDataType.String
            // EventClass
            ,ProfilerColumnDataType.Int
            // ObjectType
            ,ProfilerColumnDataType.Int
            // NestLevel
            ,ProfilerColumnDataType.Int
            // State
            ,ProfilerColumnDataType.Int
            // Error
            ,ProfilerColumnDataType.Int
            // Mode
            ,ProfilerColumnDataType.Int
            // Handle
            ,ProfilerColumnDataType.Int
            // ObjectName
            ,ProfilerColumnDataType.String
            // DatabaseName
            ,ProfilerColumnDataType.String
            // FileName
            ,ProfilerColumnDataType.String
            // OwnerName
            ,ProfilerColumnDataType.String
            // RoleName
            ,ProfilerColumnDataType.String
            // TargetUserName
            ,ProfilerColumnDataType.String
            // DBUserName
            ,ProfilerColumnDataType.String
            // LoginSid
            ,ProfilerColumnDataType.Byte
            // TargetLoginName
            ,ProfilerColumnDataType.String
            // TargetLoginSid
            ,ProfilerColumnDataType.Byte
            // ColumnPermissions
            ,ProfilerColumnDataType.Int
            // LinkedServerName
            ,ProfilerColumnDataType.String
            // ProviderName
            ,ProfilerColumnDataType.String
            // MethodName
            ,ProfilerColumnDataType.String
            // RowCounts
            ,ProfilerColumnDataType.Long
            // RequestID
            ,ProfilerColumnDataType.Int
            // XactSequence
            ,ProfilerColumnDataType.Long
            // EventSequence
            ,ProfilerColumnDataType.Long
            // BigintData1
            ,ProfilerColumnDataType.Long
            // BigintData2
            ,ProfilerColumnDataType.Long
            // GUID
            ,ProfilerColumnDataType.Guid
            // IntegerData2
            ,ProfilerColumnDataType.Int
            // ObjectID2
            ,ProfilerColumnDataType.Long
            // Type
            ,ProfilerColumnDataType.Int
            // OwnerID
            ,ProfilerColumnDataType.Int
            // ParentName
            ,ProfilerColumnDataType.String
            // IsSystem
            ,ProfilerColumnDataType.Int
            // Offset
            ,ProfilerColumnDataType.Int
            // SourceDatabaseID
            ,ProfilerColumnDataType.Int
            // SqlHandle
            ,ProfilerColumnDataType.Byte
            // SessionLoginName
            ,ProfilerColumnDataType.String
            // PlanHandle
            ,ProfilerColumnDataType.Byte
        };

    /*
    select 'public static final int '+Name + '= '+cast(trace_column_id as varchar)+';'
    from sys.trace_columns
    order by trace_column_id
     */
    public static final int TextData = 1;
    public static final int BinaryData = 2;
    public static final int DatabaseID = 3;
    public static final int TransactionID = 4;
    public static final int LineNumber = 5;
    public static final int NTUserName = 6;
    public static final int NTDomainName = 7;
    public static final int HostName = 8;
    public static final int ClientProcessID = 9;
    public static final int ApplicationName = 10;
    public static final int LoginName = 11;
    public static final int SPID = 12;
    public static final int Duration = 13;
    public static final int StartTime = 14;
    public static final int EndTime = 15;
    public static final int Reads = 16;
    public static final int Writes = 17;
    public static final int CPU = 18;
    public static final int Permissions = 19;
    public static final int Severity = 20;
    public static final int EventSubClass = 21;
    public static final int ObjectID = 22;
    public static final int Success = 23;
    public static final int IndexID = 24;
    public static final int IntegerData = 25;
    public static final int ServerName = 26;
    public static final int EventClass = 27;
    public static final int ObjectType = 28;
    public static final int NestLevel = 29;
    public static final int State = 30;
    public static final int Error = 31;
    public static final int Mode = 32;
    public static final int Handle = 33;
    public static final int ObjectName = 34;
    public static final int DatabaseName = 35;
    public static final int FileName = 36;
    public static final int OwnerName = 37;
    public static final int RoleName = 38;
    public static final int TargetUserName = 39;
    public static final int DBUserName = 40;
    public static final int LoginSid = 41;
    public static final int TargetLoginName = 42;
    public static final int TargetLoginSid = 43;
    public static final int ColumnPermissions = 44;
    public static final int LinkedServerName = 45;
    public static final int ProviderName = 46;
    public static final int MethodName = 47;
    public static final int RowCounts = 48;
    public static final int RequestID = 49;
    public static final int XactSequence = 50;
    public static final int EventSequence = 51;
    public static final int BigintData1 = 52;
    public static final int BigintData2 = 53;
    public static final int GUID = 54;
    public static final int IntegerData2 = 55;
    public static final int ObjectID2 = 56;
    public static final int Type = 57;
    public static final int OwnerID = 58;
    public static final int ParentName = 59;
    public static final int IsSystem = 60;
    public static final int Offset = 61;
    public static final int SourceDatabaseID = 62;
    public static final int SqlHandle = 63;
    public static final int SessionLoginName = 64;
    public static final int PlanHandle = 65;
}

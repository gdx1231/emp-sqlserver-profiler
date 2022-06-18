package com.gdxsoft.sqlProfiler.rawTraceReader;

public class ProfilerEvents {

	/*
	 * select 'public static class '+replace(name,' ','')+' { }
	 * 
	 * ' from sys.trace_categories order by category_id
	 * 
	 * 
	 * select '/ *'+sc.name+'* / '+'public final int '+replace(replace(ev.name,'
	 * ',''),':','')+' = '+cast(trace_event_id as varchar)+';' from
	 * sys.trace_categories sc inner join sys.trace_events ev on sc.category_id =
	 * ev.category_id order by sc.category_id,ev.trace_event_id
	 */
	// ReSharper disable RedundantExplicitArraySize
	public static final String[] Names = 
//ReSharper restore RedundantExplicitArraySize
                                                {
                                                    ""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,"RPC:Completed"
                                                    ,"RPC:Starting"
                                                    ,"SQL:BatchCompleted"
                                                    ,"SQL:BatchStarting"
                                                    ,"Audit Login"
                                                    ,"Audit Logout"
                                                    ,"Attention"
                                                    ,"ExistingConnection"
                                                    ,"Audit Server Starts And Stops"
                                                    ,"DTCTransaction"
                                                    ,"Audit Login Failed"
                                                    ,"EventLog"
                                                    ,"ErrorLog"
                                                    ,"Lock:Released"
                                                    ,"Lock:Acquired"
                                                    ,"Lock:Deadlock"
                                                    ,"Lock:Cancel"
                                                    ,"Lock:Timeout"
                                                    ,"Degree of Parallelism (7.0 Insert)"
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,"Exception"
                                                    ,"SP:CacheMiss"
                                                    ,"SP:CacheInsert"
                                                    ,"SP:CacheRemove"
                                                    ,"SP:Recompile"
                                                    ,"SP:CacheHit"
                                                    ,"Deprecated"
                                                    ,"SQL:StmtStarting"
                                                    ,"SQL:StmtCompleted"
                                                    ,"SP:Starting"
                                                    ,"SP:Completed"
                                                    ,"SP:StmtStarting"
                                                    ,"SP:StmtCompleted"
                                                    ,"Object:Created"
                                                    ,"Object:Deleted"
                                                    ,""
                                                    ,""
                                                    ,"SQLTransaction"
                                                    ,"Scan:Started"
                                                    ,"Scan:Stopped"
                                                    ,"CursorOpen"
                                                    ,"TransactionLog"
                                                    ,"Hash Warning"
                                                    ,""
                                                    ,""
                                                    ,"Auto Stats"
                                                    ,"Lock:Deadlock Chain"
                                                    ,"Lock:Escalation"
                                                    ,"OLEDB Errors"
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,""
                                                    ,"Execution Warnings"
                                                    ,"Showplan Text (Unencoded)"
                                                    ,"Sort Warnings"
                                                    ,"CursorPrepare"
                                                    ,"Prepare SQL"
                                                    ,"Exec Prepared SQL"
                                                    ,"Unprepare SQL"
                                                    ,"CursorExecute"
                                                    ,"CursorRecompile"
                                                    ,"CursorImplicitConversion"
                                                    ,"CursorUnprepare"
                                                    ,"CursorClose"
                                                    ,"Missing Column Statistics"
                                                    ,"Missing Join Predicate"
                                                    ,"Server Memory Change"
                                                    ,"UserConfigurable:0"
                                                    ,"UserConfigurable:1"
                                                    ,"UserConfigurable:2"
                                                    ,"UserConfigurable:3"
                                                    ,"UserConfigurable:4"
                                                    ,"UserConfigurable:5"
                                                    ,"UserConfigurable:6"
                                                    ,"UserConfigurable:7"
                                                    ,"UserConfigurable:8"
                                                    ,"UserConfigurable:9"
                                                    ,"Data File Auto Grow"
                                                    ,"Log File Auto Grow"
                                                    ,"Data File Auto Shrink"
                                                    ,"Log File Auto Shrink"
                                                    ,"Showplan Text"
                                                    ,"Showplan All"
                                                    ,"Showplan Statistics Profile"
                                                    ,""
                                                    ,"RPC Output Parameter"
                                                    ,""
                                                    ,"Audit Database Scope GDR Event"
                                                    ,"Audit Schema Object GDR Event"
                                                    ,"Audit Addlogin Event"
                                                    ,"Audit Login GDR Event"
                                                    ,"Audit Login Change Property Event"
                                                    ,"Audit Login Change Password Event"
                                                    ,"Audit Add Login to Server Role Event"
                                                    ,"Audit Add DB User Event"
                                                    ,"Audit Add Member to DB Role Event"
                                                    ,"Audit Add Role Event"
                                                    ,"Audit App Role Change Password Event"
                                                    ,"Audit Statement Permission Event"
                                                    ,"Audit Schema Object Access Event"
                                                    ,"Audit Backup/Restore Event"
                                                    ,"Audit DBCC Event"
                                                    ,"Audit Change Audit Event"
                                                    ,"Audit Object Derived Permission Event"
                                                    ,"OLEDB Call Event"
                                                    ,"OLEDB QueryInterface Event"
                                                    ,"OLEDB DataRead Event"
                                                    ,"Showplan XML"
                                                    ,"SQL:FullTextQuery"
                                                    ,"Broker:Conversation"
                                                    ,"Deprecation Announcement"
                                                    ,"Deprecation Final Support"
                                                    ,"Exchange Spill Event"
                                                    ,"Audit Database Management Event"
                                                    ,"Audit Database Object Management Event"
                                                    ,"Audit Database Principal Management Event"
                                                    ,"Audit Schema Object Management Event"
                                                    ,"Audit Server Principal Impersonation Event"
                                                    ,"Audit Database Principal Impersonation Event"
                                                    ,"Audit Server Object Take Ownership Event"
                                                    ,"Audit Database Object Take Ownership Event"
                                                    ,"Broker:Conversation Group"
                                                    ,"Blocked process report"
                                                    ,"Broker:Connection"
                                                    ,"Broker:Forwarded Message Sent"
                                                    ,"Broker:Forwarded Message Dropped"
                                                    ,"Broker:Message Classify"
                                                    ,"Broker:Transmission"
                                                    ,"Broker:Queue Disabled"
                                                    ,"Broker:Mirrored Route State Changed"
                                                    ,""
                                                    ,"Showplan XML Statistics Profile"
                                                    ,""
                                                    ,"Deadlock graph"
                                                    ,"Broker:Remote Message Acknowledgement"
                                                    ,"Trace File Close"
                                                    ,""
                                                    ,"Audit Change Database Owner"
                                                    ,"Audit Schema Object Take Ownership Event"
                                                    ,""
                                                    ,"FT:Crawl Started"
                                                    ,"FT:Crawl Stopped"
                                                    ,"FT:Crawl Aborted"
                                                    ,"Audit Broker Conversation"
                                                    ,"Audit Broker Login"
                                                    ,"Broker:Message Undeliverable"
                                                    ,"Broker:Corrupted Message"
                                                    ,"User Error Message"
                                                    ,"Broker:Activation"
                                                    ,"Object:Altered"
                                                    ,"Performance statistics"
                                                    ,"SQL:StmtRecompile"
                                                    ,"Database Mirroring State Change"
                                                    ,"Showplan XML For Query Compile"
                                                    ,"Showplan All For Query Compile"
                                                    ,"Audit Server Scope GDR Event"
                                                    ,"Audit Server Object GDR Event"
                                                    ,"Audit Database Object GDR Event"
                                                    ,"Audit Server Operation Event"
                                                    ,""
                                                    ,"Audit Server Alter Trace Event"
                                                    ,"Audit Server Object Management Event"
                                                    ,"Audit Server Principal Management Event"
                                                    ,"Audit Database Operation Event"
                                                    ,""
                                                    ,"Audit Database Object Access Event"
                                                    ,"TM: Begin Tran starting"
                                                    ,"TM: Begin Tran completed"
                                                    ,"TM: Promote Tran starting"
                                                    ,"TM: Promote Tran completed"
                                                    ,"TM: Commit Tran starting"
                                                    ,"TM: Commit Tran completed"
                                                    ,"TM: Rollback Tran starting"
                                                    ,"TM: Rollback Tran completed"
                                                    ,"Lock:Timeout (timeout > 0)"
                                                    ,"Progress Report: Online Index Operation"
                                                    ,"TM: Save Tran starting"
                                                    ,"TM: Save Tran completed"
                                                    ,"Background Job Error"
                                                    ,"OLEDB Provider Information"
                                                    ,"Mount Tape"
                                                    ,"Assembly Load"
                                                    ,""
                                                    ,"XQuery Static Type"
                                                    ,"QN: Subscription"
                                                    ,"QN: Parameter table"
                                                    ,"QN: Template"
                                                }  ;
}
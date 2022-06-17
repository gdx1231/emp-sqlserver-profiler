package com.gdxsoft.sqlProfiler.helpers;

public class PerfColumn {
	public String Caption;
	public int Column;
	public int Width;
	public String Format;
	public HorizontalAlignment Alignment = HorizontalAlignment.Left;

	public PerfColumn() {
  
	}

	// Caption = "Event Class", Column = ProfilerEventColumns.EventClass, Width =
	// 122
	public PerfColumn(String caption, int column, int width) {
		this.Caption = caption;
		this.Column = column;
		this.Width = width;
	}

	public PerfColumn(String caption, int column, int width, HorizontalAlignment alignment, String format) {
		this.Caption = caption;
		this.Column = column;
		this.Width = width;
		this.Alignment = alignment;
		this.Format = format;
	}
}

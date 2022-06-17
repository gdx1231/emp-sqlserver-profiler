package com.gdxsoft.sqlProfiler.eventDelegate;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdxsoft.sqlProfiler.ProfilerEvent;

public class SetBaseColumn {
	static Logger LOGGER = LoggerFactory.getLogger(SetBaseColumn.class);
	public ResultSet m_Reader;

	public static void setGuidColumn(ProfilerEvent evt, int columnid) throws SQLException {
		throw new SQLException("not implements");
	}

	public void setDateTimeColumn(ProfilerEvent evt, int columnid) throws SQLException {
		// 2 byte - year
		// 2 byte - month
		// 2 byte - ???
		// 2 byte - day
		// 2 byte - hour
		// 2 byte - min
		// 2 byte - sec
		// 2 byte - msec
		byte[] m_B16 = new byte[16];
		byte[] bytes = m_Reader.getBytes(3);
		System.arraycopy(bytes, 0, m_B16, 0, 16);
		int year = m_B16[0] & 0xFF | m_B16[1] << 8;
		int month = m_B16[2] | m_B16[3] << 8;
		int day = m_B16[6] | m_B16[7] << 8;
		int hour = m_B16[8] | m_B16[9] << 8;
		int min = m_B16[10] | m_B16[11] << 8;
		int sec = m_B16[12] | m_B16[13] << 8;
		int msec = m_B16[14] | m_B16[15] << 8;
		// LOGGER.info("year={}, month={}, day={}, hour={}, min={}, sec={}, msec={}",
		// year, month, day, hour, min, sec, msec);

		Calendar cal;
		cal = Calendar.getInstance();
		cal.set(year, month, day, hour, min, sec);
		cal.set(Calendar.MILLISECOND, msec);

		evt.m_Events[columnid] = cal.getTime();
	}

	public void setByteColumn(ProfilerEvent evt, int columnid) throws SQLException {
		// byte[] b = new byte[(int) m_Reader[1]];
		byte[] b = new byte[m_Reader.getByte(3)];
		evt.m_Events[columnid] = b;
	}

	public void setStringColumn(ProfilerEvent evt, int columnid) throws SQLException {
		// evt.m_Events[columnid] = System.Text.Encoding.Unicode.GetString((byte[])
		// m_Reader[2]);
		/*
		 * int colid = m_Reader.getInt(1); int length = m_Reader.getInt(2);
		 */
		byte[] bytes = m_Reader.getBytes(3);
		String txt = new String(bytes, StandardCharsets.UTF_16LE);
		// System.out.println(colid + "," + length + "," + bytes.length + ", " + txt);
		evt.m_Events[columnid] = txt;

	}

	public void setIntColumn(ProfilerEvent evt, int columnid) throws SQLException {
		// m_Reader.GetBytes(2, 0, m_B4, 0, 4);
		byte[] m_B4 = new byte[4];
		byte[] bytes = m_Reader.getBytes(3);
		System.arraycopy(bytes, 0, m_B4, 0, 4);
		evt.m_Events[columnid] = toInt32(m_B4);
	}

	public void setLongColumn(ProfilerEvent evt, int columnid) throws SQLException {
		byte[] m_B8 = new byte[8];
		byte[] bytes = m_Reader.getBytes(3);
		System.arraycopy(bytes, 0, m_B8, 0, 8);
		evt.m_Events[columnid] = toInt64(m_B8);
	}

	public static long toInt64(byte[] value) {
		// ByteBuffer buffer = ByteBuffer.allocate(8);
//		buffer.put(value);
//		buffer.flip();
//		return buffer.getLong();
		int i1 = (value[0] & 0xFF) | (value[1] & 0xFF << 8) | (value[2] & 0xFF << 16) | (value[3] & 0xFF << 24);
		int i2 = (value[4] & 0xFF) | (value[5] & 0xFF << 8) | (value[6] & 0xFF << 16) | (value[7] & 0xFF << 24);
		return (int) i1 | ((long) i2 << 32);
	}

	public static int toInt32(byte[] value) {
		return (value[0] & 0xFF) | (value[1] & 0xFF << 8) | (value[2] & 0xFF << 16) | (value[3] & 0xFF << 24);
	}

	public static int toInt16(byte[] value) {
		return (value[0] & 0xFF) | (value[1] & 0xFF << 8);
	}

	/**
	 * @return the m_Reader
	 */
	public ResultSet getReader() {
		return m_Reader;
	}

	/**
	 * @param m_Reader the m_Reader to set
	 */
	public void setReader(ResultSet reader) {
		this.m_Reader = reader;
	}

}

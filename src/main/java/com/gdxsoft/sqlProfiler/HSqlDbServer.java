package com.gdxsoft.sqlProfiler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.gdxsoft.easyweb.conf.ConnectionConfig;
import com.gdxsoft.easyweb.conf.ConnectionConfigs;
import com.gdxsoft.easyweb.utils.UFile;
import com.gdxsoft.easyweb.utils.UPath;
import com.gdxsoft.easyweb.utils.msnet.MTableStr;

public class HSqlDbServer {
	/**
	 * HSQLDB数据库所在目录的初始化参数名称<br>
	 * 在ewa_conf.xml的 initparas中定义<br>
	 * 不设定的话，会在临时目录创建
	 */
	public static final String PARA_HSQLDB_PATH = "sqlprofiler_hsqldb_path";
	private static Logger LOGGER = LoggerFactory.getLogger(HSqlDbServer.class);

	private static HSqlDbServer INSTANCE;
	public static final String CONN_STR = "hsqldb_server"; // 必须小写

	public static String WORK_PATH;
	public static String HSQLDB_URL;
	
	public static HSqlDbServer getInstance() throws Exception {
		if (INSTANCE != null) {
			return INSTANCE;
		}
		init();
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @return
	 * @throws Exception
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	synchronized private static void init() throws Exception {
		ConnectionConfigs c1 = ConnectionConfigs.instance();
		if (c1.containsKey(CONN_STR)) {
			// already defined in the ewa_conf.xml
			HSqlDbServer o = new HSqlDbServer();
			INSTANCE = o;
			return;
		}

		ConnectionConfig poolCfg = new ConnectionConfig();
		poolCfg.setName(CONN_STR);
		poolCfg.setType("HSQLDB");
		poolCfg.setConnectionString(CONN_STR);
		poolCfg.setSchemaName("PUBLIC");

		String hsqlDbWorkPath = createHsqldbWorkDirectory().getAbsolutePath();
		String url = "jdbc:hsqldb:file:" + hsqlDbWorkPath + "/hsqldb";
		
		HSQLDB_URL = url;
		
		MTableStr poolParams = new MTableStr();
		poolParams.put("driverClassName", "org.hsqldb.jdbc.JDBCDriver");
		poolParams.put("url", url);
		poolParams.put("username", "sa");
		poolParams.put("password", "gldflg!fsd$fldfnnd");
		poolParams.put("maxActive", 10);
		poolParams.put("maxIdle", 100);

		poolCfg.setPool(poolParams);
		c1.put(CONN_STR, poolCfg);

		LOGGER.info("initialize the hsqldb {}", url);
		HSqlDbServer o = new HSqlDbServer();
		INSTANCE = o;

	}

	/**
	 * 创建hsqldb工作目录
	 * 
	 * @return
	 * @throws Exception
	 */
	private static File createHsqldbWorkDirectory() throws Exception {
		String databasePath = SqlServerProfiler.APPNAME + "." + CONN_STR;
		// ewa_conf定义的工作目录
		String hsqldbPath = UPath.getInitPara(PARA_HSQLDB_PATH);
		if (StringUtils.isBlank(hsqldbPath)) {
			// 使用临时目录
			hsqldbPath = System.getProperty("java.io.tmpdir") + File.separator + databasePath;
		}
		File tmpdir = new File(hsqldbPath);
		if (!tmpdir.exists()) {
			UFile.buildPaths(tmpdir.getAbsolutePath());
		}
		WORK_PATH = tmpdir.getAbsolutePath();
		
		List<String> files = new ArrayList<>();
		files.add("hsqldb.script");
		files.add("hsqldb.properties");
		files.add("hsqldb.lobs");
		final StringBuilder needCopy = new StringBuilder();

		files.forEach(dbFilePath -> {
			File f = new File(tmpdir + File.separator + dbFilePath);
			if (!f.exists()) {
				needCopy.append("yes");
			}
		});
		if (needCopy.length() == 0) {
			return tmpdir;
		}
		String resRoot = "/com/gdxsoft/sqlProfiler/hsqldb/";
		final StringBuilder err = new StringBuilder();
		files.forEach(dbFilePath -> {
			String res = resRoot + dbFilePath;
			URL url = HSqlDbServer.class.getResource(res);
			File targetFile = new File(tmpdir + File.separator + dbFilePath);
			if (url == null) {
				String err1 = String.format("Copy hsqldb form res:%s to %s, res is null", res, targetFile.toString());
				err.append(err1).append("\n");
				LOGGER.error(err1);
				return;
			}

			try {
				byte[] buf = IOUtils.toByteArray(url);
				UFile.createBinaryFile(targetFile.getAbsolutePath(), buf, true);
				LOGGER.info("Copy form {} to {}", url, targetFile);
			} catch (Exception e) {
				String err1 = String.format("Copy hsqldb form %s to %s, %s", url.toString(), targetFile.toString(),
						e.getMessage());
				err.append(err1).append("\n");
				LOGGER.error(err1);
			}
		});
		if (err.length() > 0) {
			throw new Exception(err.toString());
		}
		return tmpdir;
	}

}

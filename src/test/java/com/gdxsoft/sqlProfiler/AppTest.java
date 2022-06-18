package com.gdxsoft.sqlProfiler;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class AppTest extends TestCase {
	/*
	 * static { InputStream stream =
	 * TestCase.class.getClassLoader().getResourceAsStream("logging.properties");
	 * try { LogManager.getLogManager().readConfiguration(stream);
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } }
	 */
	public static void main(String[] args) {
		ProfilerControl.main(args); 
	}

	 
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}
}

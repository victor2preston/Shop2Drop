package com.victorpreston.shop2drop;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(Shop2DropActivityTest.class);
		//$JUnit-END$
		return suite;
	}

}

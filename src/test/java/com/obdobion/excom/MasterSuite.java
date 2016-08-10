package com.obdobion.excom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <p>MasterSuite class.</p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 * @since 2.0.1
 */
@RunWith(Suite.class)
@SuiteClasses({ HelpTests.class, SenderTest.class, StandardCommandsTest.class })
public class MasterSuite
{}

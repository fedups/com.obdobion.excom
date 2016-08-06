package com.obdobion.excom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ HelpTests.class, SenderTest.class, StandardCommandsTest.class })
public class MasterSuite
{}

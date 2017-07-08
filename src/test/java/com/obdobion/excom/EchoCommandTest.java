package com.obdobion.excom;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.obdobion.excom.ui.ExComContext;

public class EchoCommandTest
{
    static private Receiver      receiver;

    @BeforeClass
    public static void before() throws Exception
    {
        try
        {
            receiver = ExCom.listenForExternalCommands();
        } catch (final Exception e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public static void after() throws Exception
    {
        receiver.stop();
    }

    @Test
    public void commandline() throws Exception
    {
        ExCom.sendExternalCommandToListener("echo commandLine");
    }

    @Test
    public void xml() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("echo xml");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void property() throws Exception
    {
        ExCom.sendExternalCommandToListener("echo property");
    }

    @Test
    public void off() throws Exception
    {
        ExCom.sendExternalCommandToListener("echo off");
    }
}

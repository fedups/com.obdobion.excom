package com.obdobion.excom;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.obdobion.excom.ui.ExComContext;

public class EchoCommandTest
{
    static ExCom ex;

    @BeforeClass
    public static void before() throws Exception
    {
        try
        {
            ex = new ExCom();
            ex.startReceiveMode();
        } catch (final Exception e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void commandline() throws Exception
    {
        final Sender sender = ex.createSender();
        sender.processInputRequest("echo commandLine");
    }

    @Test
    public void xml() throws Exception
    {
        final Sender sender = ex.createSender();
        final ExComContext context = sender.processInputRequest("echo xml");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void property() throws Exception
    {
        final Sender sender = ex.createSender();
        sender.processInputRequest("echo property");
    }

    @Test
    public void off() throws Exception
    {
        final Sender sender = ex.createSender();
        sender.processInputRequest("echo off");
    }
}

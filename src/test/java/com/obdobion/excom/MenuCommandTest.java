package com.obdobion.excom;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.obdobion.excom.ui.ExComContext;

public class MenuCommandTest
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
    public void menu() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("menu");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void menuMatchesOneCommand() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("menu menu");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void menuMatchesAFew() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("menu system");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void menuMatchesWIthMoreThanOneCriteria() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("menu system history");
        System.out.println(context.getOutline().getWriter().toString());
    }
}

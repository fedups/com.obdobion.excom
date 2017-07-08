package com.obdobion.excom;

import org.junit.BeforeClass;
import org.junit.Test;

import com.obdobion.excom.ui.ExComContext;

public class MenuCommandTest
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
        {}
    }

    @Test
    public void menu() throws Exception
    {
        final Sender sender = ex.createSender();
        final ExComContext context = sender.processInputRequest("menu");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void menuMatchesOneCommand() throws Exception
    {
        final Sender sender = ex.createSender();
        final ExComContext context = sender.processInputRequest("menu menu");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void menuMatchesAFew() throws Exception
    {
        final Sender sender = ex.createSender();
        final ExComContext context = sender.processInputRequest("menu system");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void menuMatchesWIthMoreThanOneCriteria() throws Exception
    {
        final Sender sender = ex.createSender();
        final ExComContext context = sender.processInputRequest("menu system history");
        System.out.println(context.getOutline().getWriter().toString());
    }
}

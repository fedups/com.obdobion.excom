package com.obdobion.excom;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.obdobion.excom.ui.ExComContext;

/**
 * <p>
 * HelpTests class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 * @since 2.0.1
 */
public class HelpTests
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
    public void commandHelpLong() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("menu --help");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void commandEchoLong() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("echo --help");
        System.out.println(context.getOutline().getWriter().toString());
    }

    @Test
    public void commandHelpShort() throws Exception
    {
        final ExComContext context = ExCom.sendExternalCommandToListener("menu -?");
        System.out.println(context.getOutline().getWriter().toString());
    }
}

package com.obdobion.excom;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * HelpTests class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 * @since 2.0.1
 */
public class InteractiveTests
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
    public void startInteractiveSession() throws Exception
    {
        // final Sender sender = ex.createSender();
        // final ExComContext context = sender.processInputRequest("IC");
        // System.out.println(context.getOutline().getWriter().toString());
    }
}

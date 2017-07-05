package com.obdobion.excom;

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
    public void startInteractiveSession() throws Exception
    {
        // final Sender sender = ex.createSender();
        // final ExComContext context = sender.processInputRequest("IC");
        // System.out.println(context.getOutline().getWriter().toString());
    }
}

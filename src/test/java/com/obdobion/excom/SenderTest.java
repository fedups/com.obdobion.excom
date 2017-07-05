package com.obdobion.excom;

import org.junit.Assert;
import org.junit.Test;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.Config;
import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.PluginManager;

/**
 * <p>
 * SenderTest class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 * @since 2.0.1
 */
public class SenderTest
{
    public class MyRequest implements IExternalRequest
    {
        @Arg(caseSensitive = true)
        public String myParm;

        @Override
        public String execute(final ClientCommand cc) throws Exception
        {
            return myParm;
        }
    }

    static String interruptMsgShouldBeNull;

    /**
     * <p>
     * abortingAHungThreadCCTimer.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void abortingAHungThreadCCTimer() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            final ClientCommand cc = rcvr.createCommand("abortingAHungThreadCCTimer", p_cc -> {
                for (int x = 0; x < 60; x++)
                    synchronized (rcvr)
                    {
                        try
                        {
                            Thread.sleep(10);
                        } catch (final Exception e)
                        {
                            System.out.println("ignoring " + e.getMessage());
                        }
                    }
                Assert.fail("thread should have been aborted");
                return "thread should have been aborted";
            });
            cc.setTimeoutMS(500);
            rcvr.register(cc);

            rcvr.go();

            final Config config = new Config(".");
            final ExcomContext context = PluginManager.createContext(config, new PluginManager(config));
            final Sender sender = new Sender();
            context.setParser(CmdLine.load(sender, "-h localhost -p 2526 -n abortingAHungThreadCCTimer"));
            sender.processInputRequest(context, "abortingAHungThreadCCTimer");
            WatchForCommandLineCommands.stop(context);

            Assert.assertEquals("abortingAHungThreadCCTimer result",
                    "timed-out",
                    context.getOutline().getWriter().toString().substring(0, 9));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>
     * abortingAHungThreadContextTimer.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void abortingAHungThreadContextTimer() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            final ClientCommand cc = rcvr.createCommand("abortingAHungThreadContextTimer", _cc -> {
                for (int x = 0; x < 60; x++)
                    synchronized (rcvr)
                    {
                        try
                        {
                            Thread.sleep(10);
                        } catch (final Exception e)
                        {
                            System.out.println("ignoring " + e.getMessage());
                        }
                    }
                Assert.fail("thread should have been aborted");
                return "thread should have been aborted";
            });
            cc.setTimeoutMS(-1);
            rcvr.register(cc);

            rcvr.go();

            final Config config = new Config(".");
            final ExcomContext context = PluginManager.createContext(config, new PluginManager(config));
            final Sender sender = new Sender();
            context.setParser(CmdLine.load(sender,
                    "-h localhost -p 2526 -n abortingAHungThreadContextTimer --timeoutMS 500 --logResult"));
            sender.processInputRequest(context, "abortingAHungThreadContextTimer");
            WatchForCommandLineCommands.stop(context);

            Assert.assertEquals("abortingAHungThreadCCTimer result",
                    "timed-out",
                    context.getOutline().getWriter().toString().substring(0, 9));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>
     * targetPortNotUp.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void targetPortNotUp() throws Exception
    {
        final Config config = new Config(".");
        final ExcomContext context = PluginManager.createContext(config, new PluginManager(config));
        final Sender sender = new Sender();
        context.setParser(CmdLine.load(sender, "-h localhost -p 2526 -n targetPortNotUp"));
        sender.processInputRequest(context, "targetPortNotUp");
        WatchForCommandLineCommands.stop(context);

        Assert.assertEquals("targetPortNotUp result",
                "Connection refused", context.getOutline().getWriter().toString().substring(0, 18));
    }

    /**
     * <p>
     * testNeverEndingCommand.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testNeverEndingCommand() throws Exception
    {
        interruptMsgShouldBeNull = null;
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.register("waitForever", cc -> {
                synchronized (rcvr)
                {
                    rcvr.wait(1000);
                }
                interruptMsgShouldBeNull = "this should not be set";
                return "should not return";
            });
            rcvr.go();

            final Config config = new Config(".");
            final ExcomContext context = PluginManager.createContext(config, new PluginManager(config));
            final Sender sender = new Sender();
            context.setParser(CmdLine.load(sender, "-h localhost -p 2526 -n waitForever --async"));
            sender.processInputRequest(context, "waitForever");
            WatchForCommandLineCommands.stop(context);

        } finally
        {
            rcvr.stop();

            synchronized (rcvr)
            {
                rcvr.wait(1100);
            }

            Assert.assertNull(interruptMsgShouldBeNull, interruptMsgShouldBeNull);
        }
    }
}

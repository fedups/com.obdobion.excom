package com.obdobion.excom;

import org.junit.Assert;
import org.junit.Test;

import com.obdobion.argument.annotation.Arg;

public class SenderTest
{
    public class MyRequest implements IExternalRequest
    {
        @Arg(caseSensitive = true)
        public String myParm;

        public String execute(final ClientCommand cc) throws Exception
        {
            return myParm;
        }
    }

    static String interruptMsgShouldBeNull;

    @Test
    public void abortingAHungThreadCCTimer() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            final ClientCommand cc = rcvr.createCommand("abortingAHungThreadCCTimer", new IExternalRequest()
            {
                public String execute(final ClientCommand cc) throws Exception
                {
                    for (int x = 0; x < 60; x++)
                    {
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
                    }
                    Assert.fail("thread should have been aborted");
                    return "thread should have been aborted";
                }
            });
            cc.setTimeoutMS(500);
            rcvr.register(cc);

            rcvr.go();
            final ExComContext context = new ExComContext("abortingAHungThreadCCTimer");
            // context.timeoutMS = 500;
            context.logResult = true;
            new Sender(2526).send(context);
            Assert.assertEquals("abortingAHungThreadCCTimer result", "timed-out", context.toString());

        } finally
        {
            rcvr.stop();
        }
    }

    @Test
    public void abortingAHungThreadContextTimer() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            final ClientCommand cc = rcvr.createCommand("abortingAHungThreadContextTimer", new IExternalRequest()
            {
                public String execute(final ClientCommand cc) throws Exception
                {
                    for (int x = 0; x < 60; x++)
                    {
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
                    }
                    Assert.fail("thread should have been aborted");
                    return "thread should have been aborted";
                }
            });
            cc.setTimeoutMS(-1);
            rcvr.register(cc);

            rcvr.go();
            final ExComContext context = new ExComContext("abortingAHungThreadContextTimer");
            context.timeoutMS = 500;
            context.logResult = true;
            new Sender(2526).send(context);
            Assert.assertEquals("abortingAHungThreadContextTimer result", "timed-out",
                    context.toString().substring(0, 9));

        } finally
        {
            rcvr.stop();
        }
    }

    @Test
    public void submitWhileBusy() throws Exception
    {
        interruptMsgShouldBeNull = null;
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard();
            rcvr.register("waitForever", new IExternalRequest()
            {
                public String execute(final ClientCommand cc) throws Exception
                {
                    synchronized (rcvr)
                    {
                        rcvr.wait(1000);
                    }
                    interruptMsgShouldBeNull = "this should not be set";
                    return "should not return";
                }
            });
            rcvr.go();

            final Sender sender = new Sender(2526);

            final ExComContext result = sender.send(new ExComContext(false, "waitForever", ""));
            Assert.assertEquals("testNoParms result", "submitted", result.toString());

            synchronized (this)
            {
                wait(500);
            }

            final ExComContext result2 = sender.send(new ExComContext("echo", "'Hello World'"));
            Assert.assertEquals("testNoParms result", "busy - please try later", result2.toString());

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

    @Test
    public void targetPortNotUp() throws Exception
    {
        final ExComContext context = new ExComContext("doesn't matter");
        new Sender(2526).send(context);
        Assert.assertEquals("targetPortNotUp", "The receiver on port 2526 is not reachable at localhost", context
                .toString());
    }

    @Test
    public void testNeverEndingCommand() throws Exception
    {
        interruptMsgShouldBeNull = null;
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.register("waitForever", new IExternalRequest()
            {
                public String execute(final ClientCommand cc) throws Exception
                {
                    synchronized (rcvr)
                    {
                        rcvr.wait(1000);
                    }
                    interruptMsgShouldBeNull = "this should not be set";
                    return "should not return";
                }
            });
            rcvr.go();
            final ExComContext result = new Sender(2526).send(new ExComContext(false, "waitForever", ""));
            Assert.assertEquals("testNoParms result", "submitted", result.toString());

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

    @Test
    public void testNoParms() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.register("testNoParms", new IExternalRequest()
            {
                public String execute(final ClientCommand cc) throws Exception
                {
                    return "That was fun";
                }
            });
            rcvr.go();
            final ExComContext result = new Sender(2526).send(new ExComContext("testNoParms"));
            Assert.assertEquals("testNoParms result", "That was fun", result.toString());

        } finally
        {
            rcvr.stop();
        }
    }

    @Test
    public void testWithParms() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.register("testWithParms", new MyRequest()).go();
            final ExComContext result = new Sender(2526).send(new ExComContext("testWithParms", "--myParm WHAT!"));
            Assert.assertEquals("testWithParms result", "WHAT!", result.toString());
        } finally
        {
            rcvr.stop();
        }
    }
}

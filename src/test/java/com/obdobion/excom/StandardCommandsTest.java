package com.obdobion.excom;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>StandardCommandsTest class.</p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 * @since 2.0.1
 */
public class StandardCommandsTest
{
    /**
     * <p>dumpAnd.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void dumpAnd() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("dump", "-m system RUNNABLE");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertTrue("dump result", context.toString().contains("system"));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>dumpAndInverse.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void dumpAndInverse() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("dump", "-vm system RUNNABLE");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertTrue("dump result", context.toString().contains("system"));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>dumpMatchesSystem.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void dumpMatchesSystem() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("dump", "-m system");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertTrue("dump result", context.toString().contains("system:"));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>dumpMatchesSystemInverted.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void dumpMatchesSystemInverted() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("dump", "-vm '(system|RUNNABLE)'");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertTrue("dump result", context.toString().contains("TIMED_WAITING"));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>dumpShowAll.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void dumpShowAll() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("dump");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertTrue("dump result", context.toString().contains("RUNNABLE"));

        } finally
        {
            rcvr.stop();
        }
    }

    // @Test
    /**
     * This test can not normally be activated since it shuts down the test
     * harness. Run it stand-alone and it will succeed, but hang.
     *
     * @throws java.lang.Exception if any.
     */
    public void kill0() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    System.out.println("shutdown hook success");
                }
            });

            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("kill", "0 -c");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertEquals("kill 0 result", "ok", context.toString());

            synchronized (rcvr)
            {
                rcvr.wait(1000);
            }

            Assert.fail("The thread should have been killed");

        } finally
        {
            rcvr.stop();
        }
    }

    // @Test
    /**
     * This test can not normally be activated since it shuts down the test
     * harness. Run it stand-alone and it will succeed, but hang.
     *
     * @throws java.lang.Exception if any.
     */
    public void kill9() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    Assert.fail("shutdown hook should not have been called");
                }
            });

            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("kill", "0 --c");

            new Sender(2526).send(context);
            System.out.println(context.toString());
            Assert.assertEquals("kill 9 result", "ok", context.toString());

            synchronized (rcvr)
            {
                rcvr.wait(1000);
            }

            Assert.fail("The thread should have been killed");

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>standardEcho.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void standardEcho() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("echo", "'hello world from junit'");

            new Sender(2526).send(context);
            Assert.assertEquals("standardEcho result", "hello world from junit", context.toString());

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>standardGC.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void standardGC() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            final ExComContext context = new ExComContext("GC");
            context.logResult = true;

            new Sender(2526).send(context);
            Assert.assertTrue("GC result", context.toString().startsWith("free memory before("));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>standardGCWithPrefix.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void standardGCWithPrefix() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("prefix").go();
            final ExComContext context = new ExComContext("prefixGC");

            new Sender(2526).send(context);
            Assert.assertTrue("GC result", context.toString().startsWith("free memory before("));

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>subsequentCommandsWork.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void subsequentCommandsWork() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        try
        {
            rcvr.registerStandard("").go();
            ExComContext context = null;
            final Sender sender = new Sender(2526);

            context = new ExComContext("echo", "'echo1'");
            sender.send(context);
            Assert.assertEquals("standardEcho result", "echo1", context.toString());

            context = new ExComContext("echo", "'echo2'");
            sender.send(context);
            Assert.assertEquals("standardEcho result", "echo2", context.toString());

        } finally
        {
            rcvr.stop();
        }
    }
}

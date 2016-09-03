package com.obdobion.excom;

import org.junit.Test;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.annotation.Arg;
import com.obdobion.howto.App;
import com.obdobion.howto.Config;
import com.obdobion.howto.Context;
import com.obdobion.howto.PluginManager;

import junit.framework.Assert;

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
    public class MyRequest implements IExternalRequest
    {
        @Arg
        public String myParm;

        @Override
        public String execute(final ClientCommand cc) throws Exception
        {
            return myParm;
        }
    }

    /**
     * <p>
     * testHelpTOC.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testHelpTOC() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        rcvr.register("This is a test command - nothing to see here.", "testWithParms", new MyRequest());
        try
        {
            rcvr.go();

            final Config config = new Config(".");
            final Context context = PluginManager.createContext(config, new PluginManager(config));
            final Sender sender = new Sender();
            context.setParser(CmdLine.load(sender, "-h localhost -p 2526 -n testHelpTOC"));
            final int bytesReceived = sender.processInputRequest(context, "help");
            App.destroyContext(context);

            Assert.assertEquals(80, bytesReceived);
            Assert.assertEquals("Commands\n" +
                    "--------\n"
                    + "testWithParms - This is a test command - nothing to see here.\n",
                    context.getOutline().getWriter().toString());

        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>
     * testHelpTopic.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testHelpTopic() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        rcvr.register("testWithParms", new MyRequest());
        try
        {
            rcvr.go();

            final Config config = new Config(".");
            final Context context = PluginManager.createContext(config, new PluginManager(config));
            final Sender sender = new Sender();
            context.setParser(CmdLine.load(sender, "-h localhost -p 2526 -n testWithParms"));
            final int bytesReceived = sender.processInputRequest(context, "help testWithParms");
            App.destroyContext(context);

            Assert.assertEquals(13, bytesReceived);
            Assert.assertEquals("testWithParms\n", context.getOutline().getWriter().toString());
        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>
     * testHelpTopicQuestionMark.
     * </p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testHelpTopicQuestionMark() throws Exception
    {
        final Receiver rcvr = new Receiver(2526);
        rcvr.register("testWithParms", new MyRequest());
        try
        {
            rcvr.go();

            final Config config = new Config(".");
            final Context context = PluginManager.createContext(config, new PluginManager(config));
            final Sender sender = new Sender();
            context.setParser(CmdLine.load(sender, "-h localhost -p 2526 -n testWithParms"));
            final int bytesReceived = sender.processInputRequest(context, "testWithParms --help");
            App.destroyContext(context);

            Assert.assertEquals(40, bytesReceived);
        } finally
        {
            rcvr.stop();
        }
    }
}

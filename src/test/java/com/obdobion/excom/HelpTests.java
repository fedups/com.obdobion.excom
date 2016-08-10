package com.obdobion.excom;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.obdobion.argument.annotation.Arg;

/**
 * <p>HelpTests class.</p>
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

        public String execute(final ClientCommand cc) throws Exception
        {
            return myParm;
        }
    }

    Logger log = Logger.getLogger("");

    /**
     * <p>testHelpTOC.</p>
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
            final ExComContext result = new Sender(2526).send(new ExComContext("help"));
            System.out.println(result.toString());
        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>testHelpTopic.</p>
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
            final ExComContext result = new Sender(2526).send(new ExComContext("help", "testWithParms"));
            System.out.println(result.toString());
        } finally
        {
            rcvr.stop();
        }
    }

    /**
     * <p>testHelpTopicQuestionMark.</p>
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
            final ExComContext result = new Sender(2526).send(new ExComContext("testWithParms", "-?"));
            System.out.println(result.toString());
        } finally
        {
            rcvr.stop();
        }
    }
}

package com.obdobion.excom;

import org.apache.log4j.Logger;
import org.junit.Test;

public class HelpTests
{

    public class MyRequest implements IExternalRequest
    {

        public String myParm;

        public String execute(final ClientCommand cc) throws Exception
        {

            return myParm;
        }
    }

    Logger log = Logger.getLogger("");

    @Test
    public void testHelpTOC() throws Exception
    {

        final Receiver rcvr = new Receiver(2526);
        rcvr.register("This is a test command - nothing to see here.", "testWithParms", new MyRequest(), new String[] {
                "--type String --key myParm --var myParm"
        });
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

    @Test
    public void testHelpTopic() throws Exception
    {

        final Receiver rcvr = new Receiver(2526);
        rcvr.register("testWithParms", new MyRequest(), new String[] {
                "--type String --key myParm --var myParm"
        });
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

    @Test
    public void testHelpTopicQuestionMark() throws Exception
    {

        final Receiver rcvr = new Receiver(2526);
        rcvr.register("testWithParms", new MyRequest(), new String[] {
                "--type String --key myParm --var myParm"
        });
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

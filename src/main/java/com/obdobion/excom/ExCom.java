package com.obdobion.excom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExCom
{

    private final static Logger logger = LoggerFactory.getLogger(ExCom.class.getName());

    static private String convertToParms(final String[] args)
    {
        final StringBuilder bldr = new StringBuilder();
        for (int s = 2; s < args.length; s++)
        {
            bldr.append(args[s]).append(' ');
        }
        return bldr.toString();
    }

    /**
     * This class expects an integer port number and a command name as the first
     * two parameters. The remainder of the parameters are transferred as is to
     * the receiver over a TCP/IP socket.
     *
     * @param args
     */
    public static void main(final String[] args)
    {
        try
        {
            final int port = Integer.parseInt(args[0]);
            final String cmd = args[1];
            final Sender sender = new Sender(port);
            final ExComContext result = sender.send(new ExComContext(cmd, convertToParms(args)));
            logger.info(result.toString());
            System.exit(0);
        } catch (final Exception e)
        {
            logger.error("", e);
            System.exit(-1);
        }
    }
}

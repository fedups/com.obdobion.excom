package com.obdobion.excom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.ParseException;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Receiver class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Receiver
{
    private final static Logger  logger         = LoggerFactory.getLogger(Receiver.class.getName());

    final private ExCom          excom;

    boolean                      running;
    ServerSocket                 serverSocket;
    ReceiverThread               socketWatcher;

    /**
     * Using this constructor assumes that you will be setting the port some
     * other way.
     *
     * @throws ParseException
     * @throws IOException
     */
    Receiver(final ExCom excom) throws IOException, ParseException
    {
        super();
        this.excom = excom;
    }

    public ExCom getExCom()
    {
        return excom;
    }

    /**
     * <p>
     * go.
     * </p>
     *
     * @throws java.io.IOException if any.
     */
    public void go() throws IOException
    {
        NDC.push(myNdcId());
        try
        {
            // if (getCommandsToBeRegistered() != null)
            // for (final CommandWrapper wrapper : getCommandsToBeRegistered())
            // try
            // {
            // register(wrapper.getTitle(), wrapper.getCommandName(),
            // wrapper.getCommand());
            // } catch (final ParseException e)
            // {
            // logger.error(wrapper.toString(), e);
            // }
            logger.info("initialized");
            running = true;
            final InetAddress addr = InetAddress.getByName(excom.getConfig().getReceiverHost());
            serverSocket = new ServerSocket(excom.getConfig().getSendReceivePort(), 50, addr);
            serverSocket.setSoTimeout(Integer.MAX_VALUE);
            socketWatcher = new ReceiverThread("ExComListener", this, serverSocket);
            logger.trace("started ReceiverThread");
            socketWatcher.start();
        } finally
        {
            NDC.pop();
        }
    }

    /**
     * <p>
     * isRunning.
     * </p>
     *
     * @return a boolean.
     */
    public boolean isRunning()
    {
        return running;
    }

    private String myNdcId()
    {
        return "excom@" + excom.getConfig().getReceiverHost() + ":"
                + excom.getConfig().getSendReceivePort();
    }

    /**
     * <p>
     * Setter for the field <code>running</code>.
     * </p>
     *
     * @param running a boolean.
     */
    public void setRunning(final boolean running)
    {
        this.running = running;
    }

    /**
     * <p>
     * stop.
     * </p>
     */
    public void stop()
    {
        NDC.push(myNdcId());
        try
        {
            logger.trace("stopping");
            /*
             * This little block is probably only needed for test cases. It
             * keeps the receiver from stopping before the command can make it
             * across from the sender. Test cases, especially when run with
             * no-block, can easily overrun the efficiency of the TCP
             * connection.
             */
            synchronized (this)
            {
                try
                {
                    wait(10);
                } catch (final InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            setRunning(false);
            try
            {
                if (serverSocket != null)
                {
                    logger.trace("server socket {}:{} closing",
                            excom.getConfig().getReceiverHost(),
                            excom.getConfig().getSendReceivePort());
                    serverSocket.close();
                }
            } catch (final IOException e1)
            {
                logger.info(e1.getMessage() + ":" + excom.getConfig().getSendReceivePort());
            }
            /*
             * Joining the socketWatcher will hang forever if it is an excom
             * command causing excom to stop. For instance, the app might have
             * an excom command called stop that calls this method.
             */
            /*-
            try
            {
                socketWatcher.join();
            } catch (final InterruptedException e)
            {
                log.error(e);
            }
             */

            int MAX_WAIT_MS = 1000;
            synchronized (this)
            {
                try
                {
                    logger.trace("interrupting any commands in process");
                    socketWatcher.interrupt();
                    while (socketWatcher.isRunning() && MAX_WAIT_MS > 0)
                    {
                        MAX_WAIT_MS -= 5;
                        wait(5);
                    }
                    if (MAX_WAIT_MS < 0)
                        logger.warn("aborting the current request since it did not end");
                } catch (final Exception e)
                {
                    logger.debug("waiting for ReceiverThread to end: {}", e.getMessage(), e);
                }
            }
            logger.info("stopped");
        } finally
        {
            NDC.pop();
        }
    }
}

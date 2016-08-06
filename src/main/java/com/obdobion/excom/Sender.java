package com.obdobion.excom;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sender
{
    private final static Logger logger = LoggerFactory.getLogger(Sender.class.getName());

    String                      host;
    int                         port   = 2526;
    Socket                      socket = null;

    public Sender(final int port)
    {
        this.port = port;
        this.host = "localhost";
    }

    private String communicateWithRemoteHost(final ExComContext context) throws IOException, SocketException
    {
        try
        {
            logger.trace("connecting");
            socket = new Socket(host, port);
            logger.trace("connected");

        } catch (final Exception e)
        {
            logger.error("connect to remote failed - {}", e.getMessage());
            return "The receiver on port " + port + " is not reachable at " + host;
        }
        socket.setSoTimeout(0);
        try
        {

            final ByteBuffer bb = ByteBuffer.allocate(4096);

            bb.put(context.commandName.trim().getBytes());
            bb.put((byte) 0x00);

            /*
             * Change the ReceiverThread convertToContext method if changing
             * what is being transmitted.
             */

            bb.put((byte) (context.logResult
                    ? '1'
                    : '0'));
            bb.put((byte) (context.wait
                    ? '1'
                    : '0'));
            bb.putLong(context.timeoutMS);

            if (context.commandArgs != null)
            {
                boolean firstTime = true;
                for (final String argLine : context.commandArgs)
                {
                    if (!firstTime)
                        bb.putChar(' ');
                    firstTime = false;
                    bb.put(argLine.trim().getBytes());
                }
            }

            logger.trace("sending {} bytes", bb.position());
            socket.getOutputStream().write(bb.array(), 0, bb.position());

            final byte[] b = new byte[4096];
            final StringBuilder output = new StringBuilder();
            int cnt = 0;
            int totalBytesReceived = 0;

            logger.trace("waiting for response");

            while (cnt != -1)
            {
                cnt = socket.getInputStream().read(b);
                if (cnt == -1)
                    break;
                totalBytesReceived += cnt;
                output.append(new String(b, 0, cnt));
            }

            logger.trace("{} bytes received", totalBytesReceived);

            return output.toString();
        } finally
        {
            logger.trace("closing connection");
            socket.shutdownOutput();
            socket.close();
        }
    }

    public ExComContext send(final ExComContext context) throws IOException
    {
        NDC.push(context.commandName.trim() + "@" + host + ":" + port);
        try
        {
            if (context.wait)
            {
                context.result = communicateWithRemoteHost(context);
            } else
            {
                final Thread remoteCommunicator = new Thread("ExComSubmit")
                {
                    @Override
                    public void run()
                    {
                        NDC.push(context.commandName.trim() + "@" + host + ":" + port + "-async");
                        try
                        {
                            logger.trace("running");
                            context.result = communicateWithRemoteHost(context);

                        } catch (final Exception e)
                        {
                            logger.error("exception while sending - {}", e.getMessage());
                        } finally
                        {
                            NDC.pop();
                        }
                    }
                };
                remoteCommunicator.start();
                logger.trace("scheduled in separate thread");
                context.result = "submitted";
            }
            return context;
        } finally
        {
            NDC.pop();
        }
    }

}

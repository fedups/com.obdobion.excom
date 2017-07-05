package com.obdobion.excom;

import java.io.Console;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.IPluginCommand;
import com.obdobion.excom.ui.module.Empty;
import com.obdobion.excom.ui.module.InteractiveConsole;
import com.obdobion.excom.ui.module.Quit;

/**
 * <p>
 * Sender class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Sender implements IPluginCommand
{
    private final static Logger logger = LoggerFactory.getLogger(Sender.class.getName());
    /** Constant <code>GROUP="RemoteControl"</code> */
    static public final String  GROUP  = InteractiveConsole.GROUP;
    /** Constant <code>NAME="connectToServer"</code> */
    static public final String  NAME   = "connectToServer";

    @Arg(shortName = 'h', defaultValues = "localhost", help = "The DNS name or the IP address of the remote server.")
    private String              host;

    @Arg(shortName = 'p',
            defaultValues = "2526",
            range = { "1025", "65535" },
            help = "The port on the remote host that is listening for these commands.")
    private int                 port;

    @Arg(shortName = 'n',
            caseSensitive = true,
            help = "A name used by this command to decorate the prompt.  If unspecified, this will be the same as --host.")
    private String              remoteName;

    @Arg(help = "Only allow the command to run up to this limit of milliseconds", defaultValues = "-1")
    private long                timeoutMS;

    @Arg
    private boolean             logResult;

    @Arg
    private boolean             asynchronous;

    ExComCommandContext                excomContext;

    private Thread              consoleInputThread;

    Socket                      socket = null;

    private boolean             stop;

    /**
     * <p>
     * Constructor for Sender.
     * </p>
     */
    public Sender()
    {}

    private int communicateWithRemoteHost() throws IOException, SocketException
    {
        logger.trace("connecting");
        socket = new Socket(host, port);
        logger.trace("connected");

        socket.setSoTimeout(0);
        try
        {

            final ByteBuffer bb = ByteBuffer.allocate(4096);

            bb.put(excomContext.commandName.trim().getBytes());
            bb.put((byte) 0x00);

            /*
             * Change the ReceiverThread convertToContext method if changing
             * what is being transmitted.
             */

            bb.put((byte) (excomContext.logResult
                    ? '1'
                    : '0'));
            bb.put((byte) (excomContext.block
                    ? '1'
                    : '0'));
            bb.putLong(excomContext.timeoutMS);

            if (excomContext.commandArgs != null)
            {
                boolean firstTime = true;
                for (final String argLine : excomContext.commandArgs)
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

            excomContext.result = output.toString();
            return totalBytesReceived;
        } finally
        {
            logger.trace("closing connection");
            socket.shutdownOutput();
            socket.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * send.
     * </p>
     */
    @Override
    public int execute(final ExcomContext p_context)
    {
        final ExcomContext context = p_context;
        logger.debug("interactive remote control console opened");

        setConsoleInputThread(new Thread()
        {
            @Override
            public void run()
            {
                setStop(false);

                final Console c = System.console();
                if (c == null)
                {
                    logger.error("the system console is not available");
                    System.err.println("No console.");
                    System.exit(1);
                    return;
                }

                if (remoteName == null)
                    remoteName = host;

                NDC.push(remoteName);
                try
                {
                    while (true)
                    {
                        if (isStop())
                            return;
                        final String aLine = c.readLine(remoteName + " > ");
                        if (aLine == null)
                            return;
                        processInputRequest(context, aLine.trim());
                    }
                } finally
                {
                    NDC.pop();
                }
            }
        });
        getConsoleInputThread().start();
        try
        {
            getConsoleInputThread().join();
        } catch (final InterruptedException e)
        {
            logger.debug("waiting for interactive console input", e);
        }
        logger.trace("interactive remote control console closed");
        return 0;
    }

    /**
     * <p>
     * Getter for the field <code>consoleInputThread</code>.
     * </p>
     *
     * @return a {@link java.lang.Thread} object.
     * @since 3.0.0
     */
    public Thread getConsoleInputThread()
    {
        return consoleInputThread;
    }

    /** {@inheritDoc} */
    @Override
    public String getGroup()
    {
        return GROUP;
    }

    /**
     * <p>
     * Getter for the field <code>host</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.0
     */
    public String getHost()
    {
        return host;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return NAME;
    }

    /** {@inheritDoc} */
    @Override
    public String getOverview()
    {
        return "Communicate with a server via a specified port.";
    }

    /**
     * <p>
     * Getter for the field <code>port</code>.
     * </p>
     *
     * @return a int.
     * @since 3.0.0
     */
    public int getPort()
    {
        return port;
    }

    /**
     * <p>
     * Getter for the field <code>remoteName</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.0
     */
    public String getRemoteName()
    {
        return remoteName;
    }

    /**
     * <p>
     * Getter for the field <code>socket</code>.
     * </p>
     *
     * @return a {@link java.net.Socket} object.
     * @since 3.0.0
     */
    public Socket getSocket()
    {
        return socket;
    }

    /**
     * <p>
     * isAsynchronous.
     * </p>
     *
     * @return a boolean.
     * @since 3.0.0
     */
    public boolean isAsynchronous()
    {
        return asynchronous;
    }

    /**
     * <p>
     * isLogResult.
     * </p>
     *
     * @return a boolean.
     * @since 3.0.0
     */
    public boolean isLogResult()
    {
        return logResult;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return false;
    }

    /**
     * <p>
     * isStop.
     * </p>
     *
     * @return a boolean.
     * @since 3.0.0
     */
    public boolean isStop()
    {
        return stop;
    }

    int processInputRequest(final ExcomContext context, final String inputRequest)
    {
        int bytesReceived = 0;
        try
        {
            excomContext = new ExComCommandContext();
            excomContext.logResult = logResult;
            excomContext.timeoutMS = timeoutMS;
            excomContext.block = !asynchronous;

            if (inputRequest.length() == 0)
            {
                excomContext.commandName = Empty.GROUP + "." + Empty.NAME;
                excomContext.commandArgs = new String[] { "" };
            } else
            {
                final int firstWordEnd = inputRequest.indexOf(' ');
                if (firstWordEnd <= 0)
                {
                    excomContext.commandName = inputRequest;
                    excomContext.commandArgs = new String[] { "" };

                } else
                {
                    excomContext.commandName = inputRequest.substring(0, firstWordEnd);
                    excomContext.commandArgs = new String[] { inputRequest.substring(firstWordEnd + 1) };
                }
            }

            if (excomContext.commandName.equalsIgnoreCase(Quit.NAME))
            {
                stop();
                return bytesReceived;
            }

            bytesReceived = communicateWithRemoteHost();
            context.getOutline().printf(excomContext.result);

        } catch (final Exception e)
        {
            context.getOutline().printf("%1$s", e.getMessage());
        }
        context.getOutline().reset();

        return bytesReceived;
    }

    /**
     * <p>
     * Setter for the field <code>asynchronous</code>.
     * </p>
     *
     * @param asynchronous a boolean.
     * @since 3.0.0
     */
    public void setAsynchronous(final boolean asynchronous)
    {
        this.asynchronous = asynchronous;
    }

    /**
     * <p>
     * Setter for the field <code>consoleInputThread</code>.
     * </p>
     *
     * @param consoleInputThread a {@link java.lang.Thread} object.
     * @since 3.0.0
     */
    public void setConsoleInputThread(final Thread consoleInputThread)
    {
        this.consoleInputThread = consoleInputThread;
    }

    /**
     * <p>
     * Setter for the field <code>host</code>.
     * </p>
     *
     * @param host a {@link java.lang.String} object.
     * @since 3.0.0
     */
    public void setHost(final String host)
    {
        this.host = host;
    }

    /**
     * <p>
     * Setter for the field <code>logResult</code>.
     * </p>
     *
     * @param logResult a boolean.
     * @since 3.0.0
     */
    public void setLogResult(final boolean logResult)
    {
        this.logResult = logResult;
    }

    /**
     * <p>
     * Setter for the field <code>port</code>.
     * </p>
     *
     * @param port a int.
     * @since 3.0.0
     */
    public void setPort(final int port)
    {
        this.port = port;
    }

    /**
     * <p>
     * Setter for the field <code>remoteName</code>.
     * </p>
     *
     * @param remoteName a {@link java.lang.String} object.
     * @since 3.0.0
     */
    public void setRemoteName(final String remoteName)
    {
        this.remoteName = remoteName;
    }

    /**
     * <p>
     * Setter for the field <code>socket</code>.
     * </p>
     *
     * @param socket a {@link java.net.Socket} object.
     * @since 3.0.0
     */
    public void setSocket(final Socket socket)
    {
        this.socket = socket;
    }

    /**
     * <p>
     * Setter for the field <code>stop</code>.
     * </p>
     *
     * @param stop a boolean.
     * @since 3.0.0
     */
    public void setStop(final boolean stop)
    {
        this.stop = stop;
    }

    void stop()
    {
        setStop(true);
    }

}

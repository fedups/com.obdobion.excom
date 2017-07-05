package com.obdobion.excom;

import java.io.Console;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.text.ParseException;

import org.apache.log4j.NDC;

import com.obdobion.excom.ui.ExComConfig;
import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.IPluginCommand;
import com.obdobion.excom.ui.PluginManager;
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
    /** Constant <code>GROUP="RemoteControl"</code> */
    static public final String GROUP  = InteractiveConsole.GROUP;
    /** Constant <code>NAME="connectToServer"</code> */
    static public final String NAME   = "connectToServer";
    TeleportedCommandContext   commandContext;

    private Thread             consoleInputThread;

    Socket                     socket = null;

    private boolean            stop;
    final private ExComConfig  config;

    /**
     * <p>
     * Constructor for Sender.
     * </p>
     *
     * @throws ParseException
     * @throws IOException
     */
    public Sender() throws IOException, ParseException
    {
        config = new ExComConfig(".");
    }

    private int communicateWithRemoteHost() throws IOException, SocketException
    {
        socket = new Socket(config.getReceiverHost(), config.getSendReceivePort());
        socket.setSoTimeout(0);
        try
        {

            final ByteBuffer bb = ByteBuffer.allocate(4096);

            bb.put(commandContext.commandName.trim().getBytes());
            bb.put((byte) 0x00);

            /*
             * Change the ReceiverThread convertToContext method if changing
             * what is being transmitted.
             */

            bb.put((byte) (commandContext.logResult ? '1' : '0'));
            bb.put((byte) (commandContext.block ? '1' : '0'));
            bb.putLong(commandContext.timeoutMS);

            if (commandContext.commandArgs != null)
            {
                boolean firstTime = true;
                for (final String argLine : commandContext.commandArgs)
                {
                    if (!firstTime)
                        bb.putChar(' ');
                    firstTime = false;
                    bb.put(argLine.trim().getBytes());
                }
            }

            socket.getOutputStream().write(bb.array(), 0, bb.position());

            final byte[] b = new byte[4096];
            final StringBuilder output = new StringBuilder();
            int cnt = 0;
            int totalBytesReceived = 0;

            while (cnt != -1)
            {
                cnt = socket.getInputStream().read(b);
                if (cnt == -1)
                    break;
                totalBytesReceived += cnt;
                output.append(new String(b, 0, cnt));
            }
            commandContext.result = output.toString();
            return totalBytesReceived;
        } finally
        {
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
    public int execute(final ExComContext context)
    {
        setConsoleInputThread(new Thread()
        {
            @Override
            public void run()
            {
                setStop(false);

                final Console c = System.console();
                if (c == null)
                {
                    System.err.println("No console.");
                    System.exit(1);
                    return;
                }

                NDC.push(config.getRemoteName());
                try
                {
                    while (true)
                    {
                        if (isStop())
                            return;
                        final String aLine = c.readLine(config.getRemoteName() + " > ");
                        if (aLine == null)
                            return;
                        try
                        {
                            processInputRequest(context, aLine.trim());
                        } catch (IOException | ParseException e)
                        {
                            e.printStackTrace();
                        }
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
        }
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

    public ExComContext processInputRequest(final String... args)
            throws IOException, ParseException
    {
        final StringBuilder sb = new StringBuilder();
        for (final String arg : args)
            sb.append(arg.trim()).append(" ");
        return processInputRequest(sb.toString());
    }

    ExComContext processInputRequest(final String inputRequest)
            throws IOException, ParseException
    {
        final ExComConfig config = new ExComConfig(".");
        final ExComContext context = PluginManager.createContext(
                config,
                new PluginManager(config));
        return processInputRequest(context, inputRequest);
    }

    ExComContext processInputRequest(final ExComContext context, final String inputRequest)
            throws IOException, ParseException
    {
        context.setBytesReceived(0);
        try
        {
            commandContext = new TeleportedCommandContext();
            commandContext.logResult = config.isLogResult();
            commandContext.timeoutMS = config.getTimeoutMS();
            commandContext.block = !config.isAsynchronous();

            if (inputRequest.length() == 0)
            {
                commandContext.commandName = Empty.GROUP + "." + Empty.NAME;
                commandContext.commandArgs = new String[] { "" };
            } else
            {
                final int firstWordEnd = inputRequest.indexOf(' ');
                if (firstWordEnd <= 0)
                {
                    commandContext.commandName = inputRequest;
                    commandContext.commandArgs = new String[] { "" };

                } else
                {
                    commandContext.commandName = inputRequest.substring(0, firstWordEnd);
                    commandContext.commandArgs = new String[] {
                            inputRequest.substring(firstWordEnd + 1) };
                }
            }

            if (commandContext.commandName.equalsIgnoreCase(Quit.NAME))
            {
                stop();
                return context;
            }

            context.setBytesReceived(communicateWithRemoteHost());
            context.getOutline().printf(commandContext.result);

        } catch (final Exception e)
        {
            context.getOutline().printf("%1$s", e.getMessage());
        }
        context.getOutline().reset();

        return context;
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

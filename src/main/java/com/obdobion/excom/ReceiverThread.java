package com.obdobion.excom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.usage.UsageBuilder;

/**
 * <p>
 * ReceiverThread class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class ReceiverThread extends Thread
{
    private final static Logger logger = LoggerFactory.getLogger(ReceiverThread.class.getName());

    List<Thread>                clientProcesses;
    private final Receiver      receiver;
    boolean                     running;
    protected ServerSocket      serverSocket;

    /**
     * <p>
     * Constructor for ReceiverThread.
     * </p>
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @param receiver
     *            a {@link com.obdobion.excom.Receiver} object.
     * @param serverSocket
     *            a {@link java.net.ServerSocket} object.
     */
    public ReceiverThread(final String name, final Receiver receiver, final ServerSocket serverSocket)
    {
        super(name);
        this.receiver = receiver;
        this.serverSocket = serverSocket;
        clientProcesses = new ArrayList<Thread>();
    }

    private void addClientProcess(final Thread cp)
    {
        synchronized (clientProcesses)
        {
            clientProcesses.add(cp);
        }
    }

    private ExComContext convertToContext(final byte[] b, final int length)
    {
        final ExComContext context = new ExComContext();

        final ByteBuffer request = ByteBuffer.wrap(b, 0, length);

        final int pivot = positionOf(request, (byte) 0x00);
        byte[] tmp = new byte[pivot];
        request.get(tmp, request.position(), pivot);
        context.commandName = new String(tmp);
        /*
         * consume the 0 byte
         */
        request.get();

        context.logResult = request.get() == (byte) '1';
        context.block = request.get() == (byte) '1';
        context.timeoutMS = request.getLong();

        /*
         * All command line occurrences were converted into a single context
         * commandArgs. Everywhere else on the receiver side only occurrence 0
         * is used.
         */
        if (request.remaining() > 0)
        {
            tmp = new byte[request.remaining()];
            request.get(tmp);
            context.commandArgs = new String[] { new String(tmp) };
        }

        return context;
    }

    private Thread createCommandThread(final ExComContext context)
    {
        final Stack<?> parentNDC = NDC.cloneStack();
        final Thread commandThread = new Thread(context.commandName + "_ExComCommand")
        {
            @Override
            public void run()
            {
                NDC.inherit(parentNDC);

                long start, end;
                start = System.currentTimeMillis();
                try
                {
                    context.clientCommand.duration = 0;
                    context.result = context.clientCommand.command.execute(context.clientCommand);
                    end = System.currentTimeMillis();
                    context.clientCommand.duration = end - start;
                } catch (final InterruptedException e)
                {
                } catch (final Exception e)
                {
                    logger.error(e.getMessage(), e);
                    context.result = e.getMessage();
                    end = System.currentTimeMillis();
                    context.clientCommand.duration = end - start;
                }
            }
        };
        return commandThread;
    }

    private ExComContext createContext(final Socket client, final StringBuilder out) throws Exception
    {
        final byte[] b = new byte[4096];
        final int cnt = client.getInputStream().read(b);

        final ExComContext context = convertToContext(b, cnt);

        if (cnt < 0)
        {
            showHelpTOC(out);
            return null;
        }

        NDC.push(context.commandName);
        try
        {

            if ("help".equalsIgnoreCase(context.commandName))
            {
                if (context.commandArgs == null)
                    showHelpTOC(out);
                else
                    showHelpTopic(context.commandArgs[0], out);

                return null;
            }

            if (context.commandArgs != null && context.commandArgs.length > 0)
                logger.info("args:{}", context.commandArgs[0]);

            final ClientCommand cc = getReceiver().clientCommands.get(context.commandName.toLowerCase());
            context.clientCommand = cc;

            if (cc == null)
                throw new Exception("unknown command named: " + context.commandName);

            if (context.commandArgs != null)
                cc.args.parse(cc.command, context.commandArgs);
            else
                /*
                 * So that defaults get applied and the variables are cleared on
                 * the command.
                 */
                cc.args.parse(cc.command);

            if (((CmdLine) cc.args).isUsageRun())
            {
                showHelpTopic(context.clientCommand.getCmdName(), out);
                return null;
            }

            return context;
        } finally
        {
            NDC.pop();
        }
    }

    private Thread createTimeoutWatcherThread(final ExComContext watchingContext, final Thread watchingThread)
    {
        return new Thread("ExComTimeoutWatcher")
        {
            @SuppressWarnings("deprecation")
            @Override
            public void run()
            {
                try
                {
                    long timeout = -1;
                    if (timeout == -1 && watchingContext.timeoutMS != -1)
                        timeout = watchingContext.timeoutMS;
                    if (timeout == -1 && watchingContext.clientCommand.getTimeoutMS() != -1)
                        timeout = watchingContext.clientCommand.getTimeoutMS();
                    if (timeout == -1)
                        timeout = 0;
                    watchingThread.join(timeout);
                    if (watchingThread.isAlive() && !watchingThread.isInterrupted() && timeout > 0)
                    {
                        logger.warn("command timed out");
                        watchingThread.interrupt();
                        watchingThread.stop();
                        watchingContext.clientCommand.duration = timeout;
                        watchingContext.result = "timed-out";
                    }
                } catch (final InterruptedException e)
                {
                    logger.warn("command interrupted");
                    watchingThread.stop();
                    watchingContext.result = "interrupted";
                } finally
                {
                    logger.trace("complete");
                }
            }
        };
    }

    private String fixedWidth(final String source, final int width)
    {
        final StringBuilder fw = new StringBuilder();
        fw.append(source);
        for (int w = width - source.length(); w > 0; w--)
            fw.append(' ');
        final String returnString = fw.toString();

        return returnString;
    }

    @SuppressWarnings("deprecation")
    private void forkAndTime(final Thread commandThread, final ExComContext context)
    {
        logger.trace("start");
        commandThread.start();
        final Thread timeoutWatcher = createTimeoutWatcherThread(context, commandThread);
        timeoutWatcher.start();
        if (context.block)
            try
            {
                timeoutWatcher.join();
            } catch (final InterruptedException e)
            {
                logger.warn("command interrupted");
                commandThread.stop();
                context.result = "interrupted";
            } finally
            {
            }
        else
        {
            context.result = "asynchronous";
            logger.trace("returning to caller without waiting for completion (async)");
        }
    }

    private void forkOffClientProcess(final Socket client)
    {
        ExComContext context = null;

        NDC.push(client.getRemoteSocketAddress().toString());
        try
        {
            logger.trace("connecting");

            final StringBuilder out = new StringBuilder();
            try
            {
                context = createContext(client, out);

            } catch (final Exception e1)
            {
                logger.error("reading socket", e1);
            }

            NDC.push(context == null ? "help" : context.commandName);
            try
            {
                if (!clientProcesses.isEmpty())
                {
                    out.append("busy - please try later");
                    try
                    {
                        logger.warn("concurrent request ignored");
                        client.getOutputStream().write(out.toString().getBytes());
                        client.shutdownOutput();

                    } catch (final Exception e)
                    {
                        logger.error("closing output on socket", e);
                    }
                    return;
                }

                final ExComContext contextForThread = context;
                final Stack<?> parentNDC = NDC.cloneStack();

                final Thread clientProcess = new Thread(
                        (context == null ? "help" : context.commandName) + "_ExComCommandTimeOutController")
                {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void run()
                    {
                        NDC.inherit(parentNDC);
                        Thread clientThread = null;
                        try
                        {
                            /*
                             * Help requests cause context to be null.
                             */
                            if (contextForThread != null)
                            {
                                clientThread = createCommandThread(contextForThread);
                                forkAndTime(clientThread, contextForThread);
                                out.append(contextForThread.result);
                            }
                        } catch (final Exception e)
                        {
                            String msg = e.getMessage();
                            if (msg == null)
                                msg = e.getClass().getSimpleName();
                            logger.warn(client + ": " + msg, e);
                            out.append(msg);
                        } finally
                        {
                            removeClientProcess(Thread.currentThread());
                            try
                            {
                                if (clientThread != null && clientThread.isAlive())
                                {
                                    clientThread.interrupt();
                                    clientThread.stop();
                                    out.append("interrupted");
                                }
                                client.getOutputStream().write(out.toString().getBytes());
                                client.shutdownOutput();
                            } catch (final IOException e)
                            {
                                logger.error("closing output on socket", e);
                            }
                        }
                    }
                };
                addClientProcess(clientProcess);
                clientProcess.start();
            } finally
            {
                NDC.pop();
            }
        } finally
        {
            NDC.pop();
        }
    }

    private Receiver getReceiver()
    {
        return receiver;
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
        return "excom@" + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort();
    }

    private void nl(final StringBuilder out)
    {
        out.append("\n");
    }

    int positionOf(final ByteBuffer request, final byte aByte)
    {

        for (int b = request.position(); b < request.limit(); b++)
            if (request.get(b) == aByte)
                return b;
        return -1;
    }

    private void removeClientProcess(final Thread cp)
    {

        synchronized (clientProcesses)
        {
            clientProcesses.remove(cp);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("deprecation")
    @Override
    public void run()
    {
        NDC.push(myNdcId());
        try
        {
            running = true;
            while (getReceiver().isRunning())
                try
                {
                    forkOffClientProcess(serverSocket.accept());
                } catch (final SocketTimeoutException e)
                {
                    logger.error("run()", e);
                    continue;
                } catch (final IOException e)
                {
                    if (!"socket closed".equals(e.getMessage()))
                        logger.warn(e.getMessage());
                    continue;
                }
        } finally
        {
            synchronized (clientProcesses)
            {
                for (final Thread oneProcess : clientProcesses)
                    if (oneProcess != null)
                    {
                        logger.warn("interrupting " + oneProcess.getName());
                        oneProcess.interrupt();
                        oneProcess.stop();
                    }
            }
            try
            {
                serverSocket.close();
            } catch (final IOException e)
            {
                logger.warn("run() - exception ignored", e);
            }
            serverSocket = null;
            running = false;
            NDC.pop();
        }

    }

    private void showHelpTOC(final StringBuilder out)
    {
        /*
         * Compute the largest cmdname for fixed width. And sort them for
         * display.
         */
        int maxWidth = 0;
        final Set<String> sortedNames = new TreeSet<String>();
        for (final ClientCommand cc : getReceiver().clientCommands.values())
        {
            final int cnw = cc.cmdName.length();
            sortedNames.add(cc.cmdName);
            if (cnw > maxWidth)
                maxWidth = cnw;
        }

        out.append("Commands");
        nl(out);
        out.append("--------");
        nl(out);

        for (final String sn : sortedNames)
        {
            final ClientCommand cc = getReceiver().clientCommands.get(sn.toLowerCase());
            out.append(fixedWidth(cc.cmdName, maxWidth));
            if (cc.title != null)
            {
                out.append(" - ");
                out.append(cc.title);
            }
            nl(out);
        }
        // hr(out);
    }

    private void showHelpTopic(final String key, final StringBuilder out)
    {

        // hr(out);
        // out.append("Help for \"").append(key).append("\"");
        // nl(out);
        // hr(out);

        final ClientCommand cc = getReceiver().clientCommands.get(key.toLowerCase());
        if (cc == null)
            out.append("unknown command");
        else
            out.append(UsageBuilder.getWriter(cc.args, true).toString());
        // nl(out);
        // hr(out);
    }
}

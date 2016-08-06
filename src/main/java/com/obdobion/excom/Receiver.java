package com.obdobion.excom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.ICmdLine;
import com.obdobion.excom.spring.CommandWrapper;
import com.obdobion.excom.standard.Dump;
import com.obdobion.excom.standard.Echo;
import com.obdobion.excom.standard.GC;
import com.obdobion.excom.standard.Kill;

public class Receiver
{
    private final static Logger  logger         = LoggerFactory.getLogger(Receiver.class.getName());

    Map<String, ClientCommand>   clientCommands = new Hashtable<String, ClientCommand>();

    /*
     * This list of commands must be populated before calling go. At go time the
     * commands will be registered. This is to facilitate spring loading.
     */
    private List<CommandWrapper> commandsToBeRegistered;

    String                       host;
    int                          port;
    boolean                      running;
    ServerSocket                 serverSocket;
    ReceiverThread               socketWatcher;

    /**
     * Using this constructor assumes that you will be setting the port some
     * other way.
     */
    public Receiver()
    {
        super();
        setHost("localhost");
        setPort(2526);
    }

    public Receiver(final int _port)
    {
        super();
        setHost("localhost");
        setPort(_port);
    }

    public Receiver(final String _host, final int _port)
    {
        super();
        setHost(_host);
        setPort(_port);
    }

    public ClientCommand createCommand(final String cmdName, final IExternalRequest cmd)
            throws ParseException, IOException
    {
        return createCommand(null, cmdName, cmd, (String[]) null);
    }

    public ClientCommand createCommand(final String cmdName, final IExternalRequest cmd, final String... parserDef)
            throws ParseException, IOException
    {
        return createCommand(null, cmdName, cmd, parserDef);
    }

    public ClientCommand createCommand(final String title, final String cmdName, final IExternalRequest cmd)
            throws ParseException, IOException
    {
        return createCommand(title, cmdName, cmd, (String[]) null);
    }

    public ClientCommand createCommand(
            final String title,
            final String cmdName,
            final IExternalRequest cmd,
            final String... parserDef) throws ParseException, IOException
    {
        final ICmdLine cmdLine = new CmdLine(cmdName);
        if (parserDef != null)
            /*
             * this probably means that annotations are being used and the
             * "parse" method will "compile" those instead of this explicit
             * compile.
             */
            cmdLine.compile(parserDef == null
                    ? new String[] {}
                    : parserDef);

        final ClientCommand cc = new ClientCommand(title, cmdName, cmdLine, cmd);
        clientCommands.put(cmdName.toLowerCase(), cc);

        return cc;
    }

    public List<CommandWrapper> getCommandsToBeRegistered()
    {
        return commandsToBeRegistered;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public void go() throws IOException
    {
        NDC.push(myNdcId());
        try
        {
            if (getCommandsToBeRegistered() != null)
                for (final CommandWrapper wrapper : getCommandsToBeRegistered())
                {
                    try
                    {
                        register(wrapper.getTitle(), wrapper.getCommandName(), wrapper.getCommand(),
                                wrapper.getParserDef());
                    } catch (final ParseException e)
                    {
                        logger.error(wrapper.toString(), e);
                    }
                }
            logger.info("initialized");
            running = true;
            final InetAddress addr = InetAddress.getByName(getHost());
            serverSocket = new ServerSocket(getPort(), 50, addr);
            serverSocket.setSoTimeout(Integer.MAX_VALUE);
            socketWatcher = new ReceiverThread("ExComListener", this, serverSocket);
            logger.trace("started ReceiverThread");
            socketWatcher.start();
        } finally
        {
            NDC.pop();
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    private String myNdcId()
    {
        return "excom@" + getHost() + ":" + getPort();
    }

    public Receiver register(final ClientCommand cc) throws ParseException, IOException
    {

        NDC.push(myNdcId());
        try
        {
            logger.debug("registering {}", cc.cmdName);
            clientCommands.put(cc.cmdName.toLowerCase(), cc);
            return this;
        } finally
        {
            NDC.pop();
        }
    }

    public Receiver register(final String cmdName, final IExternalRequest cmd) throws ParseException, IOException
    {
        return register(createCommand(cmdName, cmd));
    }

    public Receiver register(final String cmdName, final IExternalRequest cmd, final String... parserDef)
            throws ParseException, IOException
    {
        return register(createCommand(cmdName, cmd, parserDef));
    }

    public Receiver register(final String title, final String cmdName, final IExternalRequest cmd)
            throws ParseException, IOException
    {
        return register(createCommand(title, cmdName, cmd));
    }

    public Receiver register(
            final String title,
            final String cmdName,
            final IExternalRequest cmd,
            final String... parserDef) throws ParseException, IOException
    {
        return register(createCommand(title, cmdName, cmd, parserDef));
    }

    public Receiver registerStandard() throws ParseException, IOException
    {
        return registerStandard("");
    }

    public Receiver registerStandard(final String prefix) throws ParseException, IOException
    {
        ClientCommand cc;

        register(cc = createCommand("Garbage Collection", prefix + "GC", new GC()));
        cc.setTimeoutMS(60000);

        register(cc = createCommand("Stack trace", prefix + "Dump", new Dump()));
        cc.setTimeoutMS(1000);

        register(cc = createCommand("Application Shutdown", prefix + "Kill", new Kill()));
        cc.setTimeoutMS(60000);

        register(cc = createCommand("Echo to log", prefix + "Echo", new Echo()));
        cc.setTimeoutMS(100);

        return this;
    }

    public void setCommandsToBeRegistered(final List<CommandWrapper> commandsToBeRegistered)
    {
        this.commandsToBeRegistered = commandsToBeRegistered;
    }

    public void setHost(final String _host)
    {
        host = _host;
    }

    public void setPort(final int port)
    {
        this.port = port;
    }

    public void setRunning(final boolean running)
    {
        this.running = running;
    }

    public void stop()
    {
        NDC.push(myNdcId());
        try
        {
            logger.trace("stopping");
            /*
             * This little wait is probably only needed for test cases. It keeps
             * the receiver from stopping before the command can make it across
             * from the sender. Test cases, especially when run with no-wait,
             * can easily overrun the efficiency of the TCP connection.
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
                    logger.trace("server socket {}:{} closing", getHost(), getPort());
                    serverSocket.close();
                }
            } catch (final IOException e1)
            {
                logger.info(e1.getMessage() + ":" + getPort());
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

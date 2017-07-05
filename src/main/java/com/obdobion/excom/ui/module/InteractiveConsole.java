package com.obdobion.excom.ui.module;

import java.io.Console;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.HistoryManager;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * InteractiveConsole class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class InteractiveConsole implements IPluginCommand
{
    private final static Logger logger = LoggerFactory.getLogger(InteractiveConsole.class.getName());

    /** Constant <code>GROUP="IC"</code> */
    public static final String  GROUP  = "IC";
    /** Constant <code>NAME="interactiveConsole"</code> */
    public static final String  NAME   = "interactiveConsole";

    private ExcomContext             context;
    private Thread              consoleInputThread;
    private boolean             stop;

    /**
     * <p>
     * Constructor for InteractiveConsole.
     * </p>
     */
    public InteractiveConsole()
    {}

    /** {@inheritDoc} */
    @Override
    public int execute(final ExcomContext p_context)
    {
        context = p_context;
        context.setRecordingHistory(false);
        logger.debug("interactive console opened");

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

                NDC.push("IC");
                try
                {
                    context.getOutline().printf(
                            "\nWelcome to the interactive menu for howto.\nUse 'menu' to see the commands you can run.\nUse '<command> --help' for more information on a specific command.\nThis is a demonstration of the 'Argument' package.  Read more about it at %1$s.\n\n",
                            "https://github.com/fedups/com.obdobion.argument/wiki");

                    while (true)
                    {
                        if (isStop())
                            return;
                        final String aLine = c.readLine("howto > ");
                        if (aLine == null)
                            return;
                        processInputRequest(aLine.trim());
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
        logger.trace("interactive console closed");
        return 0;
    }

    /**
     * @return the consoleInputThread
     */
    Thread getConsoleInputThread()
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
        return "Interactive console mode";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return true;
    }

    /**
     * @return the stop
     */
    boolean isStop()
    {

        return stop;
    }

    private void processInputRequest(final String inputRequest)
    {
        String commandName;
        String arguments;

        if (inputRequest.length() == 0)
        {
            commandName = Empty.GROUP + "." + Empty.NAME;
            arguments = "";
        } else
        {
            final int firstWordEnd = inputRequest.indexOf(' ');
            if (firstWordEnd <= 0)
            {
                commandName = inputRequest;
                arguments = "";

            } else
            {
                commandName = inputRequest.substring(0, firstWordEnd);
                arguments = inputRequest.substring(firstWordEnd + 1);
            }
        }

        if (commandName.equalsIgnoreCase(Quit.NAME))
        {
            stop();
            return;
        }

        try
        {
            final ExcomContext subcommandContext = context.getPluginManager().run(context,
                    context.getPluginManager().uniqueNameFor(commandName),
                    arguments);
            HistoryManager.getInstance().record(subcommandContext);
            context.getOutline().print(context);

        } catch (final Exception e)
        {
            logger.error("{} unsuccessfull", commandName, e);
            context.getOutline().printf("\n\n%1$s\n\n", e.getMessage());
        }
        context.getOutline().reset();
    }

    /**
     * @param consoleInputThread the consoleInputThread to set
     */
    void setConsoleInputThread(final Thread consoleInputThread)
    {

        this.consoleInputThread = consoleInputThread;
    }

    /**
     * @param stop the stop to set
     */
    void setStop(final boolean stop)
    {

        this.stop = stop;
    }

    void stop()
    {

        setStop(true);
    }
}

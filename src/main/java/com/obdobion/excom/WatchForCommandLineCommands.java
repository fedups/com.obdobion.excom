package com.obdobion.excom;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obdobion.excom.ui.Config;
import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.HistoryManager;
import com.obdobion.excom.ui.PluginManager;
import com.obdobion.excom.ui.PluginNotFoundException;
import com.obdobion.excom.ui.module.Empty;

/**
 * <p>
 * App class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
final public class WatchForCommandLineCommands
{
    private final static Logger logger = LoggerFactory.getLogger(WatchForCommandLineCommands.class.getName());

    /**
     * <p>
     * destroyContext.
     * </p>
     *
     * @param context a {@link com.obdobion.excom.ui.ExcomContext} object.
     */
    static public void stop(final ExcomContext context)
    {
        if (context == null)
            return;
        context.getConsoleErrorOutput().flush();
    }

    private Config               config;
    private ExcomContext              context;

    private final PluginManager  pluginManager;

    private String               currentCommandName;
    private String[]             currentCommandLineArgs;

    private final HistoryManager histman;

    public WatchForCommandLineCommands(
            final String[] commandLineArgs,
            final String configDir)
    {
        try
        {
            config = new Config(configDir);

        } catch (IOException | ParseException e)
        {
            System.err.println("loading config: " + e.getMessage());
            System.exit(-1);
        }

        if (commandLineArgs.length == 0)
        {
            setCommandName(Empty.GROUP + "." + Empty.NAME);
            setCommandLineArgs(new String[0]);
        } else
        {
            setCommandName(commandLineArgs[0]);
            if (commandLineArgs.length == 1)
                setCommandLineArgs(new String[0]);
            else
                setCommandLineArgs(Arrays.copyOfRange(commandLineArgs, 1, commandLineArgs.length));
        }

        histman = new HistoryManager(config);
        pluginManager = new PluginManager(config);
        pluginManager.loadCommands();
    }

    private String[] getCommandLineArgs()
    {
        return currentCommandLineArgs;
    }

    private String getCommandName()
    {
        return currentCommandName;
    }

    public ExcomContext run()
    {
        try
        {
            NDC.push(getCommandName());
            context = pluginManager.run(getCommandName(), getCommandLineArgs());
            stop(context);

        } catch (PluginNotFoundException | IOException | ParseException e)
        {
            logger.error("unsuccessfull {}", e.getMessage(), e);
            System.err.println(e.getMessage());
            return context;
        } finally
        {
            histman.record(context);
            NDC.pop();
        }
        return context;
    }

    private void setCommandLineArgs(final String[] commandLineArgs)
    {
        currentCommandLineArgs = commandLineArgs;
    }

    private void setCommandName(final String commandName)
    {
        currentCommandName = commandName;
    }

}

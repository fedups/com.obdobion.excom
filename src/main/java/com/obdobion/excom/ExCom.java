package com.obdobion.excom;

import java.io.IOException;
import java.text.ParseException;

import com.obdobion.excom.ui.ExComConfig;
import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.HistoryManager;
import com.obdobion.excom.ui.PluginManager;

/**
 * <p>
 * App class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
final public class ExCom
{
    /**
     * <p>
     * destroyContext.
     * </p>
     *
     * @param context a {@link com.obdobion.excom.ui.ExComContext} object.
     */
    static public void stop(final ExComContext context)
    {
        if (context == null)
            return;
        context.getConsoleErrorOutput().flush();
    }

    private final ExComConfig config;

    private PluginManager     pluginManager;
    private Receiver          receiver;

    private HistoryManager    histman;

    public ExCom() throws IOException, ParseException
    {
        config = new ExComConfig(".");
    }

    public ExComConfig getConfig()
    {
        return config;
    }

    public Receiver startReceiveMode() throws IOException, ParseException
    {
        if (pluginManager != null)
            return receiver;

        histman = new HistoryManager(config);
        pluginManager = new PluginManager(config);
        pluginManager.loadCommands();
        receiver = new Receiver(this);
        receiver.go();
        return receiver;
    }

    public PluginManager getPlugInManager()
    {
        return pluginManager;
    }

    public Sender createSender() throws IOException, ParseException
    {
        return new Sender();
    }
}

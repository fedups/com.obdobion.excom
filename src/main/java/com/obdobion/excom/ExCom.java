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
    private final ExComConfig config;

    private PluginManager pluginManager;
    private Receiver      receiver;

    ExCom() throws IOException, ParseException
    {
        config = new ExComConfig(".");
    }

    public ExComConfig getConfig()
    {
        return config;
    }

    static public Receiver listenForExternalCommands() throws IOException, ParseException
    {
        return new ExCom().startReceiveMode();
    }

    private Receiver startReceiveMode() throws IOException, ParseException
    {
        if (pluginManager != null)
            return receiver;

        new HistoryManager(config);
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

    public static ExComContext sendExternalCommandToListener(final String[] args)
                    throws IOException, ParseException
    {
        return new Sender().processInputRequest(args);
    }

    public static ExComContext sendExternalCommandToListener(final String args)
                    throws IOException, ParseException
    {
        return new Sender().processInputRequest(args);
    }
}

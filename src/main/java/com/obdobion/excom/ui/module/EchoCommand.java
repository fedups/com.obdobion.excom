package com.obdobion.excom.ui.module;

import java.text.ParseException;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * EchoCommand class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class EchoCommand implements IPluginCommand
{
    /** Constant <code>GROUP="Utility"</code> */
    static final public String GROUP = "Utility";
    /** Constant <code>NAME="calc"</code> */
    static final public String NAME  = "echo";

    @Arg(positional = true,
            inList = { "off", "commandLine", "property", "xml" },
            help = "Toggle the redisplay of commands before they are executed.  Don't forget that you can abbreviate these choices.")
    private String             type;

    /**
     * <p>
     * Constructor for EchoCommand.
     * </p>
     */
    public EchoCommand()
    {}

    /** {@inheritDoc} */
    @Override
    public int execute(final ExcomContext context) throws ParseException
    {
        showCommand(context);
        return 0;
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
        return "See what different formats Argument can handle by displaying them for each command.  Use this link to read about it.  https://github.com/fedups/com.obdobion.argument/wiki";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return false;
    }

    private void showCommand(final ExcomContext context)
    {
        context.getOutline().printf("echo set to %s\n", type);
        context.getPluginManager().setEchoType(type);
    }

}

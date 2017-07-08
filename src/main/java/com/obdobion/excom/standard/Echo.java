package com.obdobion.excom.standard;

import java.text.ParseException;

import org.apache.log4j.Logger;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * Echo class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Echo implements IPluginCommand
{
    static final private Logger logger = Logger.getLogger(Echo.class.getName());

    @Arg(shortName = 'm',
            positional = true,
            required = true,
            help = "A message that will be logged in the server's output.")
    private String              message;

    public int execute(final ExComContext context) throws ParseException
    {
        logger.info(message.trim());
        context.getOutline().printf(message);
        return 0;
    }

    public String getGroup()
    {
        return "System";
    }

    public String getName()
    {
        return "Echo";
    }

    public String getOverview()
    {
        return "Echo the message back to the console.";
    }

    public boolean isOnceAndDone()
    {
        return false;
    }
}

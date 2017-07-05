package com.obdobion.excom.ui.module;

import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * Menu class.
 * </p>
 * This is a placeholder class in that it does nothing in the execute method.
 * Its purpose is to stop the interactive session. The InteractiveConsole looks
 * for the name of this command to stop. But by having this IPluginCommand it
 * will appear in the menu.
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Quit implements IPluginCommand
{
    /** Constant <code>GROUP="InteractiveConsole.GROUP"</code> */
    static public final String GROUP = InteractiveConsole.GROUP;
    /** Constant <code>NAME="quit"</code> */
    static public final String NAME  = "quit";

    /**
     * <p>
     * Constructor for Quit.
     * </p>
     */
    public Quit()
    {}

    /** {@inheritDoc} */
    @Override
    public int execute(final ExComContext context)
    {
        context.setRecordingHistory(false);
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
        return "Ends an interactive session";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return true;
    }
}

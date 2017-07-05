package com.obdobion.excom.ui.module;

import com.obdobion.excom.ui.ExcomContext;
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
public class Empty implements IPluginCommand
{
    /** Constant <code>GROUP="Menu.GROUP"</code> */
    static public final String GROUP = Menu.GROUP;
    /** Constant <code>NAME="empty"</code> */
    static public final String NAME  = "empty";

    /**
     * <p>
     * Constructor for Quit.
     * </p>
     */
    public Empty()
    {}

    /** {@inheritDoc} */
    @Override
    public int execute(final ExcomContext context)
    {
        context.setRecordingHistory(false);
        context.getOutline().printf("\n\nTry \"menu\" or \"menu --help\"\n\n");
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
        return "Shows a minimal help message";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return false;
    }
}

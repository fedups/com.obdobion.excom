package com.obdobion.excom.ui.module;

import java.util.List;
import java.util.regex.Pattern;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.HistoryManager;
import com.obdobion.excom.ui.HistoryManager.HistoryRecord;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * History class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class History implements IPluginCommand
{
    /** Constant <code>GROUP="Menu.GROUP"</code> */
    static public final String  GROUP  = Menu.GROUP;
    /** Constant <code>NAME="history"</code> */
    static public final String  NAME   = "history";

    @Arg(shortName = 'm', help = "Only history matching all patterns will be displayed.", caseSensitive = true)
    private Pattern[]           matches;

    @Arg(shortName = 'c', help = "Limit to this many rows of output.", range = { "1" }, defaultValues = "10")
    private int                 count;

    /**
     * <p>
     * Constructor for History.
     * </p>
     */
    public History()
    {}

    private boolean allMatchersMatch(final String output)
    {
        if (matches == null)
            return true;
        for (final Pattern pattern : matches)
            if (!pattern.matcher(output).find())
                return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int execute(final ExComContext context)
    {
        int outputCount = 0;
        int startingPoint = 0;
        context.setRecordingHistory(false);
        final List<HistoryRecord> history = HistoryManager.getInstance().getHistory();
        /*
         * Scan backwards to know where to start showing when going in the
         * forward direction.
         */
        for (startingPoint = history.size(); startingPoint > 0; startingPoint--)
        {
            final String output = history.get(startingPoint - 1).getContents();
            if (allMatchersMatch(output))
                if (++outputCount == count)
                    break;
        }

        outputCount = 0;
        for (; startingPoint < history.size(); startingPoint++)
        {
            final String output = history.get(startingPoint).getContents();
            if (allMatchersMatch(output))
                context.getOutline().printf("%1$d: %2$s\n", startingPoint + 1, output);
        }
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
        return "Show / modify the history of commands";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return false;
    }

}

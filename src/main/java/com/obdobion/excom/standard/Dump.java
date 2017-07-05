package com.obdobion.excom.standard;

import java.text.ParseException;
import java.util.Map;
import java.util.regex.Pattern;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * Dump class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Dump implements IPluginCommand
{
    @Arg(shortName = 'm',
            longName = "matches",
            caseSensitive = true,
            multimin = 1,
            help = "Java Patterns anded together that compare to the Thread header only.")
    private Pattern[] threadPattern;

    @Arg(shortName = 'v',
            help = "Inverts the selection based on -m.  Only valid with -m")
    private boolean   invertMatches;

    public int execute(final ExComContext context) throws ParseException
    {
        final StringBuilder out = new StringBuilder();
        final Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();

        nextThread: for (final Thread thr : map.keySet())
        {
            if (threadPattern != null)
            {
                boolean matched = false;
                nextPattern: for (final Pattern pat : threadPattern)
                {
                    if (pat.matcher(thr.getThreadGroup().getName()).find()
                            || pat.matcher(thr.getName()).find()
                            || pat.matcher(thr.getState().name()).find())
                    {
                        matched = true;
                        continue nextPattern;
                    } else
                    {
                        matched = false;
                        break nextPattern;
                    }
                }
                if (invertMatches && matched)
                    continue nextThread;
                if (!invertMatches && !matched)
                    continue nextThread;
            }

            out.append(thr.getId());
            out.append(" ");
            out.append(thr.getThreadGroup().getName());
            out.append(":");
            out.append(thr.getName());
            out.append(" ");
            out.append(thr.getState().name());
            for (final StackTraceElement tra : map.get(thr))
            {
                out.append("\n\t");
                out.append(tra.toString());
            }
            out.append("\n");
        }
        context.getOutline().printf(out.toString());
        return 0;
    }

    public String getGroup()
    {
        return "System";
    }

    public String getName()
    {
        return "Dump";
    }

    public String getOverview()
    {
        return "Produce a stack trace report for the process";
    }

    public boolean isOnceAndDone()
    {
        return false;
    }
}

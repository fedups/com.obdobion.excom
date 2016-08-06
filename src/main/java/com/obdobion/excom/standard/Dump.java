package com.obdobion.excom.standard;

import java.util.Map;
import java.util.regex.Pattern;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ClientCommand;
import com.obdobion.excom.IExternalRequest;

public class Dump implements IExternalRequest
{
    @Arg(shortName = 'm',
            longName = "matches",
            caseSensitive = true,
            multimin = 1,
            help = "Java Patterns anded together that compare to the Thread header only.")
    private Pattern[] threadPattern;

    @Arg(shortName = 'v',
            longName = "invertMatches",
            defaultValues = "false",
            help = "Inverts the selection based on -m.  Only valid with -m")
    private boolean   invertMatches;

    public Dump()
    {
    }

    public String execute(final ClientCommand cc) throws Exception
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
        return out.toString();
    }
}

package com.obdobion.excom.standard;

import java.text.DecimalFormat;
import java.text.ParseException;

import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * GC class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class GC implements IPluginCommand
{
    static final private DecimalFormat MemFmt = new DecimalFormat("###,###,###,###");

    public int execute(final ExComContext context) throws ParseException
    {

        final long before = Runtime.getRuntime().freeMemory();
        System.gc();
        final long after = Runtime.getRuntime().freeMemory();

        final StringBuilder results = new StringBuilder();
        results.append("free memory before(");
        results.append(MemFmt.format(before));
        results.append(") after(");
        results.append(MemFmt.format(after));
        results.append(") reclaimed(");
        results.append(MemFmt.format(after - before));
        results.append(")");
        context.getOutline().printf(results.toString());

        return 0;
    }

    public String getGroup()
    {
        return "System";
    }

    public String getName()
    {
        return "GarbageCollect";
    }

    public String getOverview()
    {
        return "Run a system garbage collection.";
    }

    public boolean isOnceAndDone()
    {
        return false;
    }
}

package com.obdobion.excom.standard;

import java.text.DecimalFormat;

import com.obdobion.excom.ClientCommand;
import com.obdobion.excom.IExternalRequest;

public class GC implements IExternalRequest
{

    static final private DecimalFormat MemFmt = new DecimalFormat("###,###,###,###");

    public GC()
    {

    }

    public String execute(final ClientCommand cc) throws Exception
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

        return results.toString();
    }

}

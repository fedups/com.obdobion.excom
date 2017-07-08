package com.obdobion.excom.standard;

import java.text.ParseException;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExComContext;
import com.obdobion.excom.ui.IPluginCommand;

/**
 * <p>
 * Kill class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Kill implements IPluginCommand
{
    @Arg(longName = "exitCode",
            positional = true,
            inList = { "0", "9" },
            help = "0 causes a normal end of the JVM with all finalizers.  9 causes a halt of the JVM.  Instead of 3 for a dump, use the System.Dump command instead.")
    private int     exitCode;

    @Arg(shortName = 'c', required = true)
    private boolean confirm;

    public int execute(final ExComContext context) throws ParseException
    {
        switch (exitCode)
        {
            case 0:
                (new Thread("Kill")
                {
                    @Override
                    public void run()
                    {
                        System.exit(exitCode);
                    }
                }).start();
                break;
            case 9:
                (new Thread("Kill")
                {
                    @Override
                    public void run()
                    {
                        Runtime.getRuntime().halt(exitCode);
                    }
                }).start();
                break;
            default:
                context.getOutline().printf("kill %d is not a supported exit code", exitCode);
                return -1;
        }

        return 0;
    }

    public String getGroup()
    {
        return "System";
    }

    public String getName()
    {
        return "Kill";
    }

    public String getOverview()
    {
        return "Send a termination code to the process";
    }

    public boolean isOnceAndDone()
    {
        return false;
    }
}

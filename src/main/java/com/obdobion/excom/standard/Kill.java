package com.obdobion.excom.standard;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ClientCommand;
import com.obdobion.excom.IExternalRequest;

/**
 * <p>Kill class.</p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Kill implements IExternalRequest
{
    @Arg(longName = "exitCode",
            positional = true,
            inList = { "0", "9" },
            help = "0 causes a normal end of the JVM with all finalizers.  9 causes a halt of the JVM.")
    private int     exitCode;

    @Arg(shortName = 'c', required = true)
    private boolean confirm;

    /** {@inheritDoc} */
    public String execute(final ClientCommand cc) throws Exception
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
            return "kill " + exitCode + " is not a supported exit code";
        }

        return "ok";
    }
}

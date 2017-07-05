package com.obdobion.excom.ui.module;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Pattern;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.IPluginCommand;
import com.obdobion.excom.ui.PluginNotFoundException;

/**
 * <p>
 * Menu class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Menu implements IPluginCommand
{
    /** Constant <code>GROUP="System"</code> */
    static public final String GROUP              = "System";
    /** Constant <code>NAME="menu"</code> */
    static public final String NAME               = "menu";

    @Arg(shortName = 'm', help = "Only commands matching all patterns will be displayed.")
    private Pattern[]          matches;

    @Arg(allowCamelCaps = true, shortName = 's')
    private boolean            sortDescending;

    int                        longestGroupLength = 6;
    int                        longestNameLength  = 8;

    /**
     * <p>
     * Constructor for Menu.
     * </p>
     */
    public Menu()
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
    public int execute(final ExcomContext context)
    {
        context.setRecordingHistory(false);

        final List<String> keys = context.getPluginManager().allNames();
        keys.sort((o1, o2) -> {
            if (sortDescending)
                return o2.compareTo(o1);
            return o1.compareTo(o2);
        });

        keys.forEach(pluginName -> {
            try
            {
                final IPluginCommand pluginCommand = context.getPluginManager().get(pluginName);
                if (pluginCommand.getGroup().length() > longestGroupLength)
                    longestGroupLength = pluginCommand.getGroup().length();
                if (pluginCommand.getName().length() > longestNameLength)
                    longestNameLength = pluginCommand.getName().length();

            } catch (final PluginNotFoundException e)
            {}
        });

        context.getOutline().printf(
                "\n\nThank you for using howto. Read more about related open-source software...\n\n%1$s at %2$s\n%3$s at %4$s\n%5$s at %6$s\n\n",
                "Argument ", "https://github.com/fedups/com.obdobion.argument/wiki",
                "Algebrain", "https://github.com/fedups/com.obdobion.algebrain/wiki",
                "Calendar ", "https://github.com/fedups/com.obdobion.calendar/wiki");

        final String headerLayout = "%1$-" + longestGroupLength + "s %2$-" + longestNameLength + "s %3$s\n";
        final String detailLayout = "%1$-" + longestGroupLength + "s %2$-" + longestNameLength + "s %3$s\n";

        context.getOutline().printf(headerLayout, "Group", "Command", "Overview");
        context.getOutline().printf(headerLayout, "-----", "-------", "--------");

        keys.forEach(pluginName -> {
            try
            {
                final StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw))
                {
                    final IPluginCommand pluginCommand = context.getPluginManager().get(pluginName);

                    pw.printf(detailLayout, pluginCommand.getGroup(), pluginCommand.getName(),
                            pluginCommand.getOverview());
                    final String output = sw.toString();

                    if (allMatchersMatch(output)
                            && !(pluginCommand.getGroup().equals(Empty.GROUP)
                                    && pluginCommand.getName().equals(Empty.NAME)))
                    {
                        context.getOutline().printf(longestGroupLength + 1 + longestNameLength + 1, sw.toString());
                        context.getOutline().printf("\n");
                    }
                }
            } catch (final PluginNotFoundException e)
            {}
        });

        context.getOutline().printf("\n");
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
        return "Shows a list of available commands.  Use \"--help\" as an argument to any command to get details on that command."
                + "  Commands can be just the name or group.name, "
                + "  they can be abbreviated, or only caps (camelCaps), as long as what you enter can uniquely identify a command."
                + " CamelCaps are the first letter followed by any remaining capital letters and numbers.";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return false;
    }
}

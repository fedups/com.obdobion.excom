package com.obdobion.excom.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.log4j.NDC;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.type.AbstractCLA;

/**
 * <p>
 * PluginManager class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class PluginManager
{
    /**
     * <p>
     * createContext.
     * </p>
     *
     * @param config a {@link com.obdobion.excom.ui.ExComConfig} object.
     * @param pluginManager a {@link com.obdobion.excom.ui.PluginManager} object.
     * @return a {@link com.obdobion.excom.ui.ExComContext} object.
     */
    static public ExComContext createContext(final ExComConfig config, final PluginManager pluginManager)
    {
        final ExComContext context = new ExComContext();
        context.setRecordingHistory(true);
        context.setOutline(new Outline(config));
        context.setPluginManager(pluginManager);
        context.setConsoleErrorOutput(new PrintWriter(System.err));
        return context;
    }

    /**
     * <p>
     * createContext.
     * </p>
     *
     * @param currentContextToCloneFrom a {@link com.obdobion.excom.ui.ExComContext}
     *            object.
     * @return a {@link com.obdobion.excom.ui.ExComContext} object.
     */
    static public ExComContext createContext(final ExComContext currentContextToCloneFrom)
    {
        final ExComContext context = new ExComContext();
        context.setRecordingHistory(true);
        context.setSubcontext(true);
        context.setOutline(currentContextToCloneFrom.getOutline().getCurrent());
        context.setPluginManager(currentContextToCloneFrom.getPluginManager());
        context.setConsoleErrorOutput(currentContextToCloneFrom.getConsoleErrorOutput());
        return context;
    }

    final ExComConfig         config;
    List<IPluginCommand> allPlugins;
    private String       echoType;

    /**
     * <p>
     * Constructor for PluginManager.
     * </p>
     *
     * @param config a {@link com.obdobion.excom.ui.ExComConfig} object.
     */
    public PluginManager(final ExComConfig config)
    {
        this.config = config;
    }

    /**
     * <p>
     * allNames.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> allNames()
    {
        final List<String> names = new ArrayList<>();
        allPlugins.forEach(plugin -> names.add(plugin.getName()));
        return names;
    }

    private void echoCommand(final ExComContext context)
    {
        final StringBuilder sb = new StringBuilder();
        if (echoType.equals("commandline"))
            context.getParser().exportCommandLine(sb);
        else if (echoType.equals("property"))
            context.getParser().exportNamespace("", sb);
        else if (echoType.equals("xml"))
            context.getParser().exportXml(sb);
        context.getOutline().printf("\n%1s\n\n", sb.toString());
    }

    /**
     * <p>
     * get.
     * </p>
     *
     * @param commandId a {@link java.lang.String} object.
     * @return a {@link com.obdobion.excom.ui.IPluginCommand} object.
     * @throws com.obdobion.excom.ui.PluginNotFoundException if any.
     */
    public IPluginCommand get(final String commandId) throws PluginNotFoundException
    {
        if (allPlugins == null)
            throw new PluginNotFoundException("plugins not loaded");
        if (commandId == null || commandId.trim().length() == 0)
            throw new PluginNotFoundException("get NULL is not supported");

        final int dot = commandId.indexOf('.');
        String group = null;
        String name;
        if (dot < 0 || commandId.length() == 1)
            name = commandId;
        else
        {
            group = commandId.substring(0, dot);
            name = commandId.trim().substring(dot + 1);
        }

        IPluginCommand[] foundMatches;

        final String finalGroup = group;
        foundMatches = allPlugins.stream()
                .filter(plugin -> {
                    /*
                     * Exact on group and name, first get the names to be same
                     * length and lowercase.
                     */
                    final String normalizedName = name.toLowerCase();
                    String normalizedPluginName = plugin.getName().toLowerCase();
                    if (normalizedPluginName.length() > normalizedName.length())
                        normalizedPluginName = normalizedPluginName.substring(0, normalizedName.length());
                    String normalizedPluginGroup = plugin.getGroup().toLowerCase();
                    final String normalizedGroup = finalGroup == null
                            ? normalizedPluginGroup
                                    : finalGroup.toLowerCase();
                    if (normalizedPluginGroup.length() > normalizedGroup.length())
                        normalizedPluginGroup = normalizedPluginGroup.substring(0, normalizedGroup.length());

                    return normalizedName.equalsIgnoreCase(normalizedPluginName)
                            && normalizedGroup.equalsIgnoreCase(normalizedPluginGroup);
                })
                .toArray(size -> new IPluginCommand[size]);
        if (foundMatches.length == 1)
            return foundMatches[0];

        foundMatches = allPlugins.stream()
                .filter(plugin -> {
                    /*
                     * CamelCap on group and exact name, get the names to be
                     * same length and lowercase too.
                     */
                    final String normalizedName = name.toLowerCase();
                    String normalizedPluginName = plugin.getName().toLowerCase();
                    if (normalizedPluginName.length() > normalizedName.length())
                        normalizedPluginName = normalizedPluginName.substring(0, normalizedName.length());
                    String normalizedPluginGroup = AbstractCLA.createCamelCapVersionOfKeyword(plugin.getGroup())
                            .toLowerCase();
                    final String normalizedGroup = finalGroup == null
                            ? normalizedPluginGroup
                                    : AbstractCLA.createCamelCapVersionOfKeyword(finalGroup).toLowerCase();
                    if (normalizedPluginGroup.length() > normalizedGroup.length())
                        normalizedPluginGroup = normalizedPluginGroup.substring(0, normalizedGroup.length());

                    return normalizedName.equalsIgnoreCase(normalizedPluginName)
                            && normalizedGroup.equalsIgnoreCase(normalizedPluginGroup);
                })
                .toArray(size -> new IPluginCommand[size]);
        if (foundMatches.length == 1)
            return foundMatches[0];

        foundMatches = allPlugins.stream()
                .filter(plugin -> {
                    /*
                     * Exact group and CamelCap on name, get the names to be
                     * same length and lowercase too.
                     */
                    final String normalizedName = AbstractCLA.createCamelCapVersionOfKeyword(name).toLowerCase();
                    String normalizedPluginName = AbstractCLA.createCamelCapVersionOfKeyword(plugin.getName())
                            .toLowerCase();
                    if (normalizedPluginName.length() > normalizedName.length())
                        normalizedPluginName = normalizedPluginName.substring(0, normalizedName.length());
                    String normalizedPluginGroup = plugin.getGroup().toLowerCase();
                    final String normalizedGroup = finalGroup == null
                            ? normalizedPluginGroup
                                    : finalGroup.toLowerCase();
                    if (normalizedPluginGroup.length() > normalizedGroup.length())
                        normalizedPluginGroup = normalizedPluginGroup.substring(0, normalizedGroup.length());

                    return normalizedName.equalsIgnoreCase(normalizedPluginName)
                            && normalizedGroup.equalsIgnoreCase(normalizedPluginGroup);
                })
                .toArray(size -> new IPluginCommand[size]);
        if (foundMatches.length == 1)
            return foundMatches[0];

        foundMatches = allPlugins.stream()
                .filter(plugin -> {
                    /*
                     * CamelCap on group and CamelCap on name, get the names to
                     * be same length and lowercase too.
                     */
                    final String normalizedName = AbstractCLA.createCamelCapVersionOfKeyword(name).toLowerCase();
                    String normalizedPluginName = AbstractCLA.createCamelCapVersionOfKeyword(plugin.getName())
                            .toLowerCase();
                    if (normalizedPluginName.length() > normalizedName.length())
                        normalizedPluginName = normalizedPluginName.substring(0, normalizedName.length());
                    String normalizedPluginGroup = AbstractCLA.createCamelCapVersionOfKeyword(plugin.getGroup())
                            .toLowerCase();
                    final String normalizedGroup = finalGroup == null
                            ? normalizedPluginGroup
                                    : AbstractCLA.createCamelCapVersionOfKeyword(finalGroup).toLowerCase();
                    if (normalizedPluginGroup.length() > normalizedGroup.length())
                        normalizedPluginGroup = normalizedPluginGroup.substring(0, normalizedGroup.length());

                    return normalizedName.equalsIgnoreCase(normalizedPluginName)
                            && normalizedGroup.equalsIgnoreCase(normalizedPluginGroup);
                })
                .toArray(size -> new IPluginCommand[size]);
        if (foundMatches.length == 1)
            return foundMatches[0];

        foundMatches = allPlugins.stream()
                .filter(plugin -> name.equalsIgnoreCase(plugin.getName()))
                .toArray(size -> new IPluginCommand[size]);
        if (foundMatches.length == 1)
            return foundMatches[0];

        throw new PluginNotFoundException(
                "A unique command was not found for \"" + name
                + "\".  Use 'menu' to see valid commands.");
    }

    /**
     * <p>
     * loadCommands.
     * </p>
     */
    public void loadCommands()
    {
        /*
         * The pluginClassLoader not only includes all of the extension plugin
         * jars but also the modules in this app itself.
         */
        allPlugins = new ArrayList<>();
        loadPlugins("plugin", config.getPluginClassLoader());
    }

    void loadPlugins(final String category, ClassLoader classLoader)
    {
        if (classLoader == null)
            classLoader = this.getClass().getClassLoader();

        ServiceLoader.load(IPluginCommand.class, classLoader).forEach(
                plugin -> {
                    if (plugin.getName() != null)
                        allPlugins.add(plugin);
                });
    }

    private ExComContext privateRun(final ExComContext context, final String commandName, final String... args)
            throws PluginNotFoundException, IOException, ParseException
    {
        final IPluginCommand command = get(commandName);

        NDC.push(command.getName());

        try
        {
            context.setOriginalUserInput(ExComContext.convertToString(args));
            context.setParser(new CmdLine(command.getName(), command.getOverview()));
            if (args != null)
                CmdLine.load(context.getParser(), command, args);
            if (((CmdLine) context.getParser()).isUsageRun())
                return context;

            final StringBuilder loggableArgs = new StringBuilder();
            context.getParser().exportCommandLine(loggableArgs);

            if (command.isOnceAndDone())
                remove(command);

            if (echoType != null && !echoType.equals("off"))
                echoCommand(context);

            context.setStartTime(System.nanoTime());
            command.execute(context);
            return context;
        } finally
        {
            context.setEndTime(System.nanoTime());
            NDC.pop();
        }
    }

    boolean remove(final IPluginCommand command)
    {
        if (allPlugins == null)
            return true;
        final boolean rc = allPlugins.remove(command);
        return rc;
    }

    /**
     * <p>
     * run.
     * </p>
     * Indicates that this is a sub howto. It will be inserted at this point. If
     * it is not a loaded plugin then a simple "SEE" reference will be written
     * instead.
     *
     * @param parentContext a {@link com.obdobion.excom.ui.ExComContext} object.
     * @param commandName a {@link java.lang.String} object.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     * @return a {@link com.obdobion.excom.ui.ExComContext} object.
     * @throws java.text.ParseException if any.
     * @throws java.io.IOException if any.
     * @throws com.obdobion.excom.ui.PluginNotFoundException if any.
     */
    public ExComContext run(final ExComContext parentContext, final String commandName, final String format, final Object... args)
            throws PluginNotFoundException, IOException, ParseException
    {
        final ExComContext context = createContext(parentContext);
        String cmdlineArgs = null;

        final StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw))
        {
            pw.printf(format, args);
            cmdlineArgs = sw.toString();
        }
        return privateRun(context, commandName, cmdlineArgs);
    }

    /**
     * <p>
     * run.
     * </p>
     *
     * @param commandName a {@link java.lang.String} object.
     * @param args a {@link java.lang.String} object.
     * @return a {@link com.obdobion.excom.ui.ExComContext} object.
     * @throws com.obdobion.excom.ui.PluginNotFoundException if any.
     * @throws java.io.IOException if any.
     * @throws java.text.ParseException if any.
     */
    public ExComContext run(final String commandName, final String... args)
            throws PluginNotFoundException, IOException, ParseException
    {
        return privateRun(createContext(config, this), commandName, args);
    }

    public void setEchoType(final String type)
    {
        echoType = type;
    }

    /**
     * <p>
     * uniqueNameFor.
     * </p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String uniqueNameFor(final String name)
    {
        try
        {
            get(name);
            return name;
        } catch (final PluginNotFoundException e)
        {
            return "System." + name;
        }
    }

    /**
     * <p>
     * uniqueNameFor.
     * </p>
     *
     * @param group a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String uniqueNameFor(final String group, final String name)
    {
        try
        {
            get(name);
            return name;
        } catch (final PluginNotFoundException e)
        {
            return group + "." + name;
        }
    }
}

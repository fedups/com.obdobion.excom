package com.obdobion.excom;

/**
 * <p>
 * ExComContext class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class TeleportedCommandContext
{
    String[]                commandArgs;
    String                  commandName;
    boolean                 logResult;
    String                  result;
    long                    timeoutMS;
    boolean                 block;

    /**
     * <p>
     * Constructor for ExComContext.
     * </p>
     */
    public TeleportedCommandContext()
    {
        block = true;
        timeoutMS = -1;
    }

    /**
     * <p>
     * Constructor for ExComContext.
     * </p>
     *
     * @param commandName a {@link java.lang.String} object.
     * @param commandArgs a {@link java.lang.String} object.
     * @param wait a boolean.
     */
    public TeleportedCommandContext(final boolean wait, final String commandName, final String... commandArgs)
    {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        this.block = wait;
        timeoutMS = -1;
    }

    /**
     * <p>
     * Constructor for ExComContext.
     * </p>
     *
     * @param commandName a {@link java.lang.String} object.
     */
    public TeleportedCommandContext(final String commandName)
    {
        this.commandName = commandName;
        block = true;
        timeoutMS = -1;
    }

    /**
     * <p>
     * Constructor for ExComContext.
     * </p>
     *
     * @param commandName a {@link java.lang.String} object.
     * @param commandArgs a {@link java.lang.String} object.
     */
    public TeleportedCommandContext(final String commandName, final String... commandArgs)
    {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        block = true;
        timeoutMS = -1;
    }

    /**
     * {@inheritDoc}
     *
     * Short hand to get the results of the command. Do not change this for any
     * other reason. The end user probably depends on getting exactly what their
     * command returns.
     */
    @Override
    public String toString()
    {
        return result;
    }

    public long getTimeoutMS()
    {
        return timeoutMS;
    }

    public String[] getCommandArgs()
    {
        return commandArgs;
    }

    public String getCommandName()
    {
        return commandName;
    }
}

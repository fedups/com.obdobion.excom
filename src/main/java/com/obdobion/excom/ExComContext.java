package com.obdobion.excom;

/**
 * <p>ExComContext class.</p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class ExComContext
{
    protected ClientCommand clientCommand;
    String[]                commandArgs;
    String                  commandName;
    boolean                 logResult;
    String                  result;
    long                    timeoutMS;
    boolean                 wait;

    /**
     * <p>Constructor for ExComContext.</p>
     */
    public ExComContext()
    {
        this.wait = true;
        this.timeoutMS = -1;
    }

    /**
     * <p>Constructor for ExComContext.</p>
     *
     * @param wait a boolean.
     * @param commandName a {@link java.lang.String} object.
     * @param commandArgs a {@link java.lang.String} object.
     */
    public ExComContext(final boolean wait, final String commandName, final String... commandArgs)
    {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        this.wait = wait;
        this.timeoutMS = -1;
    }

    /**
     * <p>Constructor for ExComContext.</p>
     *
     * @param commandName a {@link java.lang.String} object.
     */
    public ExComContext(final String commandName)
    {
        this.commandName = commandName;
        this.wait = true;
        this.timeoutMS = -1;
    }

    /**
     * <p>Constructor for ExComContext.</p>
     *
     * @param commandName a {@link java.lang.String} object.
     * @param commandArgs a {@link java.lang.String} object.
     */
    public ExComContext(final String commandName, final String... commandArgs)
    {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        this.wait = true;
        this.timeoutMS = -1;
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
}

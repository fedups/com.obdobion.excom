package com.obdobion.excom;

public class ExComContext
{
    protected ClientCommand clientCommand;
    String[]                commandArgs;
    String                  commandName;
    boolean                 logResult;
    String                  result;
    long                    timeoutMS;
    boolean                 wait;

    public ExComContext()
    {
        this.wait = true;
        this.timeoutMS = -1;
    }

    public ExComContext(final boolean wait, final String commandName, final String... commandArgs)
    {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        this.wait = wait;
        this.timeoutMS = -1;
    }

    public ExComContext(final String commandName)
    {
        this.commandName = commandName;
        this.wait = true;
        this.timeoutMS = -1;
    }

    public ExComContext(final String commandName, final String... commandArgs)
    {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        this.wait = true;
        this.timeoutMS = -1;
    }

    /**
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

package com.obdobion.excom;

import com.obdobion.argument.ICmdLine;

public class ClientCommand
{

    ICmdLine         args;
    String           cmdName;
    IExternalRequest command;
    long             duration;
    long             timeoutMS;
    String           title;

    public ClientCommand(final String title, final String cmdName, final ICmdLine args, final IExternalRequest command)
    {
        this.title = title;
        this.cmdName = cmdName;
        this.args = args;
        this.command = command;
        this.timeoutMS = -1;
    }

    public ICmdLine getArgs()
    {
        return args;
    }

    public String getCmdName()
    {
        return cmdName;
    }

    public IExternalRequest getCommand()
    {
        return command;
    }

    public long getTimeoutMS()
    {
        return timeoutMS;
    }

    public String getTitle()
    {
        return title;
    }

    public void setArgs(final ICmdLine args)
    {
        this.args = args;
    }

    public void setCmdName(final String cmdName)
    {
        this.cmdName = cmdName;
    }

    public void setCommand(final IExternalRequest command)
    {
        this.command = command;
    }

    public void setTimeoutMS(final long timeoutMS)
    {
        this.timeoutMS = timeoutMS;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }
}
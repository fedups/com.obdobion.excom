package com.obdobion.excom.spring;

import com.obdobion.excom.IExternalRequest;

public class CommandWrapper
{
    private IExternalRequest command;
    private String           commandName;
    private String           title;

    public IExternalRequest getCommand()
    {
        return command;
    }

    public String getCommandName()
    {
        return commandName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setCommand(final IExternalRequest command)
    {
        this.command = command;
    }

    public void setCommandName(final String commandName)
    {
        this.commandName = commandName;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    @Override
    public String toString()
    {
        return "title(" + title + ") commandName(" + commandName + ")";
    }
}

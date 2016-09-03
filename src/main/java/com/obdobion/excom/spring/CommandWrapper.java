package com.obdobion.excom.spring;

import com.obdobion.excom.IExternalRequest;

/**
 * <p>
 * CommandWrapper class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class CommandWrapper
{
    private IExternalRequest command;
    private String           commandName;
    private String           title;

    /**
     * <p>
     * Getter for the field <code>command</code>.
     * </p>
     *
     * @return a {@link com.obdobion.excom.IExternalRequest} object.
     */
    public IExternalRequest getCommand()
    {
        return command;
    }

    /**
     * <p>
     * Getter for the field <code>commandName</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCommandName()
    {
        return commandName;
    }

    /**
     * <p>
     * Getter for the field <code>title</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * <p>
     * Setter for the field <code>command</code>.
     * </p>
     *
     * @param command a {@link com.obdobion.excom.IExternalRequest} object.
     */
    public void setCommand(final IExternalRequest command)
    {
        this.command = command;
    }

    /**
     * <p>
     * Setter for the field <code>commandName</code>.
     * </p>
     *
     * @param commandName a {@link java.lang.String} object.
     */
    public void setCommandName(final String commandName)
    {
        this.commandName = commandName;
    }

    /**
     * <p>
     * Setter for the field <code>title</code>.
     * </p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void setTitle(final String title)
    {
        this.title = title;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "title(" + title + ") commandName(" + commandName + ")";
    }
}

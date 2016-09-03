package com.obdobion.excom;

import com.obdobion.argument.ICmdLine;

/**
 * <p>
 * ClientCommand class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class ClientCommand
{

    ICmdLine         args;
    String           cmdName;
    IExternalRequest command;
    long             duration;
    long             timeoutMS;
    String           title;

    /**
     * <p>
     * Constructor for ClientCommand.
     * </p>
     *
     * @param title a {@link java.lang.String} object.
     * @param cmdName a {@link java.lang.String} object.
     * @param args a {@link com.obdobion.argument.ICmdLine} object.
     * @param command a {@link com.obdobion.excom.IExternalRequest} object.
     */
    public ClientCommand(final String title, final String cmdName, final ICmdLine args, final IExternalRequest command)
    {
        this.title = title;
        this.cmdName = cmdName;
        this.args = args;
        this.command = command;
        this.timeoutMS = -1;
    }

    /**
     * <p>
     * Getter for the field <code>args</code>.
     * </p>
     *
     * @return a {@link com.obdobion.argument.ICmdLine} object.
     */
    public ICmdLine getArgs()
    {
        return args;
    }

    /**
     * <p>
     * Getter for the field <code>cmdName</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCmdName()
    {
        return cmdName;
    }

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
     * Getter for the field <code>timeoutMS</code>.
     * </p>
     *
     * @return a long.
     */
    public long getTimeoutMS()
    {
        return timeoutMS;
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
     * Setter for the field <code>args</code>.
     * </p>
     *
     * @param args a {@link com.obdobion.argument.ICmdLine} object.
     */
    public void setArgs(final ICmdLine args)
    {
        this.args = args;
    }

    /**
     * <p>
     * Setter for the field <code>cmdName</code>.
     * </p>
     *
     * @param cmdName a {@link java.lang.String} object.
     */
    public void setCmdName(final String cmdName)
    {
        this.cmdName = cmdName;
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
     * Setter for the field <code>timeoutMS</code>.
     * </p>
     *
     * @param timeoutMS a long.
     */
    public void setTimeoutMS(final long timeoutMS)
    {
        this.timeoutMS = timeoutMS;
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
}

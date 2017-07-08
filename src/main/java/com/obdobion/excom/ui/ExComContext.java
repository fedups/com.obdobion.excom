package com.obdobion.excom.ui;

import java.io.PrintWriter;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.ICmdLine;

/**
 * <p>
 * Context1 class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
final public class ExComContext
{
    public static String convertToString(final String[] args)
    {
        final StringBuffer str = new StringBuffer();
        if (args != null)
            for (int c = 0; c < args.length; c++)
            {
                str.append(args[c]);
                str.append(" ");
            }
        return str.toString();
    }

    private PluginManager pluginManager;
    private PrintWriter   consoleErrorOutput;
    private long          startNanoTime;
    private long          endNanoTime;
    private boolean       recordingHistory;
    private Outline       outline;
    private boolean       subcontext;
    private String        originalUserInput;
    private int           bytesReceived;
    private ICmdLine      parser;

    public int getBytesReceived()
    {
        return bytesReceived;
    }

    /**
     * <p>
     * Getter for the field <code>consoleErrorOutput</code>.
     * </p>
     *
     * @return a {@link java.io.PrintWriter} object.
     */
    public PrintWriter getConsoleErrorOutput()
    {
        return consoleErrorOutput;
    }

    /**
     * <p>
     * getEndTime.
     * </p>
     *
     * @return a long.
     */
    public long getEndTime()
    {
        return endNanoTime;
    }

    public String getOriginalUserInput()
    {
        return originalUserInput;
    }

    /**
     * <p>
     * Getter for the field <code>outline</code>.
     * </p>
     *
     * @return the outline
     */
    public Outline getOutline()
    {
        return outline;
    }

    /**
     * <p>
     * Getter for the field <code>pluginManager</code>.
     * </p>
     *
     * @return a {@link com.obdobion.excom.ui.PluginManager} object.
     */
    public PluginManager getPluginManager()
    {
        return pluginManager;
    }

    public ICmdLine getParser()
    {
        return parser;
    }

    /**
     * <p>
     * getStartTime.
     * </p>
     *
     * @return a long.
     */
    public long getStartTime()
    {
        return startNanoTime;
    }

    /**
     * <p>
     * isRecordingHistory.
     * </p>
     *
     * @return a boolean.
     */
    public boolean isRecordingHistory()
    {
        return recordingHistory;
    }

    /**
     * <p>
     * isSubcontext.
     * </p>
     *
     * @return a boolean.
     */
    public boolean isSubcontext()
    {
        return subcontext;
    }

    public void setBytesReceived(final int bytesReceived)
    {
        this.bytesReceived = bytesReceived;
    }

    /**
     * <p>
     * Setter for the field <code>consoleErrorOutput</code>.
     * </p>
     *
     * @param pw a {@link java.io.PrintWriter} object.
     */
    public void setConsoleErrorOutput(final PrintWriter pw)
    {
        consoleErrorOutput = pw;
    }

    /**
     * <p>
     * setEndTime.
     * </p>
     *
     * @param nanoTime a long.
     */
    public void setEndTime(final long nanoTime)
    {
        endNanoTime = nanoTime;
    }

    public void setOriginalUserInput(final String originalUserInput)
    {
        this.originalUserInput = originalUserInput;
    }

    /**
     * @param outline the outline to set
     */
    void setOutline(final Outline outline)
    {
        this.outline = outline;
    }

    /**
     * <p>
     * Setter for the field <code>pluginManager</code>.
     * </p>
     *
     * @param pluginManager a {@link com.obdobion.excom.ui.PluginManager}
     *            object.
     */
    public void setPluginManager(final PluginManager pluginManager)
    {
        this.pluginManager = pluginManager;
    }

    /**
     * <p>
     * Setter for the field <code>recordingHistory</code>.
     * </p>
     *
     * @param recordingHistory a boolean.
     */
    public void setRecordingHistory(final boolean recordingHistory)
    {
        this.recordingHistory = recordingHistory;
    }

    /**
     * <p>
     * setStartTime.
     * </p>
     *
     * @param nanoTime a long.
     */
    public void setStartTime(final long nanoTime)
    {
        startNanoTime = nanoTime;
    }

    void setSubcontext(final boolean subcontext)
    {
        this.subcontext = subcontext;
    }

    public void setParser(final CmdLine cmdLine)
    {
        parser = cmdLine;
    }
}

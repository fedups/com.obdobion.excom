package com.obdobion.excom.ui;

import java.io.PrintWriter;

import com.obdobion.argument.ICmdLine;

/**
 * <p>
 * Context1 class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
final public class ExcomContext
{
    public static String convertToString(final String[] args)
    {
        final StringBuffer str = new StringBuffer();
        for (int c = 0; c < args.length; c++)
        {
            str.append(args[c]);
            str.append(" ");
        }
        return str.toString();
    }

    private PluginManager pluginManager;
    private PrintWriter   consoleErrorOutput;
    private ICmdLine      myParser;
    private long          startNanoTime;
    private long          endNanoTime;
    private boolean       recordingHistory;
    private Outline       outline;
    private boolean       subcontext;
    private String        originalUserInput;

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
     * getParser.
     * </p>
     *
     * @return a {@link com.obdobion.argument.ICmdLine} object.
     */
    public ICmdLine getParser()
    {
        return myParser;
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
     * setParser.
     * </p>
     *
     * @param myParser a {@link com.obdobion.argument.ICmdLine} object.
     */
    public void setParser(final ICmdLine myParser)
    {
        this.myParser = myParser;
    }

    /**
     * <p>
     * Setter for the field <code>pluginManager</code>.
     * </p>
     *
     * @param pluginManager a {@link com.obdobion.excom.ui.PluginManager} object.
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
}

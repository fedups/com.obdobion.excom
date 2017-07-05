package com.obdobion.excom.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * HistoryManager class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class HistoryManager
{
    public static class HistoryRecord
    {
        private final String contents;

        public HistoryRecord(final String contents)
        {
            this.contents = contents;
        }

        public String getContents()
        {
            return contents;
        }

        @Override
        public String toString()
        {
            return contents;
        }

    }

    private final static Logger   logger = LoggerFactory.getLogger(HistoryManager.class.getName());

    static private HistoryManager instance;

    /**
     * <p>
     * Getter for the field <code>instance</code>.
     * </p>
     *
     * @return a {@link com.obdobion.excom.ui.HistoryManager} object.
     */
    static public HistoryManager getInstance()
    {
        return instance;
    }

    private final Config config;

    /**
     * <p>
     * Constructor for HistoryManager.
     * </p>
     *
     * @param config a {@link com.obdobion.excom.ui.Config} object.
     */
    public HistoryManager(final Config config)
    {
        HistoryManager.instance = this;
        this.config = config;
    }

    /**
     * <p>
     * getHistory.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<HistoryRecord> getHistory()
    {
        return loadHistory();
    }

    List<HistoryRecord> loadHistory()
    {
        final List<HistoryRecord> history = new ArrayList<>();
        logger.debug("loading history from {}", config.getHistoryFile().getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(config.getHistoryFile())))
        {
            String aLine = null;
            while ((aLine = br.readLine()) != null)
                history.add(new HistoryRecord(aLine));
        } catch (final IOException e)
        {
            logger.warn(e.getMessage());
        }
        return history;
    }

    /**
     * <p>
     * record.
     * </p>
     *
     * @param context a {@link com.obdobion.excom.ui.ExcomContext} object.
     */
    public void record(final ExcomContext context)
    {
        if (context == null)
            return;
        if (!context.isRecordingHistory())
            return;
        /*
         * Append to the end of the file
         */
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(config.getHistoryFile(), true)))
        {
            final StringBuilder historyContents = new StringBuilder();
            historyContents.append(context.getParser().getName());
            historyContents.append(" ");
            historyContents.append(context.getOriginalUserInput());
            bw.write(historyContents.toString());
            bw.newLine();

        } catch (final IOException e)
        {
            logger.warn(e.getMessage());
        }
    }
}

package com.obdobion.excom.ui.writer;

import java.io.Console;
import java.io.IOException;

import com.obdobion.excom.ui.ExComConfig;

/**
 * ConsoleWriter.
 *
 * Writes a simple text based output to the console or at least what the context
 * refers to as the console output.
 *
 * @author Chris DeGreef fedupforone@gmail.com
 *
 */
public class ConsoleWriter implements IOutlineWriter
{
    ExComConfig config;
    Console     console;
    private int newLineCount;
    private int currentLineLength;
    private int currentIndentLevelForWrapping;

    ConsoleWriter(final ExComConfig config)
    {
        console = System.console();
        this.config = config;
        setCurrentIndentLevelForWrapping(0);
    }

    /** {@inheritDoc} */
    @Override
    public void append(final String value, final int wrappingIndentSize)
    {
        final String[] lines = value.split("\\n", -1);
        String remainderOfLongLine = null;
        String aLine = null;

        for (int line = 0; remainderOfLongLine != null || line < lines.length; line++)
        {
            if (remainderOfLongLine != null)
            {
                aLine = remainderOfLongLine;
                /*
                 * don't use up the next line until remainder is finished.
                 */
                line--;
                /*
                 * But indent continuations of a line a little.
                 */
                for (int w = 0; w < wrappingIndentSize; w++)
                {
                    indent();
                    printf(" ");
                }
                setCurrentLineLength(getCurrentLineLength() + wrappingIndentSize);
            } else
            {
                aLine = lines[line];
                if (line != 0)
                {
                    newline();
                    indent();
                    setCurrentLineLength(0);
                }
            }

            if (getCurrentLineLength() == (getIndentSize() * getCurrentIndentLevelForWrapping()))
                indent();
            if (getCurrentLineLength() + aLine.length() >= config.getOutlineWidth())
            {
                final String[] parts = split(aLine,
                        config.getOutlineWidth() - getCurrentLineLength());
                /*
                 * It is possible that it is one big word that won't fit. Just
                 * go ahead and overrun the end of the line. That is when the
                 * first part of the split ends up being just whitespace.
                 */
                if (parts[0].length() > 0)
                {
                    printf(parts[0]);
                    newline();
                    remainderOfLongLine = parts[1];
                    continue;
                }
            }
            remainderOfLongLine = null;
            printf(aLine);
            setCurrentLineLength(getCurrentLineLength() + aLine.length());
        }
        // newline();
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException
    {}

    /** {@inheritDoc} */
    @Override
    public void decreaseLevel()
    {
        setCurrentIndentLevelForWrapping(getCurrentIndentLevelForWrapping() - 1);
        newline();
    }

    /**
     * @return the currentIndentLevelForWrapping
     */
    int getCurrentIndentLevelForWrapping()
    {
        return currentIndentLevelForWrapping;
    }

    /**
     * @return the currentLineLength
     */
    int getCurrentLineLength()
    {
        return currentLineLength;
    }

    int getIndentSize()
    {
        return config.getOutlineIndentSize();
    }

    /**
     * @return the newLineCount
     */
    int getNewLineCount()
    {
        return newLineCount;
    }

    /** {@inheritDoc} */
    @Override
    public void increaseLevel()
    {
        setCurrentIndentLevelForWrapping(getCurrentIndentLevelForWrapping() + 1);
        newline();
    }

    void indent()
    {
        for (int i = 0; i < getCurrentIndentLevelForWrapping(); i++)
            for (int s = 0; s < getIndentSize(); s++)
                printf(" ");
    }

    void newline()
    {
        setCurrentLineLength(getIndentSize() * getCurrentIndentLevelForWrapping());
        setNewLineCount(getNewLineCount() + 1);
        if (getNewLineCount() > 2)
            /*
             * suppress more than one blank line
             */
            return;
        console.printf("\n");
    }

    /**
     * Don't use new lines in this.
     */
    void printf(final String contents)
    {
        setNewLineCount(0);
        console.printf(contents);
        console.flush();
    }

    /**
     * @param currentIndentLevelForWrapping the currentIndentLevelForWrapping to
     *            set
     */
    void setCurrentIndentLevelForWrapping(final int currentIndentLevelForWrapping)
    {
        this.currentIndentLevelForWrapping = currentIndentLevelForWrapping;
    }

    /**
     * @param currentLineLength the currentLineLength to set
     */
    void setCurrentLineLength(final int currentLineLength)
    {
        this.currentLineLength = currentLineLength;
    }

    /**
     * @param newLineCount the newLineCount to set
     */
    void setNewLineCount(final int newLineCount)
    {
        this.newLineCount = newLineCount;
    }

    String[] split(final String line, final int availableBytesLeft)
    {
        int splitPoint = 0;
        for (int b = 0; b < availableBytesLeft; b++)
            /*
             * look for the last space within the printable part of the line.
             */
            if (line.charAt(b) == ' ')
                splitPoint = b;
        if (splitPoint >= line.length())
            return new String[] { line };
        return new String[] {
                line.substring(0, splitPoint),
                line.substring(splitPoint + 1)
        };
    }
}

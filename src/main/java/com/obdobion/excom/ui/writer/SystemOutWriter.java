package com.obdobion.excom.ui.writer;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obdobion.excom.ui.Config;

/**
 * ConsoleWriter.
 *
 * Writes a simple text based output to the console. This class is necessary for
 * supporting jUnits. The java Console class returns null when they run. The
 * selection of this class is controlled through the Config file. And in the
 * case of jUnit testing that file is in the src/test/resources folder.
 *
 * @author Chris DeGreef fedupforone@gmail.com
 *
 */
public class SystemOutWriter extends ConsoleWriter
{
    private final static Logger logger = LoggerFactory.getLogger(SystemOutWriter.class.getName());

    @SuppressWarnings("hiding")
    PrintWriter                 console;

    public SystemOutWriter(final Config config)
    {
        super(config);
        console = new PrintWriter(System.out);
        logger.debug("overriding output to System.out");
    }

    @Override
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
    @Override
    void printf(final String contents)
    {
        setNewLineCount(0);
        console.printf(contents);
        console.flush();
    }
}

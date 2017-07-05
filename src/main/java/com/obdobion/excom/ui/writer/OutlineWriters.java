package com.obdobion.excom.ui.writer;

import com.obdobion.excom.ui.Config;

/**
 * <p>
 * OutlineWriters class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public enum OutlineWriters
{
    /**
     * ConsoleWriter.
     *
     * Writes a simple text based output to the console.
     *
     * @author Chris DeGreef fedupforone@gmail.com
     *
     */
    Console,

    /**
     * SystemOutWriter.
     *
     * Writes a simple text based output to System.out
     *
     * @author Chris DeGreef fedupforone@gmail.com
     *
     */
    System,

    /**
     * StringWriter.
     *
     * Saves the output to a String rather than writing it to an output.
     *
     * @author Chris DeGreef fedupforone@gmail.com
     *
     */
    String;

    /**
     * <p>
     * create.
     * </p>
     *
     * @param config a {@link Config} object.
     *
     * @return a {@link com.obdobion.excom.ui.writer.IOutlineWriter} object.
     */
    static final public IOutlineWriter create(final Config config)
    {
        switch (config.getWriterType())
        {
            case Console:
                return new ConsoleWriter(config);
            case System:
                return new SystemOutWriter(config);
            case String:
                return new StringWriter(config);
            default:
                break;
        }
        return new ConsoleWriter(config);
    }
}

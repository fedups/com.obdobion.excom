package com.obdobion.excom.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import com.obdobion.excom.ui.writer.IOutlineWriter;
import com.obdobion.excom.ui.writer.OutlineWriters;

/**
 * <p>
 * Outline class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Outline
{
    ExComConfig         config;
    IOutlineWriter writer;
    String         contents;

    List<Outline>  children;

    Outline(final ExComConfig config)
    {
        super();
        this.config = config;
        writer = OutlineWriters.create(config);
    }

    Outline(final Outline parentOutline)
    {
        super();
        config = parentOutline.config;
        writer = parentOutline.writer;
    }

    /**
     * <p>
     * add.
     * </p>
     *
     * @param childContents a {@link java.lang.String} object.
     * @param childArguments a {@link java.lang.Object} object.
     * @return a {@link com.obdobion.excom.ui.Outline} object.
     */
    public Outline add(final String childContents, final Object... childArguments)
    {
        if (children == null)
            children = new ArrayList<>();
        final Outline child = new Outline(this);
        child.setContents(childContents, childArguments);
        children.add(child);
        return child;
    }

    /**
     * <p>
     * getCurrent.
     * </p>
     *
     * @return a {@link com.obdobion.excom.ui.Outline} object.
     */
    public Outline getCurrent()
    {
        if (children == null || children.size() == 0)
            return this;
        return children.get(children.size() - 1).getCurrent();
    }

    /**
     * <p>
     * Getter for the field <code>writer</code>.
     * </p>
     *
     * @return a {@link IOutlineWriter} object.
     */
    public IOutlineWriter getWriter()
    {
        return writer;
    }

    /**
     * <p>
     * print.
     * </p>
     *
     * @param context a {@link com.obdobion.excom.ui.ExComContext} object.
     */
    public void print(final ExComContext context)
    {
        if (context.isSubcontext())
            return;

        // try (IOutlineWriter ow = OutlineWriters.create(config))
        // {
        print(getWriter());
        //
        // } catch (final IOException e)
        // {
        // logger.error("attempting to print outline", e);
        // }
    }

    void print(final IOutlineWriter ow)
    {
        if (contents != null)
            ow.append(contents.trim(), 0);
        if (children != null)
        {
            ow.increaseLevel();
            for (final Outline child : children)
                child.print(ow);
            ow.decreaseLevel();
        }
    }

    /**
     * <p>
     * printf.
     * </p>
     *
     * @param wrappingIndentSize a int.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public void printf(final int wrappingIndentSize, final String format, final Object... args)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        pw.printf(format, args);
        writer.append(sw.toString(), wrappingIndentSize);
    }

    /**
     * <p>
     * printf.
     * </p>
     *
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public void printf(final String format, final Object... args)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        pw.printf(format, args);
        writer.append(sw.toString(), 0);
    }

    /**
     * <p>
     * reset.
     * </p>
     */
    public void reset()
    {
        children = null;
        contents = null;
    }

    /**
     * <p>
     * Setter for the field <code>contents</code>.
     * </p>
     *
     * @param contents a {@link java.lang.String} object.
     * @param arguments a {@link java.lang.Object} object.
     */
    public void setContents(final String contents, final Object... arguments)
    {
        final StringBuilder sb = new StringBuilder();
        try (Formatter fmtr = new Formatter(sb))
        {
            fmtr.format(contents, arguments);
            this.contents = sb.toString();
        }
    }
}

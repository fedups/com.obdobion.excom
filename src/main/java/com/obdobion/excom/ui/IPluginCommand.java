package com.obdobion.excom.ui;

import java.text.ParseException;

/**
 * <p>
 * IPluginCommand interface.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public interface IPluginCommand
{
    /**
     *
     * @param context is the context
     * @return an int
     * @throws ParseException when a parsing error is detected
     */
    int execute(ExComContext context) throws ParseException;

    /**
     * <p>
     * getGroup.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getGroup();

    /**
     * <p>
     * getName.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>
     * getOverview.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getOverview();

    /**
     * <p>
     * isOnceAndDone.
     * </p>
     *
     * @return a boolean.
     */
    boolean isOnceAndDone();
}

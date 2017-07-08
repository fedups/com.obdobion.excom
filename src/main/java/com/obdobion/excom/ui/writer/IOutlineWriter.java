package com.obdobion.excom.ui.writer;

import java.io.Closeable;

/**
 * <p>
 * IOutlineWriter interface.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public interface IOutlineWriter extends Closeable
{

    /**
     * <p>
     * append.
     * </p>
     *
     * @param trim a {@link java.lang.String} object.
     * @param wrappingIndentSize a int.
     */
    void append(String trim, int wrappingIndentSize);

    /**
     * <p>
     * decreaseLevel.
     * </p>
     */
    void decreaseLevel();

    /**
     * <p>
     * increaseLevel.
     * </p>
     */
    void increaseLevel();

}

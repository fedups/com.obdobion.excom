package com.obdobion.excom.ui;

/**
 * <p>
 * PluginNotFoundException class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class PluginNotFoundException extends Exception
{
    /**
     * <p>
     * Constructor for PluginNotFoundException.
     * </p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public PluginNotFoundException(final String message)
    {
        super(message);
    }

    /**
     * <p>
     * Constructor for PluginNotFoundException.
     * </p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause a {@link java.lang.Throwable} object.
     */
    public PluginNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * <p>
     * Constructor for PluginNotFoundException.
     * </p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause a {@link java.lang.Throwable} object.
     * @param enableSuppression a boolean.
     * @param writableStackTrace a boolean.
     */
    public PluginNotFoundException(final String message,
            final Throwable cause,
            final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * <p>
     * Constructor for PluginNotFoundException.
     * </p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public PluginNotFoundException(final Throwable cause)
    {
        super(cause);
    }
}

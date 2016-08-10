package com.obdobion.excom;

import org.apache.log4j.NDC;

/**
 * <p>NDCValue class.</p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class NDCValue
{

    /**
     * <p>pop.</p>
     */
    static public void pop()
    {
        NDC.pop();
    }

    /**
     * <p>push.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a int.
     */
    static public void push(final String key, final int value)
    {
        NDC.push(key + "(" + value + ")");
    }

    /**
     * <p>push.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     */
    static public void push(final String key, final String value)
    {
        NDC.push(key + "(" + value + ")");
    }
}

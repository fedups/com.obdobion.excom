package com.obdobion.excom;

import org.apache.log4j.NDC;

public class NDCValue
{

    static public void pop()
    {
        NDC.pop();
    }

    static public void push(final String key, final int value)
    {
        NDC.push(key + "(" + value + ")");
    }

    static public void push(final String key, final String value)
    {
        NDC.push(key + "(" + value + ")");
    }
}

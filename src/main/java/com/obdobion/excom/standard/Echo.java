package com.obdobion.excom.standard;

import org.apache.log4j.Logger;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ClientCommand;
import com.obdobion.excom.IExternalRequest;

/**
 * <p>
 * Echo class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class Echo implements IExternalRequest
{
    static final private Logger logger = Logger.getLogger(Echo.class.getName());

    @Arg(shortName = 'm',
            positional = true,
            required = true,
            help = "A message that will be logged in the server's output.")
    private String              message;

    /** {@inheritDoc} */
    public String execute(final ClientCommand cc) throws Exception
    {
        logger.info(message.trim());
        return message;
    }
}

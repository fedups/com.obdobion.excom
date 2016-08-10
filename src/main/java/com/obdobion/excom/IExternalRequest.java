package com.obdobion.excom;

/**
 * <p>IExternalRequest interface.</p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public interface IExternalRequest
{
    /**
     * <p>execute.</p>
     *
     * @param cc a {@link com.obdobion.excom.ClientCommand} object.
     * @return a {@link java.lang.String} object.
     * @throws java.lang.Exception if any.
     */
    public String execute(ClientCommand cc) throws Exception;
}

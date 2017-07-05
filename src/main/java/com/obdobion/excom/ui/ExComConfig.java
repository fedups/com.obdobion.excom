package com.obdobion.excom.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

import com.obdobion.argument.CmdLine;
import com.obdobion.argument.ICmdLine;
import com.obdobion.argument.annotation.Arg;
import com.obdobion.argument.type.WildFiles;
import com.obdobion.excom.ui.writer.OutlineWriters;

/**
 * <p>
 * Config class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
final public class ExComConfig
{
    @Arg(caseSensitive = true, defaultValues = "NothingSpecified")
    private WildFiles           plugins;

    @Arg(caseSensitive = true, longName = "log4j", required = true)
    private String              log4jConfigFileName;

    @Arg(caseSensitive = true, required = true)
    private String              version;

    @Arg(caseSensitive = true, required = true)
    private File                history;

    @Arg(defaultValues = "Console")
    private OutlineWriters      writerType;

    @Arg(defaultValues = "80", range = "10")
    private int                 outlineWidth;

    @Arg(defaultValues = "2", range = "0")
    private int                 outlineIndentSize;

    @Arg(defaultValues = "localhost",
            shortName = 'h',
            help = "The DNS name or the IP address of the remote server.")
    private String              receiverHost;

    @Arg(defaultValues = "2526",
            shortName = 'p',
            range = { "1025", "65535" },
            help = "The port on the remote host that is listening for these commands.")
    private int            sendReceivePort;

    @Arg(shortName = 'n',
            caseSensitive = true,
            help = "A name used by this command to decorate the prompt.  If unspecified, this will be the same as --host.")
    private String              remoteName;

    @Arg(help = "Only allow the command to run up to this limit of milliseconds",
            defaultValues = "-1")
    private long                timeoutMS;

    @Arg
    private boolean             logResult;

    @Arg
    private boolean             asynchronous;

    private final String        configFileLocation;

    private final ICmdLine      properties;

    private ClassLoader         pluginClassLoader;

    private final Pattern       badJarPattern = Pattern.compile(
            "junit|commons-codec|slf4j|log4j|algebrain|argument|calendar|howto-[.0-9]+jar");

    /**
     * <p>
     * Constructor for Config.
     * </p>
     *
     * @param appDir a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     * @throws java.text.ParseException if any.
     */
    public ExComConfig(final String appDir) throws IOException, ParseException
    {
        configFileLocation = System.getProperty("excom.config",
                appDir + "/src/test/resources/excom.cfg");
        /*
         * Saving an instance of the CmdLine so that it can be exported later if
         * necessary.
         */
        properties = CmdLine.loadProperties(this, new File(configFileLocation));
        if (remoteName == null)
            remoteName = receiverHost;

        LogManager.resetConfiguration();
        DOMConfigurator.configure(getLog4jConfigFileName());

        final URL[] pluginJars = getPluginJars();
        if (pluginJars.length > 0)
            setPluginClassLoader(new URLClassLoader(pluginJars, this.getClass().getClassLoader()));
    }

    /**
     * <p>
     * getHistoryFile.
     * </p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getHistoryFile()
    {
        return history;
    }

    /**
     * <p>
     * Getter for the field <code>log4jConfigFileName</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLog4jConfigFileName()
    {
        return log4jConfigFileName;
    }

    /**
     * <p>
     * Getter for the field <code>outlineIndentSize</code>.
     * </p>
     *
     * @return a int.
     */
    public int getOutlineIndentSize()
    {
        return outlineIndentSize;
    }
    /**
     * <p>
     * Getter for the field <code>outlineWidth</code>.
     * </p>
     *
     * @return a int.
     */
    public int getOutlineWidth()
    {
        return outlineWidth;
    }
    /**
     * <p>
     * Getter for the field <code>pluginClassLoader</code>.
     * </p>
     *
     * @return a {@link java.lang.ClassLoader} object.
     */
    public ClassLoader getPluginClassLoader()
    {
        return pluginClassLoader;
    }

    private URL[] getPluginJars() throws ParseException, IOException
    {
        final List<File> jarList = plugins.files();
        final List<URL> loadableJars = new ArrayList<>();
        for (final File jar : jarList)
            if (isLoadableJar(jar))
            {
                loadableJars.add(jar.toURI().toURL());
            }

        final URL[] urlArray = new URL[loadableJars.size()];
        int j = 0;
        for (final URL jar : loadableJars)
            urlArray[j++] = jar;
        return urlArray;
    }

    public String getReceiverHost()
    {
        return receiverHost;
    }

    public String getRemoteName()
    {
        return remoteName;
    }

    public int getSendReceivePort()
    {
        return sendReceivePort;
    }

    public long getTimeoutMS()
    {
        return timeoutMS;
    }

    /**
     * <p>
     * Getter for the field <code>writerType</code>.
     * </p>
     *
     * @return a {@link OutlineWriters} object.
     */
    public OutlineWriters getWriterType()
    {
        return writerType;
    }

    public boolean isAsynchronous()
    {
        return asynchronous;
    }

    /**
     * Do not load jars that are part of this howto app. They would create odd
     * situations because they would overload the jars that this app expects.
     *
     * @param jar
     * @return
     */
    private boolean isLoadableJar(final File jar)
    {
        final boolean bad = badJarPattern.matcher(jar.getName()).find();
        return !bad;
    }

    public boolean isLogResult()
    {
        return logResult;
    }

    /**
     * <p>
     * saveToDisk.
     * </p>
     *
     * @throws java.text.ParseException if any.
     * @throws java.io.IOException if any.
     */
    public void saveToDisk() throws ParseException, IOException
    {
        properties.pull(this);
        properties.exportNamespace(new File(configFileLocation));
    }

    private void setPluginClassLoader(final ClassLoader urlClassLoader)
    {
        pluginClassLoader = urlClassLoader;
    }
}

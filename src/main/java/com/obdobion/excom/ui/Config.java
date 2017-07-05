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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
final public class Config
{
    private final static Logger logger        = LoggerFactory.getLogger(Config.class.getName());

    @Arg(caseSensitive = true, required = true)
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
    public Config(final String appDir) throws IOException, ParseException
    {
        configFileLocation = System.getProperty("howto.config",
                appDir + "/src/test/resources/howto.cfg");
        /*
         * Saving an instance of the CmdLine so that it can be exported later if
         * necessary.
         */
        properties = CmdLine.loadProperties(this, new File(configFileLocation));

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
                logger.debug("found plugin jar: {}", jar.getAbsolutePath());
            }

        final URL[] urlArray = new URL[loadableJars.size()];
        int j = 0;
        for (final URL jar : loadableJars)
            urlArray[j++] = jar;
        return urlArray;
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

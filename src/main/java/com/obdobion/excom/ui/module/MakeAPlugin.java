package com.obdobion.excom.ui.module;

import com.obdobion.argument.annotation.Arg;
import com.obdobion.excom.ui.ExcomContext;
import com.obdobion.excom.ui.IPluginCommand;
import com.obdobion.excom.ui.Outline;

/**
 * <p>
 * MakeAPlugin class.
 * </p>
 *
 * @author Chris DeGreef fedupforone@gmail.com
 */
public class MakeAPlugin implements IPluginCommand
{
    /** Constant <code>GROUP="Menu.GROUP"</code> */
    static public final String GROUP = Menu.GROUP;
    /** Constant <code>NAME="makeAPlugin"</code> */
    static public final String NAME  = "makeAPlugin";

    @Arg(matches = "[a-z][a-zA-Z0-9]*",
            required = true,
            caseSensitive = true,
            allowCamelCaps = true,
            help = "Indicates the name of the command that you want to develop.  This needs to be unique within a specific group.")
    String                     commandName;

    @Arg(matches = "[A-Z][a-zA-Z0-9]+",
            required = true,
            caseSensitive = true,
            allowCamelCaps = true,
            help = "Indicates the assigned group name for all of this commands in this plugin.")
    String                     groupName;

    @Arg(required = true,
            allowCamelCaps = true,
            help = "Indicates the reverse domain name; if your domain was example.com then this parameter value would need to be com.example (the reverse.)")
    String                     domainName;

    @Arg(required = true,
            caseSensitive = true,
            allowCamelCaps = true,
            help = "Indicates the disk folder where your git repositories reside.")
    String                     gitRepoParentDirectory;

    @Arg(allowCamelCaps = true, defaultValues = "4.8.1")
    String                     junitVersion;

    @Arg(allowCamelCaps = true, defaultValues = "0.0.3")
    String                     howtoVersion;

    /**
     * <p>
     * Constructor for MakeAPlugin.
     * </p>
     */
    public MakeAPlugin()
    {}

    /** {@inheritDoc} */
    @Override
    public int execute(final ExcomContext context)
    {
        final Outline ol = context.getOutline();

        showCreateGitRepo(ol.add("Initialize a GIT repo for this project"));
        showCreateMavenProject(ol.add("Initialize a Maven project"));
        showImportIntoEclipse(ol.add("Set up the project in Eclipse"));
        showAddServicesFile(ol.add("Add a services file"));
        showMinimalClassSetup(ol.add("Update the %s class to be a plugin", "App"));

        ol.print(context);
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getGroup()
    {
        return GROUP;

    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return NAME;
    }

    /** {@inheritDoc} */
    @Override
    public String getOverview()
    {
        return "Want to make a plugin? - this is how to do it.";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOnceAndDone()
    {
        return false;
    }

    private void showAddServicesFile(final Outline ol)
    {
        ol.add("Create source folder: %s", "src/main/resources");
        ol.add("Add folder (%s) to %s", "META-INF/services", "src/main/resources");
        final Outline file = ol.add("Add file (%s) to %s", IPluginCommand.class.getName(), "META-INF/services");
        file.add("Insert this text on the first line: %s.howto.%s.%s", domainName, groupName.toLowerCase(), "App");
    }

    private void showCreateGitRepo(final Outline ol)
    {
        final Outline gb = ol.add("start GitBash");
        gb.add("Need a download? %s", "https://git-scm.com/download/win");
        ol.add("cd %s", gitRepoParentDirectory);
        ol.add("git init %s/howto.%s", domainName, groupName.toLowerCase());
    }

    private void showCreateMavenProject(final Outline ol)
    {
        ol.add("cd %s", domainName);

        final Outline gb = ol.add("Run this Maven command");
        gb.add(
                "mvn archetype:generate -DgroupId=%s -DartifactId=howto.%s -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false",
                domainName, groupName.toLowerCase());
        gb.add("Need a download? %s", "https://git-scm.com/download/win");

    }

    private void showImportIntoEclipse(final Outline ol)
    {
        final Outline importOL = ol.add("Choose menu option File/Import");
        importOL.add("Type \"Exisiting Maven Projects\" into filter text");
        importOL.add("Root directory is %s/%s/howto.%s", gitRepoParentDirectory, domainName, groupName.toLowerCase());
        importOL.add("Click OK");

        final Outline pom = ol.add("Update the pom.xml file");

        pom.add("<plugin>\n"
                + "    <groupId>org.apache.maven.plugins</groupId>\n"
                + "    <artifactId>maven-compiler-plugin</artifactId>\n"
                + "    <version>3.0</version>\n"
                + "    <configuration>\n"
                + "        <source>1.8</source>\n"
                + "        <target>1.8</target>\n"
                + "    </configuration>\n"
                + "</plugin>");
        pom.add("<plugin>\n"
                + "    <artifactId>maven-clean-plugin</artifactId>\n"
                + "    <version>3.0.0</version>\n"
                + "</plugin>");
        pom.add("<plugin>\n"
                + "    <groupId>org.apache.maven.plugins</groupId>\n"
                + "    <artifactId>maven-jar-plugin</artifactId>\n"
                + "    <version>2.6</version>\n"
                + "    <configuration>\n"
                + "        <archive>\n"
                + "            <manifest>\n"
                + "                <addClasspath>true</addClasspath>\n"
                + "                <addDefaultImplementationEntries>true</addDefaultImplementationEntries>\n"
                + "                <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>\n"
                + "            </manifest>\n"
                + "        </archive>\n"
                + "    </configuration>\n"
                + "</plugin>");
        pom.add("<plugin>\n"
                + "    <groupId>org.apache.maven.plugins</groupId>\n"
                + "    <artifactId>maven-dependency-plugin</artifactId>\n"
                + "    <version>2.10</version>\n"
                + "    <executions>\n"
                + "        <execution>\n"
                + "            <id>copy-dependencies</id>\n"
                + "            <phase>package</phase>\n"
                + "            <goals>\n"
                + "                <goal>copy-dependencies</goal>\n"
                + "            </goals>\n"
                + "            <configuration>\n"
                + "                <outputDirectory>${project.build.directory}/mavenDependenciesForNSIS</outputDirectory>\n"
                + "                <overWriteReleases>false</overWriteReleases>\n"
                + "                <overWriteSnapshots>true</overWriteSnapshots>\n"
                + "                <excludeTransitive>false</excludeTransitive>\n"
                + "                <excludeScope>provided</excludeScope>\n"
                + "            </configuration>\n"
                + "        </execution>\n"
                + "    </executions>\n"
                + "</plugin>");
        pom.add("<dependency>\n"
                + "    <groupId>junit</groupId>\n"
                + "    <artifactId>junit</artifactId>\n"
                + "    <version>%s</version>\n"
                + "    <scope>provided</scope>\n"
                + "</dependency>",
                junitVersion);
        pom.add("<dependency>\n"
                + "    <groupId>com.obdobion</groupId>\n"
                + "    <artifactId>howto</artifactId>\n"
                + "    <version>%s</version>\n"
                + "    <scope>provided</scope>\n"
                + "</dependency>",
                howtoVersion);
    }

    private void showMinimalClassSetup(final Outline ol)
    {
        ol.add("Make the class implement %s", IPluginCommand.class.getName());
    }

}

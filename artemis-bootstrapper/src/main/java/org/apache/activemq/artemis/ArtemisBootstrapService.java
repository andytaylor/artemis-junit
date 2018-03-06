package org.apache.activemq.artemis;

import org.apache.activemq.artemis.cli.process.ProcessBuilder;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.deployers.impl.FileConfigurationParser;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ArtemisBootstrapService {
    private String artemisHome;
    private Configuration configuration;
    private Process broker;

    public ArtemisBootstrapService() {
        artemisHome = System.getProperty("ARTEMIS_HOME");
        System.out.println("ArtemisBootstrapService.ArtemisBootstrapService " + artemisHome);
    }

    public void start(Boolean clean)  {
        FileConfigurationParser parser = new FileConfigurationParser();
        try {
            configuration = parser.parseMainConfig(new FileInputStream(new File(artemisHome + "/etc/broker.xml")));
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
        File absoluteHome = new File(artemisHome);
        try {
            if (clean) {
                Path dataDir = Paths.get(artemisHome + "/data");

                Files.walkFileTree(dataDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            broker = ProcessBuilder.build("artemis standalone", absoluteHome, false, "run");
        } catch (Exception e) {
            throw new IllegalStateException("unable to start broker", e);
        }
    }

    public void stop()  {
        File absoluteHome = new File(artemisHome);
        try {
            broker = ProcessBuilder.build("artemis standalone", absoluteHome, false, "stop");
        } catch (Exception e) {
            throw new IllegalStateException("unable to start broker", e);
        }
    }

    public void kill() {
        broker.destroy();
    }
}

package org.apache.activemq.artemis.arquillian.docker;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ArtemisDockerContainerConfiguration implements ContainerConfiguration {
    String dockerHost;
    private String imageName;
    private String containerEnv;
    private String containerLogs;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    String containerName;
    String registryUsername;

    public String getDockerHost() {
        return dockerHost;
    }

    public void setDockerHost(String dockerHost) {
        this.dockerHost = dockerHost;
    }

    public String getRegistryUsername() {
        return registryUsername;
    }

    public void setRegistryUsername(String registryUsername) {
        this.registryUsername = registryUsername;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public void setRegistryPassword(String registryPassword) {
        this.registryPassword = registryPassword;
    }

    String registryPassword;

    @Override
    public void validate() throws ConfigurationException {

    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getContainerEnv() {
        return containerEnv;
    }

    public void setContainerEnv(String containerEnv) {
        this.containerEnv = containerEnv;
    }

    public String getContainerLogs() {
        return containerLogs;
    }

    public void setContainerLogs(String containerLogs) {
        this.containerLogs = containerLogs;
    }
}

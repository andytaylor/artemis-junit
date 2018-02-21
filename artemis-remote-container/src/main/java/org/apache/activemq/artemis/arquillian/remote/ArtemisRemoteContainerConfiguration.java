package org.apache.activemq.artemis.arquillian.remote;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ArtemisRemoteContainerConfiguration implements ContainerConfiguration {
    private String host;

    private String port;
    @Override
    public void validate() throws ConfigurationException {

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

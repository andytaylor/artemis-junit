package org.apache.activemq.artemis.arquillian;

public interface ArtemisDeployableContainer {
    void startBroker();

    String getCoreConnectUrl();

    void kill();
}

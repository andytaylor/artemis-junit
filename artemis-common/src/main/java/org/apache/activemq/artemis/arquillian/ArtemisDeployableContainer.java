package org.apache.activemq.artemis.arquillian;

public interface ArtemisDeployableContainer {
    void startBroker(boolean clean);

    String getCoreConnectUrl();

    void kill();

    void stopBroker(boolean wait);
}

package org.apache.activemq.artemis.broker;

import java.io.File;

public interface ArtemisBrokerProcess {
   void startBroker(boolean clean, File configuration);

   String getCoreConnectUrl();

   void kill();

   void stopBroker(boolean wait);
}

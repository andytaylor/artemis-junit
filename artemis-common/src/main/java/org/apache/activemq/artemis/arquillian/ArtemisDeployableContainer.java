package org.apache.activemq.artemis.arquillian;

import java.io.File;

public interface ArtemisDeployableContainer {
   void startBroker(boolean clean, File configuration);

   String getCoreConnectUrl();

   void kill();

   void stopBroker(boolean wait);
}

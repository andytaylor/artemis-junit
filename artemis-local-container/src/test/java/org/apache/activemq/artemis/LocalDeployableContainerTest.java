package org.apache.activemq.artemis;

import org.apache.activemq.artemis.arquillian.local.ArtemisContainerConfiguration;
import org.apache.activemq.artemis.arquillian.local.ArtemisLocalDeployableContainer;
import org.junit.Test;

public class LocalDeployableContainerTest {


   @Test
   public void testStartBroker() throws Exception {
      ArtemisLocalDeployableContainer container = new ArtemisLocalDeployableContainer();
      ArtemisContainerConfiguration configuration = new ArtemisContainerConfiguration();
      configuration.setArtemisHome("./target/apache-artemis-2.5.0-SNAPSHOT");
      configuration.setArtemisInstance("./target/standalone");
      configuration.setArtemisCreateCommand("--allow-anonymous --user admin --password password standalone");
      container.setup(configuration);
      container.start();
      container.startBroker(true);
      container.stopBroker(true);
   }

}

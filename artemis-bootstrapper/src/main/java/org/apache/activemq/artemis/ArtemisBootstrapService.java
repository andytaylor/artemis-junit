package org.apache.activemq.artemis;

import org.apache.activemq.artemis.broker.BrokerProcess;

import java.io.File;
import java.io.PrintWriter;

public class ArtemisBootstrapService {
   BrokerProcess brokerProcess;

   public ArtemisBootstrapService() {
      String artemisHome = System.getProperty("ARTEMIS_HOME");
      brokerProcess = new BrokerProcess(artemisHome, "broker");
      System.out.println("ArtemisBootstrapService.ArtemisBootstrapService " + artemisHome);
   }

   public void start(Boolean clean, String configuration, String artemisCreateCommand) throws Exception {
      File file = null;
      if (configuration != null && configuration.length() > 0) {
         file = new File("broker.xml");
         file.createNewFile();
         try (PrintWriter out = new PrintWriter("broker.xml")) {
            out.println(configuration);
         }
      }
      brokerProcess.startBroker(clean, file, artemisCreateCommand);
   }

   public void stop(boolean wait) {
      brokerProcess.stopBroker(wait);
/*      File absoluteHome = new File(artemisHome);
      try {
         broker = ProcessBuilder.build("artemis standalone", absoluteHome, false, "stop");
      } catch (Exception e) {
         throw new IllegalStateException("unable to start broker", e);
      }*/
   }

   public void kill() {
      brokerProcess.kill();
   }
}

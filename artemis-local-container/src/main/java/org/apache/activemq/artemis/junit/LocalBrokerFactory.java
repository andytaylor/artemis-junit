package org.apache.activemq.artemis.junit;

import org.apache.activemq.artemis.broker.ArtemisBrokerProcess;

public class LocalBrokerFactory implements BrokerRunner {
   @Override
   public void init(BrokerRegistry brokerRegistry) {
      System.out.println("LocalBrokerRunner.init");
      ArtemisLocalBrokerConfiguration configuration = new ArtemisLocalBrokerConfiguration();
      configuration.setArtemisHome("./target/apache-artemis-2.12.0-SNAPSHOT");
      configuration.setArtemisInstance("./target/standalone");
      configuration.setArtemisCreateCommand("--allow-anonymous --user admin --password password standalone");
      ArtemisLocalBrokerProcess brokerProcess = new ArtemisLocalBrokerProcess(configuration);
   }

   @Override
   public String getFactoryName() {
      return "local";
   }

   @Override
   public ArtemisBrokerProcess createBrokerProcess(String brokerName) {
      ArtemisLocalBrokerConfiguration configuration = new ArtemisLocalBrokerConfiguration();
      configuration.setArtemisHome("./target/apache-artemis-2.12.0-SNAPSHOT");
      configuration.setArtemisInstance("./target/standalone");
      configuration.setArtemisCreateCommand("--allow-anonymous --user admin --password password " + brokerName);
      return new ArtemisLocalBrokerProcess(configuration);
   }
}

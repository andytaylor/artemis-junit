package org.apache.activemq.artemis.junit;

import org.apache.activemq.artemis.broker.ArtemisBrokerProcess;

public interface BrokerRunner {
   void init(BrokerRegistry brokerRegistry);

   String getFactoryName();

   ArtemisBrokerProcess createBrokerProcess(String brokerName);
}

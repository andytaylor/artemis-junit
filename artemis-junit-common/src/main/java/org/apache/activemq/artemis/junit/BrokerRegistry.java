package org.apache.activemq.artemis.junit;

import org.apache.activemq.artemis.broker.ArtemisBrokerProcess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

public class BrokerRegistry {

   static BrokerRegistry INSTANCE = new BrokerRegistry();

   private boolean init = false;

   private HashMap<String, BrokerRunner> brokerRunners = new HashMap<>();

   public void init() {
      ServiceLoader<BrokerRunner> load = ServiceLoader.load(BrokerRunner.class);
      Iterator<BrokerRunner> iterator = load.iterator();
      for (BrokerRunner brokerRunner : load) {
         brokerRunner.init(this);
         brokerRunners.put(brokerRunner.getFactoryName(), brokerRunner);
      }
      init = true;
   }

   public ArtemisBrokerProcess createBrokerProcess(String brokerType, String brokerName) {
      if(!init) {
         init();
      }
      return brokerRunners.get(brokerType).createBrokerProcess(brokerName);
   }
}

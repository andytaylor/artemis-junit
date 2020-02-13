package org.apache.activemq.artemis.junit;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.broker.ArtemisBrokerProcess;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.HashMap;

public class BrokerLifecycleListener extends RunListener {

   HashMap<String, ArtemisBrokerProcess> brokerProcesses = new HashMap<>();

   public BrokerLifecycleListener() {
      super();
   }

   @Override
   public void testRunStarted(Description description) throws Exception {
      super.testRunStarted(description);
   }

   @Override
   public void testRunFinished(Result result) throws Exception {
      super.testRunFinished(result);
   }

   @Override
   public void testStarted(Description description) throws Exception {
      for (ArtemisBrokerProcess value : brokerProcesses.values()) {
         value.startBroker(true, null);
         verify(10);
      }
   }

   @Override
   public void testFinished(Description description) throws Exception {
      for (ArtemisBrokerProcess value : brokerProcesses.values()) {
         value.stopBroker(true);
      }
   }

   @Override
   public void testFailure(Failure failure) throws Exception {
      super.testFailure(failure);
   }

   @Override
   public void testAssumptionFailure(Failure failure) {
      super.testAssumptionFailure(failure);
   }

   @Override
   public void testIgnored(Description description) throws Exception {
      super.testIgnored(description);
   }

   public void addBroker(String brokerType, String brokerName) {
      ArtemisBrokerProcess brokerProcess = BrokerRegistry.INSTANCE.createBrokerProcess(brokerType, brokerName);
      brokerProcesses.put(brokerName, brokerProcess);
   }

   public boolean verify(int timeout) {
         try {
            String coreConnectUrl = "tcp://localhost:61616";
            ServerLocator serverLocator = ActiveMQClient.createServerLocator(coreConnectUrl);
            serverLocator.setInitialConnectAttempts(timeout);
            serverLocator.setRetryInterval(1000);
            ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
            return sessionFactory != null;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
   }

}

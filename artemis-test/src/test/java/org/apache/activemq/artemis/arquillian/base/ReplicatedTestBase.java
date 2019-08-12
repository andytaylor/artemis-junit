package org.apache.activemq.artemis.arquillian.base;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.arquillian.ArtemisContainerController;
import org.apache.activemq.artemis.arquillian.BrokerFuture;
import org.apache.activemq.artemis.core.client.impl.ClientSessionFactoryInternal;
import org.apache.activemq.artemis.core.client.impl.ServerLocatorInternal;
import org.apache.activemq.artemis.core.client.impl.Topology;
import org.apache.activemq.artemis.core.client.impl.TopologyMemberImpl;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.net.URL;
import java.util.Collection;

public abstract class ReplicatedTestBase {

   @ArquillianResource
   protected ArtemisContainerController controller;



   @Before
   public void startBrokers() throws Exception {
      BrokerFuture live1 = controller.start("live1", true, getLiveBrokerFile(getLiveBrokerConfig()));
      controller.start("replica1", true, getBackupBrokerFile(getBackupBrokerConfig()));
      Assert.assertTrue(awaitTopology("live1", 30000, 2));
      BrokerFuture live2 = controller.start("live2", true, getLiveBrokerFile(getLiveBrokerConfig()));
      controller.start("replica2", true, getBackupBrokerFile(getBackupBrokerConfig()));
      Assert.assertTrue(awaitTopology("live1", 30000, 4));
      BrokerFuture live3 = controller.start("live3", true, getLiveBrokerFile(getLiveBrokerConfig()));
      controller.start("replica3", true, getBackupBrokerFile(getBackupBrokerConfig()));
      Assert.assertTrue(awaitTopology("live1", 30000, 6));
      //controller.createQueue("testQueue", "live1", "live2", "live3");
   }

   @After
   public void stopBrokers() {
      controller.stop("replica1", false);
      controller.stop("replica2", false);
      controller.stop("replica3", false);
      controller.stop("live1", false);
      controller.stop("live2", false);
      controller.stop("live3", false);
   }

   public abstract String getLiveBrokerConfig();

   public abstract String getBackupBrokerConfig();

   public File getLiveBrokerFile(String config) {
      if (config != null) {
         URL targetUrl = getClass().getResource(config);
         return new File(targetUrl.getFile());
      }
      return null;
   }

   public File getBackupBrokerFile(String config) {
      if (config != null) {
         URL targetUrl = getClass().getResource(config);
         return new File(targetUrl.getFile());
      }
      return null;
   }

   protected boolean awaitCluster(long timeout) throws Exception {
      String live1 = controller.getCoreConnectUrl("live1");
      String live2 = controller.getCoreConnectUrl("live2");
      String live3 = controller.getCoreConnectUrl("live3");
      try (ServerLocatorInternal serverLocator1 = (ServerLocatorInternal) ActiveMQClient.createServerLocator(live1);
           ServerLocatorInternal serverLocator2 = (ServerLocatorInternal) ActiveMQClient.createServerLocator(live2);
           ServerLocatorInternal serverLocator3 = (ServerLocatorInternal) ActiveMQClient.createServerLocator(live3)) {

         serverLocator1.connect();
         serverLocator2.connect();
         serverLocator3.connect();
         long start = System.currentTimeMillis();

         int backupCount = 0;

         do {
            backupCount += getBackupCount(serverLocator1);
            backupCount += getBackupCount(serverLocator2);
            backupCount += getBackupCount(serverLocator3);
            if (backupCount == 9) {
               return true;
            }
            backupCount = 0;
            Thread.sleep(10);
         }
         while (System.currentTimeMillis() - start < timeout);
      }

      return false;
   }

   private boolean awaitTopology(String broker, long timeout, int nodes) throws Exception {
      String live1 = controller.getCoreConnectUrl(broker);
      live1 += "?retryInterval=1000&retryIntervalMultiplier=1.0&initialConnectAttempts=25";
      boolean connected = false;
      ServerLocatorInternal serverLocator1 = null;
      long start = System.currentTimeMillis();
      do {
         try  {
            if (!connected) {
               serverLocator1 = (ServerLocatorInternal) ActiveMQClient.createServerLocator(live1);
               serverLocator1.connect();
               connected = true;
            }
            // topology is not available immediately after connecting
            int backupCount = getNodeCount(serverLocator1);

            if (backupCount == nodes) {
               return true;
            }
            Thread.sleep(10);
         }
         catch(Exception e) {
            Thread.sleep(10);
         }
      } while (System.currentTimeMillis() - start < timeout);

      return false;
   }

   private int getNodeCount(ServerLocator serverLocator1) {
      int count = 0;
      Topology t1 = serverLocator1.getTopology();
      Collection<TopologyMemberImpl> members = t1.getMembers();
      for (TopologyMemberImpl member : members) {
         count++;
         if (member.getBackup() != null) {
            count++;
         }
      }
      return count;
   }

   private int getBackupCount(ServerLocator serverLocator1) {
      int count = 0;
      Topology t1 = serverLocator1.getTopology();
      Collection<TopologyMemberImpl> members = t1.getMembers();
      for (TopologyMemberImpl member : members) {
         if (member.getBackup() != null) {
            count++;
         }
      }
      return count;
   }


   public static void main(String[] args) throws Exception {
      try (ServerLocatorInternal locator = (ServerLocatorInternal) ActiveMQClient.createServerLocator("tcp://172.17.0.2:61616?ha=true&retryInterval=1000&retryIntervalMultiplier=1.0&reconnectAttempts=-1")) {
         locator.setCallTimeout(120000).setCallFailoverTimeout(120000);
         ClientSessionFactoryInternal sessionFactory = (ClientSessionFactoryInternal) locator.createSessionFactory();
         ClientSession session = sessionFactory.createSession();
         ClientProducer producer = session.createProducer("testQueue");
         for (int i = 0; i < 100; i++) {
            ClientMessage message = session.createMessage(true);
            producer.send(message);
         }
         ClientConsumer consumer = session.createConsumer("testQueue");
         session.start();
         for (int i = 0; i < 100; i++) {
            ClientMessage message = consumer.receive(60000);
            Assert.assertNotNull(message);
         }
      }
   }
}

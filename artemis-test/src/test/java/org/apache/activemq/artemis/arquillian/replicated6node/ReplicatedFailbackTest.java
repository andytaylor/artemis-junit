package org.apache.activemq.artemis.arquillian.replicated6node;

import org.apache.activemq.artemis.api.core.JsonUtil;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.arquillian.base.ReplicatedTestBase;
import org.apache.activemq.artemis.arquillian.categories.Replicated6Node;
import org.apache.activemq.artemis.core.client.impl.ClientSessionFactoryInternal;
import org.apache.activemq.artemis.core.client.impl.ServerLocatorInternal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.json.JsonArray;
import javax.json.JsonObject;

@Category(Replicated6Node.class)
public class ReplicatedFailbackTest extends ReplicatedTestBase {
  /* @Test
   public void checkFailback() throws Exception {
      controller.kill("live1");
      String replica1 = controller.getCoreConnectUrl("replica1");
      try (ServerLocatorInternal locator = (ServerLocatorInternal) ActiveMQClient.createServerLocator(replica1)) {
         locator.setInitialConnectAttempts(30).setRetryInterval(1000);
         ClientSessionFactoryInternal sessionFactory = (ClientSessionFactoryInternal) locator.createSessionFactory();
         ClientSession session = sessionFactory.createSession();
      }

      System.out.println("***************************** restarting live node *********************************");

      controller.start("live1", false, getBackupBrokerFile(getLiveBrokerConfig()));

      String live1 = controller.getCoreConnectUrl("live1");
      try (ServerLocatorInternal locator = (ServerLocatorInternal) ActiveMQClient.createServerLocator(live1)) {
         locator.setInitialConnectAttempts(30).setRetryInterval(1000);
         ClientSessionFactoryInternal sessionFactory = (ClientSessionFactoryInternal) locator.createSessionFactory();
         ClientSession session = sessionFactory.createSession();
      }

      System.out.println("***************************** restarted live node *********************************");

      awaitCluster(30000);

      String reply = controller.managementRequest("live1", "broker", "listNetworkTopology");

      JsonArray array = JsonUtil.readJsonArray(reply);

      Assert.assertEquals(3, array.size());

      for (int i = 0; i < array.size();i ++) {
         JsonObject jsonObject = (JsonObject) array.get(i);
         Assert.assertTrue(jsonObject.containsKey("nodeID"));
         Assert.assertTrue(jsonObject.containsKey("live"));
         Assert.assertTrue(jsonObject.containsKey("backup"));
         System.out.println("ReplicatedFailbackTest.checkFailback");
      }

      System.out.println("reply = " + array);
   }

   @Override
   public String getLiveBrokerConfig() {
      return "/replicated/broker-live-check.xml";
   }

   @Override
   public String getBackupBrokerConfig() {
      return "/replicated/broker-allow-failback.xml";
   }*/
}

package org.apache.activemq.artemis.test.replicated6node;

import org.apache.activemq.artemis.test.base.ReplicatedTestBase;
import org.apache.activemq.artemis.test.categories.Replicated6Node;
import org.junit.experimental.categories.Category;

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

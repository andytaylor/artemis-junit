/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.arquillian;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.arquillian.categories.Replicated6Node;
import org.apache.activemq.artemis.arquillian.categories.Standalone;
import org.apache.activemq.artemis.core.client.impl.ClientSessionFactoryInternal;
import org.apache.activemq.artemis.core.client.impl.ServerLocatorInternal;
import org.apache.activemq.artemis.core.client.impl.Topology;
import org.apache.activemq.artemis.core.client.impl.TopologyMemberImpl;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Collection;

@RunWith(Arquillian.class)
@Category(Replicated6Node.class)
public class ArtemisReplicatedContainerTest {

   @ArquillianResource
   protected ArtemisContainerController controller;

   @Before
   public void startBrokers() throws Exception {
      BrokerFuture live1 = controller.start("live1", true);
      BrokerFuture live2 = controller.start("live2", true);
      BrokerFuture live3 = controller.start("live3", true);
      controller.start("replica1", true);
      controller.start("replica2", true);
      controller.start("replica3", true);
      Assert.assertTrue(live1.awaitBrokerStart(30000));
      Assert.assertTrue(live2.awaitBrokerStart(30000));
      Assert.assertTrue(live3.awaitBrokerStart(30000));
      Assert.assertTrue(awaitCluster(30000));
      controller.createQueue("testQueue", "live1", "live2", "live3");
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

   @Test
   @RunAsClient
   public void simpleKill() throws Exception {
      String live1 = controller.getCoreConnectUrl("live1");
      live1 += "?ha=true&retryInterval=1000&retryIntervalMultiplier=1.0&reconnectAttempts=-1";
      try (ServerLocatorInternal locator = (ServerLocatorInternal) ActiveMQClient.createServerLocator(live1)) {
         ClientSessionFactoryInternal sessionFactory = (ClientSessionFactoryInternal) locator.createSessionFactory();
         ClientSession session = sessionFactory.createSession();
         ClientProducer producer = session.createProducer("testQueue");
         for (int i = 0; i < 100; i++) {
            ClientMessage message = session.createMessage(true);
            producer.send(message);
         }
         controller.kill("live1");
         ClientConsumer consumer = session.createConsumer("testQueue");
         session.start();
         for (int i = 0; i < 100; i++) {
            ClientMessage message = consumer.receive(60000);
            Assert.assertNotNull(message);
         }
      }
   }

   private boolean awaitCluster(long timeout) throws Exception {
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
}

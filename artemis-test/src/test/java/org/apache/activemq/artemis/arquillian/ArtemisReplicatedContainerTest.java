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

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.arquillian.categories.Replicated6Node;
import org.apache.activemq.artemis.arquillian.categories.Standalone;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Category(Replicated6Node.class)
public class ArtemisReplicatedContainerTest {

   @ArquillianResource
   protected ArtemisContainerController controller;

    @Before
    public void startBroker() throws Exception {
        controller.startAndWait("live1", 30);
        controller.startAndWait("live2", 30);
        controller.startAndWait("live3", 30);
        controller.start("replica1");
        controller.start("replica2");
        controller.start("replica3");
    }

    @After
    public void stopBroker() {
        controller.stop("replica1");
        controller.stop("replica2");
        controller.stop("replica3");
        controller.stop("live1");
        controller.stop("live2");
        controller.stop("live3");
    }

   @Test
   @RunAsClient
   public void shouldWaitForBroker() throws Exception {

       String live1 = controller.getCoreConnectUrl("live1");
       String live2 = controller.getCoreConnectUrl("live2");
       String live3 = controller.getCoreConnectUrl("live3");
       try (ServerLocator serverLocator = ActiveMQClient.createServerLocator(live1)) {
           ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
           Assert.assertNotNull(sessionFactory);
       }
       try (ServerLocator serverLocator = ActiveMQClient.createServerLocator(live2)) {
           ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
           Assert.assertNotNull(sessionFactory);
       }
       try (ServerLocator serverLocator = ActiveMQClient.createServerLocator(live3)) {
           ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
           Assert.assertNotNull(sessionFactory);
       }
   }
}

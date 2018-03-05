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
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
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
@Category(Standalone.class)
public class ArtemisContainerTest {

   @ArquillianResource
   protected ArtemisContainerController controller;

   @Before
   public void startBroker() throws Exception {
       BrokerFuture standalone = controller.start("standalone");
       Assert.assertTrue(standalone.awaitBrokerStart(30000));
   }

   @After
   public void stopBroker() {
        controller.stop("standalone");
   }

   @Test
   @RunAsClient
   public void shouldWaitForBroker() throws Exception {
       String standalone = controller.getCoreConnectUrl("standalone");
       try (ServerLocator serverLocator = ActiveMQClient.createServerLocator(standalone)) {
           ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
           Assert.assertNotNull(sessionFactory);
       }
   }
}

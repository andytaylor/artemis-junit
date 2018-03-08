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
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientRequestor;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.api.core.management.ManagementHelper;
import org.apache.activemq.artemis.api.core.management.ResourceNames;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.ContainerRegistry;
import org.jboss.arquillian.container.spi.client.deployment.TargetDescription;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

public class ArtemisContainerController {

   @Inject
   private Instance<ContainerRegistry> containerRegistry;

   public BrokerFuture start(String containerQualifier, boolean clean) {
      ArtemisDeployableContainer deployableContainer = getArtemisDeployableContainer(containerQualifier);
      deployableContainer.startBroker(clean);
      return timeout -> {
         try {
            String coreConnectUrl = deployableContainer.getCoreConnectUrl();
            ServerLocator serverLocator = ActiveMQClient.createServerLocator(coreConnectUrl);
            serverLocator.setInitialConnectAttempts(timeout);
            serverLocator.setRetryInterval(1000);
            ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
            return sessionFactory != null;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
      };
   }

   public void kill(String containerQualifier) {
      ArtemisDeployableContainer deployableContainer = getArtemisDeployableContainer(containerQualifier);
      deployableContainer.kill();
   }

   public String getCoreConnectUrl(String containerQualifier) {
      ArtemisDeployableContainer deployableContainer = getArtemisDeployableContainer(containerQualifier);
      return deployableContainer.getCoreConnectUrl();
   }

   public void stop(String containerQualifier, boolean wait) {
      ArtemisDeployableContainer artemisDeployableContainer = getArtemisDeployableContainer(containerQualifier);
      artemisDeployableContainer.stopBroker(wait);
   }

   private ArtemisDeployableContainer getArtemisDeployableContainer(String containerQualifier) {
      ContainerRegistry registry = containerRegistry.get();
      if (registry == null) {
         throw new IllegalArgumentException("No container registry in context");
      }

      Container container = registry.getContainer(new TargetDescription(containerQualifier));

      if (container == null) {
         throw new IllegalArgumentException("No container in Registry named " + containerQualifier);
      }
      return (ArtemisDeployableContainer) container.getDeployableContainer();
   }

   public void createQueue(String testQueue, String... containers) throws Exception {
      for (String container : containers) {
         String coreConnectUrl = getCoreConnectUrl(container);
         ServerLocator locator = ActiveMQClient.createServerLocator(coreConnectUrl);
         ClientSessionFactory sessionFactory = locator.createSessionFactory();
         ClientSession session = sessionFactory.createSession();
         ClientRequestor requestor = new ClientRequestor(session, "activemq.management");
         ClientMessage message = session.createMessage(false);
         ManagementHelper.putOperationInvocation(message, "broker", "createQueue", "testQueue", "testQueue", true, "ANYCAST");
         session.start();
         ClientMessage reply = requestor.request(message);
         System.out.println("reply = " + reply);
      }
   }
}

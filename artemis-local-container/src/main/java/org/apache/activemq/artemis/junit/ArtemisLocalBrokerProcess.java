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
package org.apache.activemq.artemis.junit;

import org.apache.activemq.artemis.broker.ArtemisBrokerProcess;
import org.apache.activemq.artemis.broker.BrokerProcess;

import java.io.File;

public class ArtemisLocalBrokerProcess implements ArtemisBrokerProcess {

   private BrokerProcess brokerProcess;

   private ArtemisLocalBrokerConfiguration containerConfiguration;

   private String coreConnectUrl;

   public ArtemisLocalBrokerProcess(ArtemisLocalBrokerConfiguration configuration) {
      this.containerConfiguration = configuration;
      brokerProcess = new BrokerProcess(containerConfiguration.getArtemisHome(), containerConfiguration.getArtemisInstance());
   }

   public void stop()  {
      brokerProcess.stop();
      System.out.println("****************** Destroying Broker Process ********************");
   }

   public void kill() {
      brokerProcess.kill();
   }

   @Override
   public void stopBroker(boolean wait) {
      brokerProcess.stopBroker(wait);
   }

   public void startBroker(boolean clean, File configuration) {
      brokerProcess.startBroker(clean, configuration, containerConfiguration.getArtemisCreateCommand());
   }

   public String getCoreConnectUrl() {
      if (coreConnectUrl == null) {
         coreConnectUrl = brokerProcess.getCoreConnectURL();
      }
      return coreConnectUrl;
   }
/*
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

   public String managementRequest(String container, String resource, String operationName, Object... args) {
      try {

         String coreConnectUrl = getCoreConnectUrl(container);
         ServerLocator locator = ActiveMQClient.createServerLocator(coreConnectUrl);
         ClientSessionFactory sessionFactory = locator.createSessionFactory();
         ClientSession session = sessionFactory.createSession();
         ClientRequestor requestor = new ClientRequestor(session, "activemq.management");
         ClientMessage message = session.createMessage(false);
         ManagementHelper.putOperationInvocation(message, resource, operationName, args);
         session.start();
         ClientMessage reply = requestor.request(message);
         Object result = ManagementHelper.getResult(reply);
         return result != null ? result.toString() : null;
      } catch (Exception e) {
         return null;
      }
   }*/
}

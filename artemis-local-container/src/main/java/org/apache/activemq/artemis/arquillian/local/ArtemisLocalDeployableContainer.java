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
package org.apache.activemq.artemis.arquillian.local;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.arquillian.ArtemisDeployableContainer;
import org.apache.activemq.artemis.broker.BrokerProcess;
import org.apache.activemq.artemis.cli.process.ProcessBuilder;
import org.apache.activemq.artemis.configuration.XMLUpdater;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.deployers.impl.FileConfigurationParser;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ArtemisLocalDeployableContainer implements DeployableContainer<ArtemisContainerConfiguration>, ArtemisDeployableContainer {

   private BrokerProcess brokerProcess;

   private ArtemisContainerConfiguration containerConfiguration;

   private String coreConnectUrl;

   public ArtemisLocalDeployableContainer() {
      super();
   }

   public Class<ArtemisContainerConfiguration> getConfigurationClass() {
      return ArtemisContainerConfiguration.class;
   }

   public void setup(ArtemisContainerConfiguration containerConfiguration) {
      this.containerConfiguration = containerConfiguration;
   }

   public void start() throws LifecycleException {
      brokerProcess = new BrokerProcess(containerConfiguration.getArtemisHome(), containerConfiguration.getArtemisInstance());
   }

   public void stop() throws LifecycleException {
      brokerProcess.stop();
      System.out.println("****************** Destroying Broker Process ********************");
   }

   public ProtocolDescription getDefaultProtocol() {
      return new ProtocolDescription("artemis local");
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

   public void deploy(Descriptor descriptor) throws DeploymentException {
   }

   public void undeploy(Descriptor descriptor) throws DeploymentException {
   }

   public void undeploy(Archive archive) throws DeploymentException {
   }

   public ProtocolMetaData deploy(Archive archive) throws DeploymentException {
      return null;
   }
}

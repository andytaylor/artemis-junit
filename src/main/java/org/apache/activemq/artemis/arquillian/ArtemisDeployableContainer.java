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

import org.apache.activemq.artemis.cli.process.ProcessBuilder;
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

public class ArtemisDeployableContainer implements DeployableContainer<ArtemisContainerConfiguration> {

   private Process broker;
   private ArtemisContainerConfiguration containerConfiguration;

   public ArtemisDeployableContainer() {
      super();
   }

   public Class<ArtemisContainerConfiguration> getConfigurationClass() {
      return ArtemisContainerConfiguration.class;
   }

   public void setup(ArtemisContainerConfiguration containerConfiguration) {
      this.containerConfiguration = containerConfiguration;
      if (containerConfiguration.getBrokerXml() != null) {
         File from = new File(containerConfiguration.getBrokerXml());
         File to = new File(containerConfiguration.getArtemisHome() + "/etc/broker.xml");
         try {
            copyFile(from, to);
         } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
         }
      }
   }

   public void start() throws LifecycleException {
      File absoluteHome = new File(containerConfiguration.getArtemisHome());
      try {
         broker = ProcessBuilder.build("artemis standalone", absoluteHome, false, "run");
      } catch (Exception e) {
         throw new LifecycleException("unable to start broker", e);
      }
   }

   public void stop() throws LifecycleException {
      broker.destroy();
   }

   public ProtocolDescription getDefaultProtocol() {
      return new ProtocolDescription("artemis 2.0");
   }

   public void deploy(Descriptor descriptor) throws DeploymentException {
      System.out.println("ArtemisDeployableContainer.deploy");
   }

   public void undeploy(Descriptor descriptor) throws DeploymentException {
      System.out.println("ArtemisDeployableContainer.undeploy");
   }

   public void undeploy(Archive archive) throws DeploymentException {
      System.out.println("ArtemisDeployableContainer.undeploy");
   }

   public ProtocolMetaData deploy(Archive archive) throws DeploymentException {
      return new ProtocolMetaData().addContext(new ArtemisContext("localhost"));
   }

   public void kill() {
      broker.destroy();
   }

   public static void copyFile(File sourceFile, File destFile) throws IOException {
      if(!destFile.exists()) {
         destFile.createNewFile();
      }

      FileChannel source = null;
      FileChannel destination = null;

      try {
         source = new FileInputStream(sourceFile).getChannel();
         destination = new FileOutputStream(destFile).getChannel();
         destination.transferFrom(source, 0, source.size());
      }
      finally {
         if(source != null) {
            source.close();
         }
         if(destination != null) {
            destination.close();
         }
      }
   }
}

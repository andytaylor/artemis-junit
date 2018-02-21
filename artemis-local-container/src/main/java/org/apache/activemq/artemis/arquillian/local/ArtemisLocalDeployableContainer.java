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
import org.apache.activemq.artemis.cli.process.ProcessBuilder;
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
import java.util.Map;
import java.util.Set;

public class ArtemisLocalDeployableContainer implements DeployableContainer<ArtemisContainerConfiguration>, ArtemisDeployableContainer {

   private Process broker;
   private ArtemisContainerConfiguration containerConfiguration;
   private Configuration configuration;
   private String coreConnectUrl;

   public ArtemisLocalDeployableContainer() {
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
      FileConfigurationParser parser = new FileConfigurationParser();
      try {
         configuration = parser.parseMainConfig(new FileInputStream(new File(containerConfiguration.getArtemisHome() + "/etc/broker.xml")));
      } catch (Exception e) {
         throw new LifecycleException(e.getMessage(), e.getCause());
      }
   }

   public void stop() throws LifecycleException {
      broker.destroy();
   }

   public ProtocolDescription getDefaultProtocol() {
      return new ProtocolDescription("artemis local");
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

   public void startBroker() {
      File absoluteHome = new File(containerConfiguration.getArtemisHome());
      try {
         broker = ProcessBuilder.build("artemis standalone", absoluteHome, false, "run");
      } catch (Exception e) {
         throw new IllegalStateException("unable to start broker", e);
      }
   }

   public String getCoreConnectUrl() {
      if (coreConnectUrl == null) {
         String host = null;
         String port = null;
         Set<TransportConfiguration> acceptorConfigurations = configuration.getAcceptorConfigurations();
         for (TransportConfiguration acceptorConfiguration : acceptorConfigurations) {
            String factoryClassName = acceptorConfiguration.getFactoryClassName();
            if (factoryClassName.equals("org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory")) {
               Map<String, Object> params = acceptorConfiguration.getParams();
               for (Object key : params.keySet()) {
                  if (key.equals("protocols")) {
                     String protocols = (String) params.get(key);
                     if (protocols.contains("CORE")) {
                        host = (String) params.get("host");
                        port = (String) params.get("port");
                        break;
                     }
                  }
               }
            }
         }
         if (host == null || host.equals("0.0.0.0")) {
            host = "localhost";
         }
         if (port == null) {
            port = "61616";
         }
         coreConnectUrl = "tcp://" + host + ":" + port;
      }
      return coreConnectUrl;
   }
}

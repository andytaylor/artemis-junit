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

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ArtemisContainerConfiguration implements ContainerConfiguration {
   private String brokerXml;
   private String artemisHome;

   public String getArtemisHome() {
      return artemisHome;
   }

   public void setArtemisHome(String artemisHome) {
      this.artemisHome = artemisHome;
   }

   public void validate() throws ConfigurationException {
      if (artemisHome == null) {
         throw new ConfigurationException("property artemisHome cannot be null");
      }
   }

   public String getBrokerXml() {
      return brokerXml;
   }

   public void setBrokerXml(String brokerXml) {
      this.brokerXml = brokerXml;
   }
}

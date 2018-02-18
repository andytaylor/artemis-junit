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


import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.ContainerRegistry;
import org.jboss.arquillian.container.spi.client.deployment.TargetDescription;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;

public class ArtemisContainerController {
   @Inject
   private Instance<ServiceLoader> serviceLoader;

   @Inject
   private Instance<ContainerRegistry> containerRegistry;

   public void kill(String containerQualifier) {
      ContainerRegistry registry = containerRegistry.get();
      if (registry == null) {
         throw new IllegalArgumentException("No container registry in context");
      }

      Container container = registry.getContainer(new TargetDescription(containerQualifier));

      if (container == null) {
         throw new IllegalArgumentException("No container in Registry named " + containerQualifier);
      }
      ArtemisDeployableContainer deployableContainer = (ArtemisDeployableContainer) container.getDeployableContainer();
      deployableContainer.kill();
      //event.fire(new KillContainer(container));
   }
}

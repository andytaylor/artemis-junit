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

import org.jboss.arquillian.container.spi.client.deployment.Validate;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.Collection;

/**
 * @author <a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>
 */
public class ArtemisProtocolDeploymentPackager implements DeploymentPackager {
   public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> processors) {
      JavaArchive protocol = ShrinkWrap.create(JavaArchive.class);

              Archive<?> applicationArchive = testDeployment.getApplicationArchive();
              Collection<Archive<?>> auxiliaryArchives = testDeployment.getAuxiliaryArchives();

              Processor processor = new Processor(testDeployment, processors);

              if (Validate.isArchiveOfType(JavaArchive.class, applicationArchive)) {
                  return handleArchive(applicationArchive.as(JavaArchive.class), auxiliaryArchives, protocol, processor);
              }

              throw new IllegalArgumentException(ArtemisProtocolDeploymentPackager.class.getName() +
                  " can not handle archive of type " + applicationArchive.getClass().getName());
          }

          private Archive<?> handleArchive(WebArchive applicationArchive, Collection<Archive<?>> auxiliaryArchives,
              JavaArchive protocol, Processor processor) {
              applicationArchive
                  .addAsLibraries(
                      auxiliaryArchives.toArray(new Archive<?>[0]));

              // Can be null when reusing logic in EAR packaging
              if (protocol != null) {
                  applicationArchive.addAsLibrary(protocol);
              }
              processor.process(applicationArchive);
              return applicationArchive;
          }

          private Archive<?> handleArchive(JavaArchive applicationArchive, Collection<Archive<?>> auxiliaryArchives,
              JavaArchive protocol, Processor processor) {
              return handleArchive(
                  ShrinkWrap.create(WebArchive.class, "test.war")
                      .addAsLibrary(applicationArchive),
                  auxiliaryArchives,
                  protocol,
                  processor);
          }
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.arquillian;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import java.lang.annotation.Annotation;

/**
 * ContainerControllerProvider
 *
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * @version $Revision: $
 */
public class ArtemisControllerProvider implements ResourceProvider {
   @Inject
   private Instance<ArtemisContainerController> controller;

   public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
      return controller.get();
   }

   public boolean canProvide(Class<?> type) {
      return type.isAssignableFrom(ArtemisContainerController.class);
   }
}

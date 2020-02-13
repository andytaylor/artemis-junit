package org.apache.activemq.artemis.junit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.TestClass;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;



public class ArtemisJunitRunner  extends BlockJUnit4ClassRunner {
   private final Class<?> fTestClass;

   public ArtemisJunitRunner(Class<?> klass) throws InitializationError {
      super(klass);
      fTestClass = klass;
   }

   @Override public void run(RunNotifier notifier){
      BrokerLifecycleListener brokerLifecycleListener = new BrokerLifecycleListener();
      Field[] declaredFields = fTestClass.getDeclaredFields();

      for (Field declaredField : declaredFields) {
         Annotation[] declaredAnnotations = declaredField.getDeclaredAnnotations();
         for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation instanceof Broker) {
               Broker broker = (Broker) declaredAnnotation;
               brokerLifecycleListener.addBroker(broker.BrokerType(), broker.brokerName());
            }
         }
      }
      notifier.addListener(brokerLifecycleListener);
      notifier.fireTestRunStarted(getDescription());
      super.run(notifier);
   }
}

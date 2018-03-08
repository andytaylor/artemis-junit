package org.apache.activemq.artemis.arquillian;

public interface BrokerFuture {
   boolean awaitBrokerStart(int timeout);
}

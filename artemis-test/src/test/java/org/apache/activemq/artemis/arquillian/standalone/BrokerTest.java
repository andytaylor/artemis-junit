package org.apache.activemq.artemis.arquillian.standalone;

import org.apache.activemq.artemis.broker.BrokerProcess;
import org.apache.activemq.artemis.junit.Broker;
import org.apache.activemq.artemis.junit.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(org.apache.activemq.artemis.junit.ArtemisJunitRunner.class)
public class BrokerTest {

   @Broker(brokerName = "broker1")
   BrokerProcess brokerProcess;

   @Test
   public void brokerStartTest() {
      Result prodResult = new BasicSender().runClient();
      Result recResult = new BasicReceiver().runClient();

      Assert.assertEquals(1000, prodResult.accepted);
      Assert.assertEquals(1000, recResult.accepted);
   }
}

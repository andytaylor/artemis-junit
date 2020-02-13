package org.apache.activemq.artemis.arquillian.standalone;

import org.apache.activemq.artemis.junit.Client;
import org.apache.activemq.artemis.junit.Result;
import org.apache.qpid.jms.provider.exceptions.ProviderDeliveryModifiedException;
import org.apache.qpid.jms.provider.exceptions.ProviderDeliveryReleasedException;
import org.apache.qpid.jms.provider.exceptions.ProviderSendTimedOutException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

public class BasicSender implements Client {

    public static void main(String[] args) {
        BasicSender basicSender = new BasicSender();
        basicSender.runClient();
    }

    @Override
    public Result runClient() {
        Result r = new Result();
        try {
            Context context = Config.createContext();

            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(Config.LOOKUP_CONNECTION_FACTORY);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) context.lookup(Config.LOOKUP_QUEUE);

            MessageProducer messageProducer = session.createProducer(destination);
            String messageBody = MessageGenerator.generateMessage();
            while (r.accepted < Integer.parseInt(Config.getProperty(Config.MSG_COUNT))) {
                TextMessage message = session.createTextMessage(messageBody);
                r.delivered++;
                try {
                    messageProducer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
                    r.accepted++;
                } catch (JMSException e) {
                    if (e.getCause() instanceof ProviderDeliveryModifiedException || e.getCause() instanceof ProviderDeliveryReleasedException) {
                        r.released++;
                        System.out.println("Message released...");
                    } else if (e.getCause() instanceof ProviderSendTimedOutException) {
                        r.errormsg = "Timed out";
                        break;
                    } else {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }

            connection.close();
            context.close();
        } catch (Exception exp) {
            exp.printStackTrace();
            r.errormsg = "Unexpected error: " + exp.getMessage();
        }

        // print result to be parsed in the test suite
        System.out.println(r);

        // If errormsg is not empty, exit with 1
        if (!"".equals(r.errormsg)) {
            System.exit(1);
        }

        return r;
    }
}

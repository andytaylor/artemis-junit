package org.apache.activemq.artemis.test.standalone;

import org.apache.activemq.artemis.junit.Client;
import org.apache.activemq.artemis.junit.Result;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

public class BasicReceiver implements Client {
    public static void main(String[] args) {
        BasicReceiver basicReceiver = new BasicReceiver();
        basicReceiver.runClient();
    }

    public Result runClient() {
        Result r = new Result();
        try {
            Context context = Config.createContext();

            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(Config.LOOKUP_CONNECTION_FACTORY);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) context.lookup(Config.LOOKUP_QUEUE);

            MessageConsumer messageConsumer = session.createConsumer(destination);
            String expectedMessageBody = MessageGenerator.generateMessage();

            while (r.accepted < Integer.parseInt(Config.getProperty(Config.MSG_COUNT))) {
                TextMessage msg = (TextMessage) messageConsumer.receive(Integer.parseInt(Config.getProperty(Config.AMQP_TIMEOUT)));
                if (msg == null) {
                    r.errormsg = "Timed out";
                    break;
                }
                if (!expectedMessageBody.equals(msg.getText())) {
                    r.errormsg = "Invalid message received";
                    break;
                }
                r.delivered++;
                r.accepted++;
                //System.out.println(msg.getText());
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

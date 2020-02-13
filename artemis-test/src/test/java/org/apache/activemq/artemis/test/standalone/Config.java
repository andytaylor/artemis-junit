package org.apache.activemq.artemis.test.standalone;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads property value from config.ini file or if there is an
 * environment variable available with same name, then it takes
 * precedence.
 */
public class Config {
    private static final String CONFIG_FILE = "/config.properties";
    public static final Properties props = new Properties();

    public static final String AMQP_URL = "AMQP_URL";
    public static final String AMQP_ADDR = "AMQP_ADDR";
    public static final String AMQP_TIMEOUT = "AMQP_TIMEOUT";
    public static final String MSG_COUNT = "MSG_COUNT";
    public static final String MSG_SIZE = "MSG_SIZE";
    public static final String MSG_PATTERN = "MSG_PATTERN";

    private static final String INITIAL_CONTEXT = "org.apache.qpid.jms.jndi.JmsInitialContextFactory";
    public static final String LOOKUP_CONNECTION_FACTORY = "amqpFactory";
    public static final String LOOKUP_QUEUE = "testQueue";

    static {
        InputStream is = Config.class.getResourceAsStream(CONFIG_FILE);
        try {
            props.load(is);
        } catch (IOException e) {
            System.err.println("Unable to load properties");
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return System.getenv().getOrDefault(name, props.getProperty(name));
    }

    public static InitialContext createContext() {
        Properties p = new Properties();

        p.put("java.naming.factory.initial", INITIAL_CONTEXT);
        p.put("connectionfactory.amqpFactory", getProperty(AMQP_URL) +
                "?jms.sendTimeout=" + Integer.parseInt(Config.getProperty(Config.AMQP_TIMEOUT)));
        p.put("queue.testQueue", getProperty(AMQP_ADDR));

        InitialContext ic = null;
        try {
             ic = new InitialContext(p);
        } catch (NamingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return ic;
    }
}

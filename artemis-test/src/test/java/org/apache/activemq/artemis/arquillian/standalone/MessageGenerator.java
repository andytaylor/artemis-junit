package org.apache.activemq.artemis.arquillian.standalone;

public class MessageGenerator {
    public static String generateMessage() {
        int size = Integer.parseInt(Config.getProperty(Config.MSG_SIZE));
        return generateMessage(size);
    }
    public static String generateMessage(int size) {
        StringBuilder sb = new StringBuilder();
        String pattern = Config.getProperty(Config.MSG_PATTERN);
        int patLen = pattern.length();
        while (sb.length() < size) {
            int endi = pattern.length();
            if (sb.length() + patLen < size) {
                sb.append(pattern);
            } else {
                sb.append(pattern.substring(0, size-sb.length()));
            }
        }
        return sb.toString();
    }

}

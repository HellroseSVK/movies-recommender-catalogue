package org.movierc.events;

public class EventUtil {
    /**
     * Returns a queue routing key for all events of a given type.
     *
     * @param queueName the name of the queue
     * @return the routing key
     */
    public static String all(String queueName) {
        return queueName + ".*";
    }

    /**
     * Returns a queue routing key for a specific event of a given service.
     *
     * @param queueName   the name of the queue
     * @param serviceName the name of the service sending the event
     * @return the routing key
     */
    public static String forService(String queueName, String serviceName) {
        return queueName + "." + serviceName;
    }
}

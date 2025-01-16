package shopsphere_notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    public static final String TEST_QUEUE = "test.notification.queue";

    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, false);
    }
}

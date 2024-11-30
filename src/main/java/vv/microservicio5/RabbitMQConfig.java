package vv.microservicio5;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue cameraQueue() {
        return new Queue("cameraQueue", true);
    }

    @Bean
    public Queue droneQueue() {
        return new Queue("droneQueue", true);
    }

    @Bean
    public Queue eventQueue() {
        return new Queue("eventQueue", true);
    }
}
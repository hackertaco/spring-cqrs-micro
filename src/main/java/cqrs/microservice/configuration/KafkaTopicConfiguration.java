package cqrs.microservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KafkaTopicConfiguration {
    @Value(value = "${kafka.bootstrapServers:localhost:9093}")
    private String bootStrapServers;

    private final KafkaConfigProperties kafkaConfigProperties;
    private final OrderKafkaTopicsConfiguration orderKafkaTopicsConfiguration;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        final Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createOrderTopicInitializer(KafkaAdmin kafkaAdmin) {
        try {
            final var topicName = orderKafkaTopicsConfiguration.getOrderCreatedTopic();
            final var topic = new NewTopic(topicName, 3, (short) 1);
            kafkaAdmin.createOrModifyTopics(topic);
            log.info("topic: {}", topic);
            return topic;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Bean
    public NewTopic updateStatusTopicInitializer(KafkaAdmin kafkaAdmin){
        try {
            final var topicName = orderKafkaTopicsConfiguration.getOrderStatusUpdatedTopic();
            final var topic = new NewTopic(topicName, 3, (short) 1);
            kafkaAdmin.createOrModifyTopics(topic);
            log.info("topic: {}", topic);
            return topic;
        } catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
    @Bean
    public NewTopic changeDeliveryAddressTopicInitializer(KafkaAdmin kafkaAdmin){
        try {
            final var topicName = orderKafkaTopicsConfiguration.getOrderAddressChangedTopic();
            final var topic = new NewTopic(topicName, 3, (short) 1);
            kafkaAdmin.createOrModifyTopics(topic);
            log.info("topic: {}", topic);
            return topic;
        } catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

}

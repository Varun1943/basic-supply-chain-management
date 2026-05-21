    package com.app.supply_chain.config;

    import org.apache.kafka.clients.admin.NewTopic;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
    import org.springframework.kafka.core.ConsumerFactory;

    @Configuration
    public class KafkaConfig {

        @Bean
        public NewTopic orderEventsTopic() {
            return new NewTopic("order-events-v4", 12, (short) 1);
        }
        @Bean
        public NewTopic orderIngestionTopic() {
            return new NewTopic("order-ingestion-v4", 12, (short) 1);
        }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(12);

        return factory;
    }
    }

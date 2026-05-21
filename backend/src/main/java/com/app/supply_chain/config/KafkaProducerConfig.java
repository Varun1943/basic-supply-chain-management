package com.app.supply_chain.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
 
// @Configuration
// public class KafkaProducerConfig {

//     @Bean
//     public Producer<String, byte[]> kafkaProducer() {

//         Properties props = new Properties();

//         props.put("bootstrap.servers", "localhost:9092");

        
//         props.put("acks", "0");
//         props.put("linger.ms", "5");
//         props.put("batch.size", "65536");
//         props.put("compression.type", "snappy");
//         props.put("buffer.memory", "67108864");
        
//         props.put("max.block.ms", "0");

//         props.put("key.serializer", StringSerializer.class.getName());
//         props.put("value.serializer", ByteArraySerializer.class.getName());

//         return new KafkaProducer<>(props);
//     }
// }
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 131072);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
package com.chriniko.kafka.example.config;

import com.chriniko.kafka.example.domain.Student;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfigStudentsJsonTopic {

    @Bean
    public ProducerFactory<String, Student> producerFactoryForStudentsJsonTopic() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Qualifier("studentsJsonTopic")
    @Bean
    public KafkaTemplate<String, Student> studentsJsonTopic() {
        return new KafkaTemplate<>(producerFactoryForStudentsJsonTopic());
    }

    @Bean
    public ConsumerFactory<String, Student> consumerFactoryForStudentsJson() {
        final Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1-studentsJson");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        final DefaultKafkaConsumerFactory<String, Student> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumerFactory.setValueDeserializer(new JsonDeserializer<>(Student.class));

        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Student> kafkaListenerContainerFactoryForStudentsJson() {
        final ConcurrentKafkaListenerContainerFactory<String, Student> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryForStudentsJson());
        factory.setConcurrency(4);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.BATCH);
        return factory;
    }
}

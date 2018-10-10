package com.chriniko.kafka.example.config;

import com.chriniko.kafka.example.domain.Person;
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
public class KafkaConfigPeopleTopic {

    @Bean
    public ProducerFactory<String, Person> producerFactory() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        final DefaultKafkaProducerFactory<String, Person> producerFactory = new DefaultKafkaProducerFactory<>(configProps);

        producerFactory.setKeySerializer(new StringSerializer());
        producerFactory.setValueSerializer(new JsonSerializer<Person>());

        return producerFactory;
    }

    @Qualifier("peopleTopic")
    @Bean
    public KafkaTemplate<String, Person> peopleTopic() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, Person> consumerFactory() {
        final Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1-people");

        final DefaultKafkaConsumerFactory<String, Person> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumerFactory.setKeyDeserializer(new StringDeserializer());
        consumerFactory.setValueDeserializer(new JsonDeserializer<>(Person.class));

        return consumerFactory;
    }

    //TODO use it if you like to monitor the topic
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Person> kafkaListenerContainerFactoryForStudentsJson() {
        final ConcurrentKafkaListenerContainerFactory<String, Person> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(4);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.BATCH);
        return factory;
    }

}

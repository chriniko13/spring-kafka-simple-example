package com.chriniko.kafka.example.service.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class RandomIdGenerator {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public RandomIdGenerator(@Qualifier("studentsTopic") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void execute() {
        IntStream.rangeClosed(1, 3)
                .forEach(idx -> {
                    kafkaTemplate
                            .send("students", UUID.randomUUID().toString())
                            .addCallback(result -> {
                                System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                                System.out.println("Produced Record: " + result.getProducerRecord().toString());
                                System.out.println("Record Metadata: " + result.getRecordMetadata().toString());
                            }, throwable -> {
                                System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                                System.out.println("Throwable: " + throwable);
                            });
                });
    }

}

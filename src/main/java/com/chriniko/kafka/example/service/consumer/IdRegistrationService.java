package com.chriniko.kafka.example.service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IdRegistrationService {

    private final List<String> ids = Collections.synchronizedList(new LinkedList<>());

    @KafkaListener(topics = "students", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload String id, @Headers Map<String, Object> headers) {

        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("{threadName: " + Thread.currentThread().getName() + "}Received id: " + id);
        System.out.println("{headers: " + headers + "}");

        ids.add(id);

        synchronized (ids) {
            String collectedIds = ids.stream().collect(Collectors.joining(","));
            System.out.println("collectedIds: " + collectedIds);
        }

    }


}

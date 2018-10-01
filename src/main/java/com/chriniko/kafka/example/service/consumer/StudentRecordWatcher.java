package com.chriniko.kafka.example.service.consumer;

import com.chriniko.kafka.example.domain.Student;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class StudentRecordWatcher {

    private final ConcurrentMap<String, Student> studentsDb = new ConcurrentHashMap<>();

    @KafkaListener(topics = "studentsJson",
            groupId = "group1-studentsJson",
            containerFactory = "kafkaListenerContainerFactoryForStudentsJson")
    public void greetingListener(Student student) {

        System.out.println("\n{threadName: " + Thread.currentThread().getName() + "}>>>Received Student: " + student);

        studentsDb.put(student.getId(), student);

        studentsDb.entrySet().forEach(entry -> System.out.println("Student: " + entry));
    }

}

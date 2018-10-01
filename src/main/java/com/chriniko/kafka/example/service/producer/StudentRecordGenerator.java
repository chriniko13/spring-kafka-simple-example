package com.chriniko.kafka.example.service.producer;

import com.chriniko.kafka.example.domain.Student;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
public class StudentRecordGenerator {

    private final KafkaTemplate<String, Student> kafkaTemplate;
    private final ExecutorService workersPool;

    @Autowired
    public StudentRecordGenerator(@Qualifier("studentsJsonTopic") KafkaTemplate<String, Student> kafkaTemplate,
                                  ExecutorService workersPool) {
        this.kafkaTemplate = kafkaTemplate;
        this.workersPool = workersPool;
    }

    public void execute() {

        for (int i = 1; i <= 3; i++) {

            final int idx = i;

            Future<RecordMetadata> futureResult = kafkaTemplate
                    .execute((KafkaOperations.ProducerCallback<String, Student, Future<RecordMetadata>>) producer -> {

                        final Student student = new Student();
                        student.setFirstname("firstname " + idx);
                        student.setId(idx + "");
                        student.setSurname("surname " + idx);
                        student.setInitials("initials " + idx);

                        final ProducerRecord<String, Student> record = new ProducerRecord<>("studentsJson", student.getId(), student);

                        return producer.send(record);
                    });

            ListenableFuture<RecordMetadata> listenableFuture = JdkFutureAdapters.listenInPoolThread(futureResult);

            listenableFuture.addListener(() -> System.out.println("Student record send to topic: studentsJson"), workersPool);
        }
    }
}

package com.chriniko.kafka.example;

import com.chriniko.kafka.example.service.producer.RandomIdGenerator;
import com.chriniko.kafka.example.service.producer.StudentRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Autowired
    private RandomIdGenerator randomIdGenerator;

    @Autowired
    private StudentRecordGenerator studentRecordGenerator;

    @Override
    public void run(String... args) {

        // Note: Sample scenario --- CHOOSE ONE EXAMPLE TO SEE DEMONSTRATION

        //randomIdGenerator.execute(); // Note: Comment-Uncomment

        studentRecordGenerator.execute(); // Note: Comment-Uncomment
    }
}

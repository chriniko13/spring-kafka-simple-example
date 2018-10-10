package com.chriniko.kafka.example;

import com.chriniko.kafka.example.service.StreamExample1;
import com.chriniko.kafka.example.service.producer.RandomIdGenerator;
import com.chriniko.kafka.example.service.producer.StudentRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Autowired
    private RandomIdGenerator randomIdGenerator;

    @Autowired
    private StudentRecordGenerator studentRecordGenerator;

    @Autowired
    private StreamExample1 streamExample1;

    @Override
    public void run(String... args) throws Exception {

        // Note: Sample scenario --- CHOOSE ONE EXAMPLE TO SEE DEMONSTRATION

        //randomIdGenerator.execute(); // Note: Comment-Uncomment

        //studentRecordGenerator.execute(); // Note: Comment-Uncomment

        streamExample1.run(); // Note: Comment-Uncomment
    }
}

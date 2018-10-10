package com.chriniko.kafka.example.service;

import com.chriniko.kafka.example.domain.Person;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class StreamExample1 {

    private static final String PEOPLE_TOPIC = "people";
    private static final String PEOPLE_GROUPED_BY_AGES_COUNT = "people-ages-count";

    private ExecutorService executorService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Person> kafkaTemplate;
    private KafkaStreams streams;

    @Autowired
    public StreamExample1(ObjectMapper objectMapper,
                          @Qualifier("peopleTopic") KafkaTemplate<String, Person> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    void init() {
        executorService = Executors.newFixedThreadPool(2);
    }

    @PreDestroy
    void clear() {
        executorService.shutdownNow();
        streams.close();
    }


    public void run() throws Exception {

        executorService.submit(topicTrafficProducerTask());

        // create stream which listens on this X topic and do computations...
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "people-streams-example");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/home/nikolai/streams-example");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Person> source = builder.stream(
                PEOPLE_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Person.class, objectMapper)));

        source.foreach((key, value) -> System.out.println("\n ~~~" + key + " --- " + value.getName()));

//        KTable<Integer, Long> peopleGroupedByAgeCount = source
//                .groupBy((key, value) -> value.getAge())
//                .count();


        // need to override value serde to Long type
//        peopleGroupedByAgeCount
//                .toStream()
//                .to(PEOPLE_GROUPED_BY_AGES_COUNT, Produced.with(Serdes.Integer(), Serdes.Long()));


        streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("KafkaStreams#Error: " + e);
            }
        });
        streams.cleanUp();
        streams.start();


        TimeUnit.SECONDS.sleep(60);
    }

    private Runnable topicTrafficProducerTask() {
        return () -> {
            try {
                String loadedJson = Files
                        .readAllLines(
                                Paths.get(this.getClass().getClassLoader().getResource("people.json").toURI()))
                        .stream()
                        .collect(Collectors.joining());

                List<Person> people = objectMapper.readValue(loadedJson, new TypeReference<List<Person>>() {
                });

                System.out.println("people.size() == " + people.size());

                final Random random = new Random();

                //int reps = 0;

                for (Person person : people) {

                    //if (reps++ == 50) break;

                    kafkaTemplate
                            .send(PEOPLE_TOPIC, person.get_id(), person)
                            .addCallback(
                                    result -> {
                                        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                                        System.out.println("StreamExample1#run: success -> " + result.toString());
                                    }, error -> {
                                        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                                        System.out.println("StreamExample1#run: error -> " + error);
                                    });

                    TimeUnit.MILLISECONDS.sleep((random.nextInt(5) + 1) * 100);
                }

            } catch (Exception error) {
                System.out.println("topicTrafficProducerTask---error: " + error);
            }

        };
    }

}

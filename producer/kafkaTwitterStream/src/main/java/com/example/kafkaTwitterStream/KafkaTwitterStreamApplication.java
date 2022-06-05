package com.example.kafkaTwitterStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class KafkaTwitterStreamApplication {

    private  static final String consumer_key = "fxNXwf6gEPlC76i0fX4Flg6BP";
    private  static final String consumer_secret = "wOd2jpUIn6JtjWfwh5yjCuzAy8Lw58ntRACXmSFxFGyVFNd2Gg";
    private  static final String access_token_key = "1470772200189927433-gQVgFMXWpE1q8iNKFtfYliCmI1cVZI";
    private  static final String access_token_secret = "IbQ8a0HLoXPXE8jJ2DNoydPzR28jAmclxJ6YMQsknmji7";
    final static LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000);
    private static final String[] searchWords = "coronar, covid".split(",");
    public static void setUp() throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumer_key)
                .setOAuthConsumerSecret(consumer_secret)
                .setOAuthAccessToken(access_token_key)
                .setOAuthAccessTokenSecret(access_token_secret);


        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStatus(Status status) {
                queue.offer(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }
        };

        // Create twitter stream using the configuration
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(listener);


        FilterQuery query = new FilterQuery(searchWords);
        query.language("en");
        twitterStream.filter(query);
        configKafkaProducer(queue, "tweets");
    }

    private static void configKafkaProducer(LinkedBlockingQueue<Status> q, String topic) throws Exception {
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        int j = 0;
        String msg = null;
        while (true) {
            Status ret = q.poll();
            if (ret == null) {
                Thread.sleep(10000);
            }
            else {
                String location = "";
                if (ret.getUser().getLocation() != null && ret.getHashtagEntities().length > 0) {
                    String tweet = ret.getText();
                    location = ret.getUser().getLocation();
                    System.out.println("Tweet:" + tweet);
                    System.out.println("Location: " + location);
                    // Use /TLOC/ as a separator pattern
                    msg = location + " /TLOC/ " + tweet;
                }
                producer.send(new ProducerRecord<String, String>(topic, Integer.toString(j++), msg));
            }
            Thread.sleep(10000);
        }
    }

    public static Properties properties() {
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

	public static void main(String[] args) throws Exception {
        setUp();
//		SpringApplication.run(KafkaTwitterStreamApplication.class, args);
	}

}

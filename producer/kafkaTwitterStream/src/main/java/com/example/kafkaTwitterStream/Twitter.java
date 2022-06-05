package com.example.kafkaTwitterStream;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Twitter {
    private  static final String consumer_key = "fxNXwf6gEPlC76i0fX4Flg6BP";
    private  static final String consumer_secret = "wOd2jpUIn6JtjWfwh5yjCuzAy8Lw58ntRACXmSFxFGyVFNd2Gg";
    private  static final String access_token_key = "1470772200189927433-gQVgFMXWpE1q8iNKFtfYliCmI1cVZI";
    private  static final String access_token_secret = "IbQ8a0HLoXPXE8jJ2DNoydPzR28jAmclxJ6YMQsknmji7";
    final static LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(10);

    public static void main(String[] args) throws TwitterException, InterruptedException {

        BuildTwitter buildTwitter = new BuildTwitter();
        TwitterStream twitterStream = buildTwitter.createBuild();
        ProducerKafka producerKafka = new ProducerKafka();

        Producer<String, String> producer = producerKafka.producer();

        StatusListener listener = new StatusListener() {
            @Override
            public void onException(Exception ex) {

            }

            @Override
            public void onStatus(Status status) {
                queue.offer(status);
                String msg = null;
                String tweet = status.getText();
                String location = status.getUser().getLocation();
                List<HashtagEntity> hahTag = List.of(status.getHashtagEntities());
                msg = location + " /TLOC/ " + tweet;
                System.out.println("---------------------------------------------------------");
                System.out.println(msg);

                try {
                    Thread.sleep(2000);
//                    if (status.getUser().getLocation() != null && status.getHashtagEntities().length > 0) {
                        producer.send(new ProducerRecord<String, String>("tweets", msg));
//                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }
        };
        FilterQuery fq = new FilterQuery();
        String keywords[] = {"#"};
        fq.track(keywords);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);

        Thread.sleep(500000);


//
//        int i = 0;
//        int j = 0;
//        while(i < 0) {
//            Status ret = queue.poll();
//            if (ret == null) {
//                Thread.sleep(10000);
//                i++;
//            } else {
//                for (HashtagEntity hashtagEntity: ret.getHashtagEntities()) {
//                    System.out.println("Hashtag: " + hashtagEntity.getText());
//                    producer.send(new ProducerRecord<String, String>("tweets", Integer.toString(j++), hashtagEntity.getText()));
//                }
//            }
//        }
        Thread.sleep(5000);
        twitterStream.shutdown();
    }
}

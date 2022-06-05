package com.sentiment.worker.stream;


import com.sentiment.worker.amazonService.AmazonDetecSentiment;
import com.sentiment.worker.configs.TweetStreams;
import com.sentiment.worker.models.Tweet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import com.sentiment.worker.amazonService.AmazonDetecSentiment.*;

@Slf4j
@Component
public class
TweetProcessor {

    TweetStreams tweetStreams;

    AmazonDetecSentiment amazonDetecSentiment;

    public TweetProcessor(TweetStreams tweetStreams) {
        this.tweetStreams = tweetStreams;
    }

    @StreamListener(TweetStreams.INPUT)
    public void process(@Payload Tweet t) throws ParseException {
        log.info("Received results from kafka: {}", t);

        /**
         * sentiments = ["Mixed": 0.003, "Negative": 1.782, "Positive": 0.033, "Neutral": 1.782]
         */
        HashMap<String, Float> sentiments = amazonDetecSentiment.detectSentiments(t.getTweet64());

        sentiments.forEach((k, v) -> {
            // k: "Neutral", v:3.804
            log.info("Sending sentiment result to kafka for key " + k + " and value " + v);
            MessageChannel messageChannel = tweetStreams.outboundSentimentEvents();
            try {
                messageChannel.send(MessageBuilder
                        .withPayload(v)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, parseTwitterDateTime(t.getTimestamp()).toString() + "-" + k)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .build());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private Long parseTwitterDateTime(String twitterDateTime) throws ParseException {
        final String TWITTER = "EEE MMM dd HH:mm:ss ZZZ yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TWITTER);
        LocalDateTime dateTime = LocalDateTime.parse(twitterDateTime, formatter);
        ZonedDateTime zdt = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        return zdt.toInstant().truncatedTo(ChronoUnit.HOURS).toEpochMilli();
    }

}

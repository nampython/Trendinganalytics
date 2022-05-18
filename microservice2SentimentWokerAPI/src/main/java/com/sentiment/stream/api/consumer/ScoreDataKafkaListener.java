package com.sentiment.stream.api.consumer;

import com.sentiment.stream.api.mongodb.collection.SentimentScore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class ScoreDataKafkaListener {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * received key 1651348800000-Negative and value 518.029
     * @param scoreData
     * @param key
     */
    @KafkaListener(topics = "aggregated-score")
    public void listen(@Payload String scoreData, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {

        log.info("received key {} and value {}", key, scoreData);
        addScoresToCollection(key, scoreData);
    }

    /**
     *
     * @param key: 1651348800000-Neutral
     * @param scoreData: 1116.3359
     */
    private void addScoresToCollection(String key, String scoreData) {
        // keySplitter = ["1651348800000", "Neutral"]
        String[] keySplitter = key.split("-");

        // date: 2022-05-01T03:00
        LocalDateTime date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(keySplitter[0])), ZoneId.systemDefault());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(key));
        Update update = new Update();
        update.set("local_date_time", date);
        update.set("sentiment_type", keySplitter[1]);
        update.set("score", Float.parseFloat(scoreData));

        mongoTemplate.upsert(query, update, SentimentScore.class);
    }
}

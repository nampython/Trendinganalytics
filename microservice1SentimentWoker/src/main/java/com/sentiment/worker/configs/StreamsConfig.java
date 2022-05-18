package com.sentiment.worker.configs;

import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * reference: <a href="https://docs.spring.io/spring-cloud-stream/docs/1.0.0.RC2/reference/html/_programming_model.html"></a>
 */
@EnableBinding(TweetStreams.class)
public class StreamsConfig {
}

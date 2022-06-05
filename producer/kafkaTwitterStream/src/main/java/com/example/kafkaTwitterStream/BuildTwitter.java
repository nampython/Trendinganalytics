package com.example.kafkaTwitterStream;


import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;



public class BuildTwitter {
    private  static final String consumer_key = "fxNXwf6gEPlC76i0fX4Flg6BP";
    private  static final String consumer_secret = "wOd2jpUIn6JtjWfwh5yjCuzAy8Lw58ntRACXmSFxFGyVFNd2Gg";
    private  static final String access_token_key = "1470772200189927433-gQVgFMXWpE1q8iNKFtfYliCmI1cVZI";
    private  static final String access_token_secret = "IbQ8a0HLoXPXE8jJ2DNoydPzR28jAmclxJ6YMQsknmji7";

    public TwitterStream createBuild() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumer_key)
                .setOAuthConsumerSecret(consumer_secret)
                .setOAuthAccessToken(access_token_key)
                .setOAuthAccessTokenSecret(access_token_secret);
        return new TwitterStreamFactory(cb.build()).getInstance();
    }
}

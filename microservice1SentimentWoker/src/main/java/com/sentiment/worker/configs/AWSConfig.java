package com.sentiment.worker.configs;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    /**
     * Configure for AWS Comprehend
     * @return
     */
    @Bean
    public AmazonComprehend amazonComprehend() {
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
        AmazonComprehend comprehendClient = AmazonComprehendClientBuilder.standard()
                .withCredentials(awsCreds)
                .withRegion(Regions.EU_WEST_1)
                .build();
        return comprehendClient;
    }
}

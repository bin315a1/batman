package com.batman.server.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSQSConfig {
    @Bean
    public AmazonSQS amazonSimpleQueueService() {
        return AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_WEST_1)
                .build();
    }
}

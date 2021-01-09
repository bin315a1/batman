package com.batman.db.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {
    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
//    @Value("${environment}")
//    private String environment;
    @Value("${region}")
    private String region;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        final AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setSignerRegionOverride(Regions.fromName(region).getName());
        if (amazonDynamoDBEndpoint != null && !amazonDynamoDBEndpoint.isEmpty()) {
            client.setEndpoint(amazonDynamoDBEndpoint);
        }

        return new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
    }
    @Bean
    public DynamoDB dynamoDB() {
        final AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setSignerRegionOverride(Regions.fromName(region).getName());
        if (amazonDynamoDBEndpoint != null && !amazonDynamoDBEndpoint.isEmpty()) {
            client.setEndpoint(amazonDynamoDBEndpoint);
        }

        return new DynamoDB(client);
    }
}

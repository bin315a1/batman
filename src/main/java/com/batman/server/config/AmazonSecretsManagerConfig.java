package com.batman.server.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerAsyncClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Component
public class AmazonSecretsManagerConfig {
    // keys in map
    private final static String JWT_SECRET = "security.jwt.secret";

    @Value("${aws.secret.manager.secretName}")
    private String secretName;

    private String getSecret() throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, NullPointerException {
        AWSSecretsManager client = AWSSecretsManagerAsyncClientBuilder.standard()
                .withRegion(Regions.US_WEST_1)
                .build();

        String secret = "";
        ByteBuffer binarySecretData;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName).withVersionStage("AWSCURRENT");
        GetSecretValueResult getSecretValueResult = null;

        getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        secret = getSecretValueResult.getSecretString();

        return secret;
    }

    @Bean
    public RuntimeProperties customPropertySource() {
        String secretJson = getSecret();
        try {
            Map<String, Object> secretMap = new ObjectMapper().readValue(secretJson, HashMap.class);
            return RuntimeProperties.builder()
                    .JWTSecret((String)secretMap.get(JWT_SECRET))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping Json");
        }
    }
}

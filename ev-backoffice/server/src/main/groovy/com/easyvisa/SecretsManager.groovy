package com.easyvisa

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.logs.model.ResourceNotFoundException
import com.amazonaws.services.secretsmanager.AWSSecretsManager
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult
import com.amazonaws.services.secretsmanager.model.InvalidParameterException
import com.amazonaws.services.secretsmanager.model.InvalidRequestException
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.fasterxml.jackson.core.JsonProcessingException
import groovy.json.JsonSlurper
import org.springframework.core.env.MapPropertySource
import groovy.util.logging.Slf4j

@Slf4j
class SecretsManager {

    static MapPropertySource addAllSecretValueAsSource(String accessKey, String secretKey, String secret, String region) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey)
        AWSSecretsManager awsSecretsManager = AWSSecretsManagerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region)
                .build()
        new MapPropertySource("aws-ssm", fetchAllSecretValuePairs(awsSecretsManager, secret))
    }

    static Map<String, Object> fetchAllSecretValuePairs(AWSSecretsManager awsSecretsManager, String secret) {
        Map<String, Object> props = new HashMap<>()
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secret)
        GetSecretValueResult getSecretValueResponse = null
        try {
            getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest)
        } catch (ResourceNotFoundException e) {
            log.error("The requested secret ${secret} was not found")
        } catch (InvalidRequestException e) {
            log.error("The request was invalid due to: ${e.getMessage()}")
        } catch (InvalidParameterException e) {
            log.error("The request had invalid params: ${e.getMessage()}")
        }

        if (getSecretValueResponse != null) {
            try {
                String s = getSecretValueResponse.getSecretString()
                JsonSlurper parser = new JsonSlurper()
                Map result1 = (Map) parser.parseText(s)
                for (Map.Entry entry : result1.entrySet()) {
                    String key = (String) entry.getKey()
                    String value = (String) entry.getValue()
                    props.put(key, value)
                }
            } catch (JsonProcessingException e) {
                log.error("JsonProcessing error for secret string parse: ${e.getMessage()}")
            }
        }
        return props
    }
}

package com.easyvisa

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

@ComponentScan(['com.easyvisa.questionnaire.config',
        'com.easyvisa.questionnaire.repositories',
        'com.easyvisa.questionnaire.services.rule.impl',
        'com.easyvisa.questionnaire.services'])
class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    void setupAwsSsmIfConfigured(Environment environment) {
        Boolean isSsmEnabled = environment.getProperty('aws.ssmEnabled', Boolean)
        String region = environment.getProperty('aws.region')
        String secret = environment.getProperty('aws.secretName')
        String accessKey = environment.getProperty('aws.accessKey')
        String secretKey = environment.getProperty('aws.secretKey')
        if (isSsmEnabled) {
            log.info "Found AWS SSM Configuration, Configuring AWS secrets as a config source."
            def mappedPropertySource = SecretsManager.addAllSecretValueAsSource(accessKey, secretKey, secret, region)
            environment.propertySources.addFirst(mappedPropertySource) //Set AWS configuration as First lookup
        } else {
            //TODO: fix missed properties
//            log.info "AWS SSM Disabled"
        }

    }

    @Override
    void setEnvironment(Environment environment) {
        String configPath = System.properties['local.config.location']
        if (configPath) {
            Resource resourceConfig = new FileSystemResource(configPath)
            if (resourceConfig.exists()) {
                YamlPropertiesFactoryBean ypfb = new YamlPropertiesFactoryBean()
                ypfb.setResources([resourceConfig] as Resource[])
                ypfb.afterPropertiesSet()
                Properties properties = ypfb.object
                environment.propertySources.addFirst(new PropertiesPropertySource('local.config.location', properties))
            }
            setupAwsSsmIfConfigured(environment)
        }
    }
}

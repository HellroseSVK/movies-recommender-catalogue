package org.movierc.feedbackservice.configuration;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtemisConfig {
    @Bean(destroyMethod = "close")
    public ServerLocator serverLocator(ArtemisProperties props) throws Exception {
        return ActiveMQClient.createServerLocator(props.getBrokerUrl());
    }

    @Bean
    public ClientSessionFactory clientSessionFactory(ServerLocator locator) throws Exception {
        return locator.createSessionFactory();
    }
}

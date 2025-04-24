package org.movierc.feedbackservice;

import org.movierc.shared.config.SharedConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SharedConfig.class})
@EnableConfigurationProperties(ArtemisProperties.class)
public class FeedbackServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(FeedbackServiceApplication.class, args);
  }

}

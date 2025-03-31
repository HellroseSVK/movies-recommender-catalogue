package org.movierc.feedbackservice;

import org.movierc.shared.config.SharedConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SharedConfig.class})
public class FeedbackServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(FeedbackServiceApplication.class, args);
  }

}

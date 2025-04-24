package org.movierc.recommendationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("without-artemis")
class RecommendationServiceApplicationTests {

  @Test
  void contextLoads() {
  }

}

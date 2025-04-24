package org.movierc.feedbackservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("without-artemis")
class FeedbackServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}

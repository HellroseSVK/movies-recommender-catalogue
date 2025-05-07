package org.movierc.oauth2.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring MVC Controller.
 * Handles HTTP requests by preparing data in model and passing it to Thymeleaf HTML templates.
 */
@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Home page accessible even to non-authenticated users. Displays user personal data.
     */
    @GetMapping("/")
    public String index(
            Model model,
            @AuthenticationPrincipal OidcUser user,
            OAuth2AuthenticationToken authentication
    ) {
        log.debug("********************************************************");
        log.debug("* index() called                                       *");
        log.debug("********************************************************");
        log.debug("user {}", user == null ? "is anonymous" : user.getSubject());

        // put obtained user data into a model attribute named "user"
        model.addAttribute("user", user);

        // put issuer name into a model attribute named "issuerName"
        if (user != null) {
            model.addAttribute("issuerName",
                    "https://id.muni.cz/oidc/".equals(user.getIssuer().toString()) ? "MUNI" : "Google");

            String clientRegistrationId = authentication.getAuthorizedClientRegistrationId();

            // Retrieve the authorized client using the client ID and username (principal name)
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    clientRegistrationId,
                    authentication.getName()
            );

            // Extract the access token
            OAuth2AccessToken accessToken = client.getAccessToken();
            model.addAttribute("accessToken", accessToken.getTokenValue());
        }

        // return the name of a Thymeleaf HTML template that
        // will be searched in src/main/resources/templates with .html suffix
        return "index";
    }
}
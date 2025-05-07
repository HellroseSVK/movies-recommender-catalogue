package org.movierc.shared;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerOauth2RedirectController {

    @GetMapping("/login/oauth2/code/muni")
    public String redirectToSwagger(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String redirectUrl = "/swagger-ui/oauth2-redirect.html";
        if (queryString != null) {
            redirectUrl += "?" + queryString;
        }
        return "redirect:" + redirectUrl;
    }
}
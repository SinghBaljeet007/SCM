package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(OAuthSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
                logger.info("Inside OAuthSuccessHandler");

                // Identify the provider type google, github, facebook etc.

                var oAuth2AuthenticationToken  = (OAuth2AuthenticationToken)authentication;

                String clientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

                logger.info(clientRegistrationId);

                var oauth2user = (DefaultOAuth2User)authentication.getPrincipal();

                oauth2user.getAttributes().forEach((key, value) -> {
                        logger.info("{}: {}", key, value);
                });

                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setRoleList(List.of(AppConstants.ROLE_USER));
                user.setEmailVerified(true);
                user.setEnabled(true);

                if(clientRegistrationId.equalsIgnoreCase("google")) {
                    // google
                    // google attributes
                    user.setEmail(oauth2user.getAttribute("email"));
                    user.setPassword("google_password");
                    user.setProfilePic(oauth2user.getAttribute("picture"));
                    user.setName(oauth2user.getAttribute("name"));
                    user.setAbout("This account is created using google...");
                    user.setProviderUserId(oauth2user.getName());
                    user.setProvider(Providers.GOOGLE);
                } 

                else if(clientRegistrationId.equalsIgnoreCase("github")) {
                    // github 
                    // github attributes
                    String email = oauth2user.getAttribute("email") != null ? 
                    oauth2user.getAttribute("email") : 
                    oauth2user.getAttribute("login") + "@github.com";

                    user.setEmail(email);
                    user.setPassword("github_password");
                    user.setProfilePic(oauth2user.getAttribute("avatar_url"));
                    user.setName(oauth2user.getAttribute("login"));
                    user.setAbout("This account is created using github...");
                    user.setProvider(Providers.GITHUB);
                    user.setProviderUserId(oauth2user.getName());
                } 

                else if(clientRegistrationId.equalsIgnoreCase("linkedin")) {
                    // linkedin
                    // linkedin attributes
                } 

                else {
                    // let say: facebook
                    logger.info("{}: Unknown Provider", clientRegistrationId);
                }

                /*
                 *
                    DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

                    // logger.info(user.getName());
                    
                    // user.getAttributes().forEach((key, value) -> {
                    //     logger.info("{}: {}", key, value);
                    // });

                    // logger.info(user.getAuthorities().toString());

                    // save data in DB before redirect

                    String email = user.getAttribute("email");
                    String name = user.getAttribute("name");
                    String picture = user.getAttribute("picture");

                    // Create a user and save in DB

                    User user1 = new User();
                    user1.setEmail(email);
                    user1.setName(name);
                    user1.setProfilePic(picture);
                    user1.setPassword("google_password");
                    user1.setId(UUID.randomUUID().toString());
                    user1.setProvider(Providers.GOOGLE);
                    user1.setEnabled(true);
                    user1.setEmailVerified(true);

                    user1.setProviderUserId(user.getName());
                    user1.setRoleList(List.of(AppConstants.ROLE_USER));
                    user1.setAbout("This account is created using google...");
                 * 
                */

                // save user
                User user2 = userRepository.findByEmail(user.getEmail()).orElse(null);

                    if(Objects.isNull(user2)) {
                        userRepository.save(user);
                        logger.info("User Saved: {}", user.getEmail());
                    }

                new DefaultRedirectStrategy().sendRedirect(request, response, "/user/dashboard");
    }



}

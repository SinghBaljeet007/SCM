package com.scm.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.UserHelper;
import com.scm.services.UserService;

@ControllerAdvice
public class RootController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void addLoggedInUserInfoToAllUserPages(Model model, Authentication authentication) {

        if(Objects.isNull(authentication))
            return;

        String username = UserHelper.getEmailOfLoggedInUser(authentication);

        // Fetch user from db through username
        User user = userService.getUserByEmail(username);

        logger.info("User info: name => {}, email => {}", user.getName(), user.getEmail());

        model.addAttribute("loggedInUser", user);
    }

}

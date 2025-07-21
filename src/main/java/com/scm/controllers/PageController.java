package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class PageController {

    @Autowired
    private UserService userService;   // Ideally we should use constructor injection instead of property injection

    @RequestMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String homePage(Model model) {
        System.out.println("Home page handler");

        // sending below data to view on page with the help of thymleaf in html template
        model.addAttribute("name", "Smart Contact Manager Test");
        model.addAttribute("admin", "Baljeet Singh");
        model.addAttribute("githubRepo", "https://github.com/SinghBaljeet007");
        return "home";
    }

    // about

    @RequestMapping("/about")
    public String aboutPage() {
        System.out.println("About page loading");
        return "about";
    }
    

    //services
    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("Services page loading");
        return "services";
    }

    @RequestMapping("/contact")
    public String contactPage() {
        System.out.println("Contact page loading");
        return "contact";
    }

    @RequestMapping("/login")
    public String loginPage() {
        System.out.println("Login page loading");
        return new String("login");
    }

    @RequestMapping("/signup")
    public String registerPage(Model model) {
        System.out.println("Register page loading");

        UserForm userForm = new UserForm();
        // userForm.setName("Ballu");

        model.addAttribute("userForm", userForm);

        return "register";
    }

    @RequestMapping(value="/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult, HttpSession session) {
        System.out.println("Processing Registration");

        // Fetch form data
        System.out.println(userForm);

        // validate
        if(rBindingResult.hasErrors()) {
            return "register";
        }

        // save

        // User user = User.builder()
        // .name(userForm.getName())
        // .mobile(userForm.getMobile())
        // .email(userForm.getEmail())
        // .password(userForm.getPassword())
        // .about(userForm.getAbout())
        // .profilePic("https://fastly.picsum.photos/id/82/200/300.jpg?hmac=hfuNcoCWsYuVOmlcRdKAieM4Ax03DjM-mpVlqRUdGfc")
        // .build();

        // Commented the above object creation with builder becoz the default values for enum are not getting saved in DB.

        User user = new User();
        user.setName(userForm.getName());
        user.setMobile(userForm.getMobile());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setProfilePic("https://fastly.picsum.photos/id/82/200/300.jpg?hmac=hfuNcoCWsYuVOmlcRdKAieM4Ax03DjM-mpVlqRUdGfc");

        User savedUser = userService.saveUser(user);

        System.out.println(savedUser.toString());
        // message = "Registration Successful"
        // Add message
        Message message = Message.builder().content("Registration Successful. You can login now.").type(MessageType.green).build();
        session.setAttribute("message", message);

        // Redirect to login page
        return "redirect:/signup";
    }

    // @RequestMapping("/do-logout")
    // public String processLogout() {
    //     return "";
    // }
}

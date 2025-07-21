package com.scm.controllers;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.helpers.UserHelper;
import com.scm.mappers.ContactMapper;
import com.scm.services.ContactService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private UserService userService;
    
    // constructor-based injection
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // contact page
    @RequestMapping("/add")
    public String addContactView(Model model) {

        ContactForm contactForm = new ContactForm();

        // contactForm.setName("Ballu Bhaiya");
        // contactForm.setFavorite(true);

        model.addAttribute("contactForm", contactForm);

        return "/user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result, Authentication authentication, HttpSession session) {

        System.out.println(contactForm);

        // valid form data
        if(result.hasErrors()) {

            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", 
            Message.builder()
            .content("Please correct the following errors.")
            .type(MessageType.red)
            .build());

            return "/user/add_contact";
        }

        // Process form data

        Contact contact = new Contact();

        contact = contactMapper.toContact(contactForm, authentication);

        contactService.save(contact);

        // set message after submit contact form
        session.setAttribute("message", 
            Message.builder()
            .content("Your contact has been saved successfully.")
            .type(MessageType.green)
            .build());

        return "redirect:/user/contacts/add";
    }

    @RequestMapping
    public String viewContacts(
        @RequestParam(value = "page", defaultValue = AppConstants.PAGE_NUMBER) int page,
        @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE) int size, 
        @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
        @RequestParam(value = "orderBy", defaultValue = AppConstants.ORDER_BY) String direction,
        Model model, Authentication authentication) {

        // load all the user contacts
        String username = UserHelper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact =  contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "/user/contacts";
    }

    // search handler

    @RequestMapping("/search")
    public String searchContact(
        @ModelAttribute ContactSearchForm contactSearchForm,
        @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE) int size,
        @RequestParam(value = "page", defaultValue = AppConstants.PAGE_NUMBER) int page,
        @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
        @RequestParam(value = "orderBy", defaultValue = AppConstants.ORDER_BY) String orderBy,
        Model model, Authentication authentication) {

            String field = contactSearchForm.getField();
            String value = contactSearchForm.getKeyword();

            logger.info("field: {}, keyword: {}", field, value);

            var user = userService.getUserByEmail(UserHelper.getEmailOfLoggedInUser(authentication));

            Page<Contact> pageContact = null;

            if(field.equalsIgnoreCase("name")) {
                pageContact = contactService.searchByName(value, size, page, sortBy, orderBy, user);
            } else if(field.equalsIgnoreCase("email")) {
                pageContact = contactService.searchByEmail(value, size, page, sortBy, orderBy, user);
            } else if(field.equalsIgnoreCase("mobile")) {
                pageContact = contactService.searchByMobile(value, size, page, sortBy, orderBy, user);
            }

            logger.info("pageContact: {}", pageContact);
            
            model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
            model.addAttribute("pageContact", pageContact);
            model.addAttribute("contactSearchForm", contactSearchForm);


        return "user/search";
    }

    @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session) {

        contactService.delete(contactId);
        logger.info("contactId {} deleted successfully", contactId);

        session.setAttribute("message", 
            Message.builder()
            .content("Your contact has been deleted successfully.")
            .type(MessageType.blue)
            .build());

        return "redirect:/user/contacts";
    }

    @GetMapping("/edit/{contactId}")
    public String editContactView(@PathVariable String contactId, Model model) {
        
        var contact = contactService.getById(contactId);

        ContactForm contactForm = new ContactForm();

        contactForm  = contactMapper.toContactForm(contact);

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);

        return "/user/edit_contact";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId, @Valid  @ModelAttribute ContactForm contactForm, 
     BindingResult result, HttpSession session, Authentication authentication) {

        logger.info("Binding Result: {}", result);

        if(result.hasErrors()) {
            return "/user/edit_contact";
        }

        Contact contact = contactService.getById(contactId);
        var imagePublicIdOld = contact.getCloudinaryImagePublicId();
        var pictureOld = contact.getPicture();

        contact = contactMapper.toContact(contactForm, authentication);
        contact.setId(contactId);
        contactForm.setPicture(contact.getPicture());

        if(StringUtils.isNotBlank(imagePublicIdOld) && StringUtils.isNotBlank(pictureOld) && StringUtils.isBlank(contact.getCloudinaryImagePublicId()) && StringUtils.isBlank(contact.getPicture())) {
            contact.setCloudinaryImagePublicId(imagePublicIdOld);
            contact.setPicture(pictureOld);
        }

        var updatedContact = contactService.update(contact);

        logger.info("Updated contact: {}", updatedContact);

        session.setAttribute("message", 
        Message.builder()
        .content("Contact Updated")
        .type(MessageType.green)
        .build());

        return "redirect:/user/contacts";
    }
}

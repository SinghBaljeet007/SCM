package com.scm.mappers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helpers.UserHelper;
import com.scm.services.ImageService;
import com.scm.services.UserService;

@Component
public class ContactMapper {

    private Logger logger = LoggerFactory.getLogger(ContactMapper.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    UserService userService;

    // Method to map ContactForm to Contact
    public Contact toContact(ContactForm contactForm, Authentication authentication) {

        Contact contact = new Contact();

        // set user
        String username = UserHelper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        contact.setUser(user);

        // process image
        // logger.info("File info: {}", contactForm.getContactImage().getOriginalFilename());

        // String filename = UUID.randomUUID().toString();

        // String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            contact.setCloudinaryImagePublicId(fileName);
            contact.setPicture(imageUrl);

        } else {
            logger.info("file is empty");
        }

        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setMobile(contactForm.getMobile());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        // contact.setPicture(fileURL);
        // contact.setCloudinaryImagePublicId(filename);

        return contact;
    }

    // Method to map Contact to ContactForm (optional)
    public ContactForm toContactForm(Contact contact) {
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setMobile(contact.getMobile());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());

        // set user

        return contactForm;
    }

}

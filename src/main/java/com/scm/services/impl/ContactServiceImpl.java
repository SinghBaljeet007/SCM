package com.scm.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.ContactRepository;
import com.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Contact save(Contact contact) {
        
        String contactId = UUID.randomUUID().toString();

        contact.setId(contactId);

        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Contact contact) {
        
        var oldContact = contactRepository.findById(contact.getId()).orElseThrow(() -> new ResourceNotFoundException("Contact Not Found"));

        oldContact.setName(contact.getName());
        oldContact.setEmail(contact.getEmail());
        oldContact.setMobile(contact.getMobile());
        oldContact.setDescription(contact.getDescription());
        oldContact.setAddress(contact.getAddress());
        oldContact.setPicture(contact.getPicture());
        oldContact.setWebsiteLink(contact.getWebsiteLink());
        oldContact.setLinkedInLink(contact.getLinkedInLink());
        oldContact.setFavorite(contact.isFavorite());
        oldContact.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
        // oldContact.setLinks(contact.getLinks());

        return contactRepository.save(oldContact);

    }

    @Override
    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Override
    public void delete(String id) {
        var contact = contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));

        contactRepository.delete(contact);
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        
        return contactRepository.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        var pageable = PageRequest.of(page, size, sort);


        
        return contactRepository.findByUser(user, pageable);
    }

    @Override
    public Page<Contact> searchByName(String name, int size, int page, String sortBy, String orderBy, User user) {

        Sort sort = orderBy.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUserAndNameContaining(user, name, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String email, int size, int page, String sortBy, String orderBy, User user) {
        Sort sort = orderBy.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUserAndEmailContaining(user, email, pageable);
    }

    @Override
    public Page<Contact> searchByMobile(String mobile, int size, int page, String sortBy, String orderBy, User user) {
        Sort sort = orderBy.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUserAndMobileContaining(user, mobile, pageable);
    }

}

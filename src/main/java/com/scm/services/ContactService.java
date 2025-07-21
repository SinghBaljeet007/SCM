package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {

    // save contact
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    // get contact
    List<Contact> getAll();

    // get contact by id
    Contact getById(String id);

    // delete contact
    void delete(String id);

    // search contact
    Page<Contact> searchByName(String name, int size, int page, String sortBy, String orderBy, User user);

    Page<Contact> searchByEmail(String email, int size, int page, String sortBy, String orderBy, User user);

    Page<Contact> searchByMobile(String mobile, int size, int page, String sortBy, String orderBy, User user);

    // get contacts by userId

    List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction);



}

package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.repositories.UserRepository;
import com.scm.services.UserService;
import com.scm.helpers.AppConstants;
import com.scm.helpers.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public User saveUser(User user) {

        String userId = UUID.randomUUID().toString();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User userFromDb = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        userFromDb.setName(user.getName());
        userFromDb.setMobile(user.getMobile());
        userFromDb.setEmail(user.getEmail());
        userFromDb.setPassword(user.getPassword());
        userFromDb.setAbout(user.getAbout());
        userFromDb.setProfilePic(user.getProfilePic());
        userFromDb.setEnabled(user.isEnabled());
        userFromDb.setEmailVerified(user.isEmailVerified());
        userFromDb.setMobileVerified(user.isMobileVerified());
        userFromDb.setProvider(user.getProvider());
        userFromDb.setProviderUserId(user.getProviderUserId());

        User result = userRepository.save(userFromDb);

        return Optional.ofNullable(result);
    }

    @Override
    public void deleteUser(String id) {
        User userFromDb = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        userRepository.delete(userFromDb);
    }

    @Override
    public boolean isUserExist(String id) {
        User userFromDb = userRepository.findById(id)
                .orElse(null); 
        
        return userFromDb != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User userFromDb = userRepository.findByEmail(email).orElse(null); 
        
        return userFromDb != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}

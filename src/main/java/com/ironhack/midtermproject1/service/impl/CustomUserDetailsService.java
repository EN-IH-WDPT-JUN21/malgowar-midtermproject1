package com.ironhack.midtermproject1.service.impl;

import com.ironhack.midtermproject1.dao.User;
import com.ironhack.midtermproject1.repository.UserRepository;
import com.ironhack.midtermproject1.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User does not exist");
        } else {
            CustomUserDetails customUserDetails = new CustomUserDetails(user.get()); //user is passing to CustomUserDetail in which he gets the roles
            return customUserDetails;
        }
    }
}
package com.ironhack.midtermproject1.controller.impl;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    /*@GetMapping(value = "/username")
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }*/
}

package com.app.dwong.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @PreAuthorize("#oauth2.hasScope('write')")
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

}

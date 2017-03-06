package com.app.dwong.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UserController {

    @PreAuthorize("#oauth2.hasScope('write')")
    @RequestMapping({ "/user", "/me" })
    public Map<String, String> user(Principal user) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", user.getName());
        return map;
    }

    @RequestMapping("/")
    public String home(Principal user) {
        if(user != null) {
            return "Hello " + user.getName();
        }
        return "Unauthorized";
    }

}

package com.brocoding.stocks.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Authentication controller to validate basic authentication send by frontend
 *
 * Created by Dave van Hooijdonk on 21-3-2018.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/authenticate", produces = APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @GetMapping
    public Principal authenticate(Principal user) {
        return user;
    }
}

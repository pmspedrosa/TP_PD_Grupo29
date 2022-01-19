package com.isec.pd_tp.controllers;

import com.isec.pd_tp.data.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @PostMapping("session")
    public ResponseEntity login(@RequestBody(required = false) User user){
        if(user != null){
            //Check login
            //...
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");

            //Define token
            String token = user.getUsername() + "_123";

            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No credentials provided");
    }
}


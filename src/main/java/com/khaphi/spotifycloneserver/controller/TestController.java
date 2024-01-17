package com.khaphi.spotifycloneserver.controller;

import com.khaphi.spotifycloneserver.entity.User;
import com.khaphi.spotifycloneserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    @GetMapping("/hello")
    public ResponseEntity<String> hello () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok("Hello " + user.getFullname());
    }
}

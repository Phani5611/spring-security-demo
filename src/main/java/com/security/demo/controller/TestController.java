package com.security.demo.controller;


import com.security.demo.model.Student;
import com.security.demo.model.Users;
import com.security.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class TestController {

    private final UserService userService;

    @GetMapping
    public String greet(){
        return "Hi, Welcome to Spring Security Demo!";
    }

    @GetMapping("/students")
    public List<Student> getStudentList(){
        return List.of(new Student(1,"Phani"),new Student(2,"Sai"));
    }

    @PostMapping("/student")
    public List<Student> saveStudent(@RequestBody Student payload){
        return List.of(new Student(payload.getId(), payload.getName()));
    }

    @PostMapping("/register")
    public Users saveUser(@RequestBody Users users){
        return userService.saveUser(users);
    }

    @PostMapping("/login")
    public String verifyUser(@RequestBody Users user){
       return userService.verifyUser(user);

    }
}

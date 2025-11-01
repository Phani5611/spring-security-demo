package com.security.demo.service;

import com.security.demo.config.JwtService;
import com.security.demo.model.Users;
import com.security.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final  BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder(12);

    public Users saveUser( Users payload){
        Users user = new Users();
        user.setPassword(bCryptPasswordEncoder.encode(payload.getPassword()));
        user.setCpassword(bCryptPasswordEncoder.encode(payload.getCpassword()));
        user.setName(payload.getName());
      return  userRepository.save(user);
    }

    //Making an Un-authenticated object to Authenticated object.
    public String verifyUser(Users users){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(users.getName(),users.getPassword()));
        return authentication.isAuthenticated()?jwtService.generateToken(users.getName()):"failed to generate JWT Token!";
    }
}

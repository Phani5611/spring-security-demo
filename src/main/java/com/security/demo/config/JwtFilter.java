package com.security.demo.config;


import com.security.demo.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
//Why extending OncePerRequestFilter?
// Every request apply the following filter.
public  class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private  final ApplicationContext context;

    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token ="";
        String username = "";
        final String path = request.getServletPath();
        if (path.equals("/login") || path.equals("/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
             token = authHeader.substring(7);
             username= jwtService.extractUsername(token);
        }

        //SecurityContextHolder - Responsible for if user is already authenticated or not.
        // since we want only un-authenticated users to be checked here.
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){

            UserDetails userDetails= context.getBean(CustomUserDetailService.class).loadUserByUsername(username);
            //Inorder to make token validate we need to have a user who is registered in the system and saved in db
            // This is done by UserDetails object.
            if(jwtService.isTokenValid(token,userDetails)){

                //Generating UsernamePasswordToken is required for the next filter which UsernamePasswordAuthenticationFilter
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                //want to set all details of the Jwt Token to the authToken since it needs to know about it.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //Setting the authToken in the security chain, inorder to use it by the next filters.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //continue with other filters in the chain after this filter.
        filterChain.doFilter(request,response);

    }
}

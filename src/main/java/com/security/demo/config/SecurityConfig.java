package com.security.demo.config;


import com.security.demo.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //telling to  the default setting of spring security
@RequiredArgsConstructor
public class SecurityConfig {


    private final CustomUserDetailService userDetailsService;

    private final JwtFilter jwtFilter;

    @Bean // follow this filter chain instead of default one for security.
    public SecurityFilterChain  securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(Customizer->Customizer.disable())
                .authorizeHttpRequests(request->request
                        .requestMatchers("/swagger-ui/**","register","login").permitAll()
                        .anyRequest().authenticated())// need to authenticate every request
                //.formLogin(Customizer.withDefaults()) // form login for browser
                .httpBasic(Customizer.withDefaults()) // login for REST API using tools like postman or swagger.
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// creating new sessionId everytime
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //Saying spring security to use my custom Jwt Filter before proceeding with UsernamePasswordAuthentication filter.
                .build();
    }


    //Which routes the user authentication by using correct method calls.
    // this is used to avoid default UsernamePassword authentication check and follow the JWT process which user created. and then do the UsernamePassword authentication.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config ) throws Exception {
           return config.getAuthenticationManager();
    }

    //Actually worker of authentication, which validates the user.
    //AuthenticationManager will call the AuthenticationProvider to verify the user.
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

   /* // bypassing the default user authentication filter and creating customized filter for user authentication.
    @Bean
    public UserDetailsService userDetailsService(){
        //Dynamic User Object Building
        UserDetails user = User
                .withDefaultPasswordEncoder()
                .username("Phani")
                .password("1234")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager();
    }*/


}

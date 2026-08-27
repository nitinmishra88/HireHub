package com.boot.hirehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())   // form submit me issue na aaye
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()   // sab allow
            )
            .formLogin(form -> form.disable()); // default login band
      
        return http.build();
    }
}

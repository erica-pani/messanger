package com.web.messanger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.web.messanger.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final int STRENGTH = 10;

    @Autowired
    private MyUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                    .requestMatchers("/login",
                                    "/login/check",
                                    "/css/**", 
                                    "/images/**",
                                    "/favicon.ico",
                                    "/manifest.json",
                                    "/service-worker.js"
                                ).permitAll()
                    .anyRequest().authenticated())

                .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login/check")
                    .defaultSuccessUrl("/groups", true)
                    .failureUrl("/login/failed")
                    .permitAll()
                )

                .authenticationProvider(authenticationProvider())

                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(STRENGTH));
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }

}

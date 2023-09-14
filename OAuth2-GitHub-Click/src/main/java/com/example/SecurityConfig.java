package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/logout", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .logout(logout -> logout
                        //.logoutUrl("/logout").permitAll()
                        .deleteCookies("jsessionid").permitAll()
                        .logoutSuccessUrl("/").permitAll())
              //.oauth2Login((Customizer.withDefaults())
                .oauth2Login((oauthLogin) ->
                        oauthLogin
                                .loginPage("/").permitAll()
                                .defaultSuccessUrl("/user"));
        // @formatter:on
        return http.build();
    }
}

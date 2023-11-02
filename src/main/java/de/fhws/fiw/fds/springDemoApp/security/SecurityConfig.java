package de.fhws.fiw.fds.springDemoApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET, "api/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "api/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "api/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "api/**").permitAll()
        );

        http.httpBasic(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

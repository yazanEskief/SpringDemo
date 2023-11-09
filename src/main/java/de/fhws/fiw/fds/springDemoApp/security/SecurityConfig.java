package de.fhws.fiw.fds.springDemoApp.security;

import de.fhws.fiw.fds.springDemoApp.dao.UserDAOImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${security.active}")
    private boolean securityActive;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if(securityActive) {
            return basicAuth(http);
        }

        return noSecurity(http);
    }

    private SecurityFilterChain noSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .anyRequest().permitAll()
        );

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private SecurityFilterChain basicAuth(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/api/initializedatabase",
                        "/api/cleardatabase",
                        "/api/clearLocations").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,
                        "/api/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                .requestMatchers(HttpMethod.POST,
                        "/api/person/**",
                        "/api/location",
                        "/api/user").hasAnyRole("ADMIN", "MANAGER", "USER")
                .requestMatchers(HttpMethod.PUT,
                        "/api/person/**",
                        "/api/location/**",
                        "/api/user/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                .requestMatchers(HttpMethod.PATCH,
                        "/api/user/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,
                        "/api/person/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE,
                        "/api/person/**",
                        "/api/location/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE,
                        "/api/user/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.httpBasic(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDAOImpl userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(passwordEncoder());
        auth.setUserDetailsService(userService);
        return auth;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

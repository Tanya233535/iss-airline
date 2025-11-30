package com.example.issairline.config;

import com.example.issairline.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                .authenticationProvider(authProvider())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()

                        .requestMatchers("/api/**").authenticated()

                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()

                        .requestMatchers("/aircrafts/new", "/aircrafts/edit/**", "/aircrafts/delete/**")
                        .hasAnyAuthority("ADMIN", "ENGINEER")

                        .requestMatchers("/aircrafts/**")
                        .hasAnyAuthority("ADMIN", "ENGINEER", "DISPATCHER", "VIEWER")

                        .requestMatchers("/flights/new", "/flights/edit/**", "/flights/delete/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER")

                        .requestMatchers("/flights/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER", "ENGINEER", "VIEWER")

                        .requestMatchers("/maintenance/new", "/maintenance/edit/**", "/maintenance/delete/**")
                        .hasAnyAuthority("ADMIN", "ENGINEER")

                        .requestMatchers("/maintenance/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER", "ENGINEER", "VIEWER")

                        .requestMatchers("/crew/new", "/crew/edit/**", "/crew/delete/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER")

                        .requestMatchers("/crew/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER", "ENGINEER", "VIEWER")

                        .requestMatchers("/passengers/new", "/passengers/edit/**", "/passengers/delete/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER")

                        .requestMatchers("/passengers/**")
                        .hasAnyAuthority("ADMIN", "DISPATCHER", "ENGINEER", "VIEWER")

                        .requestMatchers("/users/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(f -> f.loginPage("/login").permitAll())
                .httpBasic(b -> {});

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

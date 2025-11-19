package com.example.issairline.config;

import com.example.issairline.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth


                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()



                        .requestMatchers("/aircrafts/new", "/aircrafts/edit/**", "/aircrafts/delete/**")
                        .hasAnyRole("ADMIN", "ENGINEER")

                        .requestMatchers("/aircrafts/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "DISPATCHER", "VIEWER")


                        .requestMatchers("/flights/new", "/flights/edit/**", "/flights/delete/**")
                        .hasAnyRole("ADMIN", "DISPATCHER")

                        .requestMatchers("/flights/**")
                        .hasAnyRole("ADMIN", "DISPATCHER", "ENGINEER", "VIEWER")


                        .requestMatchers("/users/**")
                        .hasRole("ADMIN")


                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )


                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403")
                );

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

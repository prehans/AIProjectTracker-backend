//package com.SpringProject.Config;
//
//import com.SpringProject.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import com.SpringProject.security.CustomAuthenticationProvider;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//           private final UserService userService;
//           private final CustomAuthenticationProvider customAuthenticationProvider;
//    public SecurityConfig(UserService userService, CustomAuthenticationProvider customAuthenticationProvider) {
//        this.userService = userService;
//        this.customAuthenticationProvider = customAuthenticationProvider;
//    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//
//
//        http
//                .csrf(customizer -> customizer.disable())
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/register","/login").permitAll() // Allow all user-related APIs
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form ->form
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home", true)
//                )
//                .logout(logout->logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login")
//                );
////                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return new ProviderManager(List.of(customAuthenticationProvider));
//    }
//
//    @Bean
//    public UserService userDetailsService() {
//        return userService; // Assuming UserService implements UserDetailsService
//    }
//}

package com.SpringProject.Config;

import com.SpringProject.security.CustomAuthenticationProvider;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private final JWTFilter jwtFilter;

    public SecurityConfig(@Lazy CustomAuthenticationProvider customAuthenticationProvider, JWTFilter jwtFilter) {
        this.customAuthenticationProvider = customAuthenticationProvider;

        this.jwtFilter = jwtFilter;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(customizer -> customizer.disable())
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/user/register", "/api/user/login" ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/api/user/login")
//                        .defaultSuccessUrl("/api/health", true)
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/api/user/login")
//                );
////                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
            .csrf(csrf -> csrf.disable()) // Disable CSRF (not needed for REST API)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                    .requestMatchers("/api/ai/**").authenticated()// Allow login/register without authentication
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session creation
            .formLogin(form -> form.disable()) // ❌ Disable default form login (prevents 302 redirects)
            .httpBasic(httpBasic -> httpBasic.disable()) // Disable Basic Authentication
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add custom authentication filter

    return http.build();
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customAuthenticationProvider));
    }
}


package com.health.care_management.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Import other necessary classes
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// ...
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// ...
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.health.care_management.Service.impl.UserDetailsServiceImpl;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            
            .authorizeHttpRequests(requests ->
                requests
                    .requestMatchers("/auth/**", "/register","/resend-otp", "/verify-otp",
                    "/register/user","/login", "/css/**", "/js/**","/changePassword","/download-prescription", 
                    "/generate-chamber-pdf","/meet","/reset-password","/verify-otp-reset","/get-OtpForPass","/forgot-password").permitAll()
                    .requestMatchers("/user/**").hasRole("USER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/doctor/**").hasRole("DOCTOR")
                    .anyRequest().authenticated()
            )
            .formLogin(login -> login
                    .loginPage("/login")  // Custom login page
                    .loginProcessingUrl("/login")
                    .successHandler(customAuthenticationSuccessHandler())  // Custom success handler
                    .permitAll()
                )

            .logout(logout -> logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

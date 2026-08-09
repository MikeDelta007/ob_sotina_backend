package com.officedubac.project.config;

import com.officedubac.project.models.Role;
import com.officedubac.project.services.UserService;
import com.officedubac.project.models.Profil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    //🔹 Configuration pour l'authentification via API Key (`/v1/api/achatOnline/**`).

    /**
     @Bean
     public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception
     {
     http
     .cors(cors -> cors.configurationSource(request -> {
     CorsConfiguration cors1 = new CorsConfiguration();
     cors1.setAllowedOriginPatterns(List.of("*"));
     cors1.setAllowedMethods(List.of("GET"));
     cors1.setAllowedHeaders(List.of("Authorization", "Content-Type", "API-Key", "API-Secret", "Cache-Control"));
     cors1.setExposedHeaders(List.of("Authorization", "Content-Type", "API-Key", "API-Secret", "Cache-Control"));
     cors1.setAllowCredentials(true);
     return cors1;
     }))
     .csrf(AbstractHttpConfigurer::disable)
     .authorizeHttpRequests(auth -> auth
     .requestMatchers("/api/v1/office-du-bac/**").authenticated()
     )
     .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);

     return http.build();
     }

    // Configuration pour les requêtes nécessitant un JWT (ex : `/api/v1/auth/**`).
     **/

    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:3000")))
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cors1 = new CorsConfiguration();
                    cors1.setAllowedOriginPatterns(List.of("*"));
                    cors1.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    cors1.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    cors1.setExposedHeaders(List.of("Authorization"));
                    cors1.setAllowCredentials(true);
                    return cors1;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/authentification/**").permitAll()
                        .requestMatchers("/api/v1/callback").permitAll()
                        .requestMatchers("/api/v1/import-data/**").permitAll()
                        .requestMatchers("/api/v1/regleMatiere/**").permitAll()
                        //.requestMatchers("/api/v1/pdf/**").hasAnyAuthority(Role.CHEF_ETABLISSEMENT.name(), Role.AGENT_DE_SAISIE.name(), Role.SCOLARITE.name(), Role.RECEPTIONNISTE.name(), Role.ADMIN.name())
                        .requestMatchers("/api/v1/pdf/**").permitAll()
                        .requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs","/webjars/**", "/v3/api-docs/swagger-config").permitAll()
                        .requestMatchers("/api/v1/parametrage/**").permitAll()
                        .requestMatchers("/api/v1/enrollment-candidats/**").permitAll()
                        .requestMatchers("/api/v1/security/**").permitAll()
                        .requestMatchers("/api/v1/files/**").permitAll()
                        .requestMatchers("/api/v1/validation-candidats/**").permitAll()
                        .requestMatchers("/api/v1/payment-FAEB3/**").permitAll()
                        .requestMatchers("/api/v1/notifications/**").permitAll()
                        .requestMatchers("/api/v1/releves-a1/**").permitAll()
                        .requestMatchers("/api/v1/releves-a2/**").permitAll()
                        .requestMatchers("/api/v1/releves-a1-2eme-partie/**").permitAll()
                        .requestMatchers("/api/v1/releves-a2-2eme-partie/**").permitAll()
                        .requestMatchers("/api/v1/releves-a3/**").permitAll()
                        .requestMatchers("/api/v1/releves-a3-2eme-partie/**").permitAll()
                        .requestMatchers("/api/v1/releves-a4/**").permitAll()
                        .requestMatchers("/api/v1/releves-b/**").permitAll()
                        .requestMatchers("/api/v1/releves-c-2eme-partie/**").permitAll()
                        .requestMatchers("/api/v1/releves-d/**").permitAll()
                        .requestMatchers("/api/v1/releves-d-2eme-partie/**").permitAll()
                        .requestMatchers("/api/v1/releves-e/**").permitAll()
                        .requestMatchers("/api/v1/releves-f1/**").permitAll()
                        .requestMatchers("/api/v1/releves-f1-2eme-partie/**").permitAll()
                        .requestMatchers("/api/v1/releves-f7/**").permitAll()
                        .requestMatchers("/api/v1/releves-g/**").permitAll()
                        .requestMatchers("/api/v1/releves-g1/**").permitAll()
                        .requestMatchers("/api/v1/releves-g2/**").permitAll()
                        .requestMatchers("/api/v1/releves-lprime1/**").permitAll()
                        .requestMatchers("/api/v1/releves-l1a/**").permitAll()
                        .requestMatchers("/api/v1/releves-l1b/**").permitAll()
                        .requestMatchers("/api/v1/releves-l2/**").permitAll()
                        .requestMatchers("/api/v1/releves-s1/**").permitAll()
                        .requestMatchers("/api/v1/releves-s2/**").permitAll()
                        .requestMatchers("/api/v1/releves-s3/**").permitAll()
                        .requestMatchers("/api/v1/releves-s4/**").permitAll()
                        .requestMatchers("/api/v1/releves-s5/**").permitAll()
                        .requestMatchers("/api/v1/releves-t1/**").permitAll()
                        .requestMatchers("/api/v1/releves-t2/**").permitAll()
                        .requestMatchers("/api/v1/stats/**").hasAnyAuthority(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //Fournisseur d'authentification depuis la base de données.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // Gestionnaire d'authentification.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Encodeur du mot de passe.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
package com.sistemaMonitoreo.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Eliminamos por completo el constructor que causaba el fallo de inicio de la aplicación

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuramos CORS de forma explícita y deshabilitamos CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Permitir explícitamente todas las peticiones previas de control de CORS (Preflight OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Tus endpoints públicos (Asegura que tu API de auth coincida con esta ruta)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/ws-fleet/**", "/ws/**").permitAll() // Tus WebSockets de IoT
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/fleet/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/fleet/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/fleet/vehicles/*/history").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/fleet/telemetry/live-locations").hasAnyRole("USER", "ADMIN")


                        // Cualquier otra petición a la API requerirá autenticación token
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                );

//        .authorizeHttpRequests(auth -> auth
//                // 1. Permitir Preflight OPTIONS siempre (Manejo de CORS en navegadores)
//                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//                // 2. Rutas verdaderamente PÚBLICAS (No requieren Token)
//                .requestMatchers("/api/auth/**").permitAll()
//                .requestMatchers("/ws-fleet/**", "/ws/**").permitAll()
//                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//
//                // 3. RUTAS COMPARTIDAS (Requieren Token de cualquier rol: USER o ADMIN)
//                // ¡Ojo! Tienen que usar la ruta completa con "/api/fleet/"
//                .requestMatchers(HttpMethod.GET, "/api/fleet/vehicles/*/history").hasAnyRole("USER", "ADMIN")
//                .requestMatchers(HttpMethod.GET, "/api/fleet/vehicles").hasAnyRole("USER", "ADMIN")
//                .requestMatchers(HttpMethod.GET, "/api/fleet/telemetry/live-locations").hasAnyRole("USER", "ADMIN")
//
//                // 4. RUTAS EXCLUSIVAS DE ADMINISTRADOR (Requieren Token con rol ADMIN)
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.GET, "/api/fleet/admin/alerts").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.POST, "/api/fleet/telemetry/**").hasRole("ADMIN")
//
//                // 5. CUALQUIER OTRA RUTA BAJO /api/** (Requiere que al menos esté autenticado)
//                // Eliminamos el .permitAll() de aquí para que obligue a validar el JWT
//                .requestMatchers("/api/**").authenticated()
//
//                // Cualquier otra petición residual del sistema requerirá autenticación
//                .anyRequest().authenticated()
//        );
        return http.build();
    }

    // 2. Definición del Bean de CORS exclusivo para Spring Security e Interceptores HTTP
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Tu frontend en React
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true); // Permite el intercambio seguro con Axios withCredentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
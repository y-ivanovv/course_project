package ru.edu.project.foundation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Кодировщик паролей. Пароли хранятся в виде BCrypt-хеша, а не в открытом виде.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF отключаем: REST API используется из SPA по сессионной cookie
            .csrf(csrf -> csrf.disable())

            // CORS берётся из конфигурации Spring MVC (WebConfig#addCorsMappings)
            .cors(Customizer.withDefaults())

            // Доступ контролируется на уровне AuthInterceptor (сессия + роли),
            // поэтому на уровне Spring Security пропускаем все запросы.
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}

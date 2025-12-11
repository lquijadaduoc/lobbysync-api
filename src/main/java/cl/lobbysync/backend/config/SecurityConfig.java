package cl.lobbysync.backend.config;

import cl.lobbysync.backend.filter.FirebaseTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private FirebaseTokenFilter firebaseTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/api/auth/sync-user").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/access-logs/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/access-logs/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/parcels/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/parcels/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/buildings/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

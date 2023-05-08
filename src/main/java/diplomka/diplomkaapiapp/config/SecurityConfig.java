package diplomka.diplomkaapiapp.config;

import diplomka.diplomkaapiapp.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable();

        http
                .authorizeHttpRequests()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/javainuse-openapi/**",
                        "/techgeeknext-openapi/**",
                        "/api-docs/**",
                        "/docs/**",
                        "/api/file/**"
                        )
                    .permitAll();

        http
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/auth/**",
                        "/api/user/save/**"
                )
                .permitAll();

        http
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/course/save",
                        "/api/component-bank/save"
                )
                .hasAnyAuthority("admin");

        http
                .authorizeHttpRequests()
                .anyRequest()
                    .authenticated();


        http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import com.project.shopapp.services.Email.EmailService;
import com.project.shopapp.services.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
//@EnableMethodSecurity
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final EmailService emailService;

    private final CustomOAuth2UserService oauth2UserService;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/users/login/oauth2**",apiPrefix),
                                    //healthcheck
                                    String.format("%s/healthcheck/**", apiPrefix),


                                    //swagger
                                    //"/v3/api-docs",
                                    //"/v3/api-docs/**",
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/**",
                                    "/swagger-ui/index.html"

                            )
                            .permitAll()
                            .requestMatchers(String.format("%s/oauth2/login/google", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/oauth2/login/facebook", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/emails/users/**", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/emails/users", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/facebooks/users/**", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/facebooks/users", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/users/login/oauth2/**", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/users/phone_number", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/users/phone/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/roles**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/categories/**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/products/**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/products/images/*", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/order_details/**", apiPrefix)).permitAll()

                            .anyRequest()
                            .authenticated();
                            //.anyRequest().permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login()
        ;
        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        return http.build();
    }
}

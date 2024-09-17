package com.project.shopapp.configurations;

import com.project.shopapp.dtos.EmailDTO;
import com.project.shopapp.dtos.FacebookDTO;
import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Email;
import com.project.shopapp.repositories.EmailRepository;
import com.project.shopapp.services.Email.EmailService;
import com.project.shopapp.services.Facebook.FacebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final EmailService emailService;
    private final FacebookService facebookService;
    private final EmailRepository emailRepository;


    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests
                            .anyRequest().permitAll(); // Bạn có thể thay đổi cấu hình này để yêu cầu xác thực cho các endpoint cụ thể
                })
                .oauth2Login(withDefaults())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class); // Thêm filter JWT

        http.cors(Customizer.withDefaults());

        http.oauth2Login(oauth2 -> oauth2.successHandler(authenticationSuccessHandler()));
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Origin", "Authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {

    return (request, response, authentication) -> {
        Integer id = 0;
        String type = "";
        if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String name = oidcUser.getFullName(); // Lấy tên người dùng
            String email = oidcUser.getEmail(); // Lấy email người dùng
            String picture = oidcUser.getPicture();
            Email existingEmail = emailRepository.findUserByEmail(email);
            if (existingEmail==null){
                emailService.createUser(EmailDTO.builder()
                        .email(email)
                        .name(name)
                        .picture(picture)
                        .build());
            }


            // Tiếp tục lấy các thông tin khác nếu cần
            // Sau đó thực hiện lưu thông tin vào cơ sở dữ liệu
            id = this.emailService.getUserByEmail(email).getId();
            type="email";
//            User existingUser = userService.findByggId(id);
//            if (existingUser == null){
//                try {
//                    userService.createUser(UserDTO.builder()
//                            .fullName(name)
//                            .googleAccountId(id)
//                            .email(email)
//                                    .roleId(1L)
//                            .build());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }


        } else {
            if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String name = oauth2User.getAttribute("name"); // Lấy tên người dùng
                String email = oauth2User.getAttribute("email"); // Lấy email người dùng
                String facebookId = oauth2User.getAttribute("id");
                type = "facebook";

                facebookService.createUser(FacebookDTO.builder()
                        .facebookId(facebookId)
                        .email(email)
                        .name(name)
                        .build());
                id = this.facebookService.getFacebookByEmail(email).getId();
            }
        }
        // Thực hiện xử lý sau khi đăng nhập thành công, ví dụ: chuyển hướng
        if (id!=0) {
            response.sendRedirect("http://localhost:4200/users/update?id=" + id+"&type="+type);
        } else {
            response.sendRedirect("http://localhost:4200");
        }
    };
}
}

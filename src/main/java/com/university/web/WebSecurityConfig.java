package com.university.web;

import com.university.model.User;
import com.university.service.AuthService;
import com.university.service.AuthServiceImpl;
import com.university.service.CourseService;
import com.university.service.CourseServiceImpl;
import com.university.service.EnrollmentService;
import com.university.service.EnrollmentServiceImpl;
import com.university.service.DepartmentService;
import com.university.service.DepartmentServiceImpl;
import com.university.service.LecturerService;
import com.university.service.LecturerServiceImpl;
import com.university.service.StudentService;
import com.university.service.StudentServiceImpl;
import com.university.service.UserService;
import com.university.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    @Bean
    AuthService authService() {
        return new AuthServiceImpl();
    }

    @Bean
    StudentService studentService() {
        return new StudentServiceImpl();
    }

    @Bean
    CourseService courseService() {
        return new CourseServiceImpl();
    }

    @Bean
    EnrollmentService enrollmentService() {
        return new EnrollmentServiceImpl();
    }

    @Bean
    DepartmentService departmentService() {
        return new DepartmentServiceImpl();
    }

    @Bean
    LecturerService lecturerService() {
        return new LecturerServiceImpl();
    }

    @Bean
    UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/api/auth/login", "/api/auth/register/student", "/api/public/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/register/student"));
        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider(AuthService authService) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = String.valueOf(authentication.getCredentials());
                User user = authService.authenticate(username, password);
                if (user == null) {
                    throw new BadCredentialsException("Invalid username or password");
                }

                return new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
}

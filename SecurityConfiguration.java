package ToDoList.Infrastructure.Jwt;

import ToDoList.Application.Exceptions.CustomAuthenticationFailureHandler;
import ToDoList.Application.Services.Interfaces.User.IUserService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//защита api c использованием jwt
//отключение csrf и сессии
//разрешение сors-запросов со сторонних сайтов
//подключение собственного обработчика ошибок
//добавление jwt-фильтра в цепочку обработки запросов
//безопасное хэширование запросов
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    //фильтр, который проверяет JWT в каждом запросе
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //обработчик ошибок авторизации
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }
    //настройка авторизации и безопасности в Spring Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //отключение CSRF
                .csrf(AbstractHttpConfigurer::disable)
                //сors позволяет отправлять запросы к api из других доменов
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                //настройки доступа
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/auth", "/error", "/api/v1/users").permitAll()
                        .anyRequest().authenticated()
                )
                //если пользователь не авторизован и не имеет доступ -> CustomAuthenticationFailureHandler
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(customAuthenticationFailureHandler)
                        .authenticationEntryPoint(customAuthenticationFailureHandler)
                )
                //каждое обращение должно нести токен заново
                .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS))
                //обработка jwt до того, как Spring попытается выполнить логин по имени пользователя и паролю
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
//хэширование паролей
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
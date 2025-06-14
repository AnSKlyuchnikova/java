package ToDoList.Infrastructure.Jwt;

import ToDoList.Domain.Services.JwtService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
//фильтрация входящих http-запросов для извлечения и валидации jwt-токена
//Ищем заголовок Authorization, извлекаем JWT, проверяем его и достаем ID пользователя
//Если тоек валиден, создаем объект аутентификации и устанавливаем его в SecurityContext
//чтобы система знала, что пользователь авторизован
//Если токен невалиден или отсутствует, возвращаем ошибку 401
//OncePerRequestFilter — специальный фильтр Spring, гарантирует выполнение фильтра один раз на каждый запрос
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Префикс токена, ожидаемый в заголовке Authorization
    public static final String BEARER_PREFIX = "Bearer ";
    // Имя заголовка, в котором находится JWT
    public static final String HEADER_NAME = "Authorization";
    // Сервис для работы с JWT
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    // Главный метод фильтра, вызывается для каждого HTTP-запроса
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
// Извлекаем заголовок Authorization из запроса
        var authHeader = request.getHeader(HEADER_NAME);
// Если заголовка нет или он не начинается с "Bearer ", передаём управление дальше по цепочке фильтров
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Извлекаем токен без префикса "Bearer"
            final String jwt = authHeader.substring(7);
            // Извлекаем идентификатор пользователя из токена
            final String userId = jwtService.extractUserId(jwt);
// Если userId успешно извлечён и пользователь ещё не аутентифицирован
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Создаём объект аутентификации
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                );
                // Устанавливаем дополнительную информацию о запросе
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Устанавливаем аутентификацию в текущий контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // В случае ошибки токена очищаем контекст безопасности
            SecurityContextHolder.clearContext();
            //Отправляем ошибку 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
            return;
        }
//Передаём запрос дальше по цепочке фильтров
        filterChain.doFilter(request, response);

    }

}

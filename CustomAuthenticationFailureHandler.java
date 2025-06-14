package ToDoList.Application.Exceptions;

import ToDoList.Application.Repositories.ModelsDTO.Other.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
//обработка ситуаций, когда пользователь не авторизован или не имеет доступа
//возвращает понятный json-ответ
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint,AuthenticationFailureHandler, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        createResponse(401, "User is not authorized", response);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        createResponse(401, "User is not authorized", response);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        createResponse(403, "User doesn't have enough rights", response);
    }
    private void createResponse(int statusCode, String message, HttpServletResponse response) throws IOException {
        ResponseModel a = new ResponseModel(
                Integer.toString(statusCode),
                message
        );
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(new ObjectMapper().writeValueAsString(a));
    }
}
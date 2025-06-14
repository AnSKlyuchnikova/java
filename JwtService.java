package ToDoList.Domain.Services;

import ToDoList.Domain.Entities.User.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
//генерируем безопасные JWT-токены
//добавляем claims
//извлекаем данные из токенов
//проверяем валидность токенов и срок действия
@Service
//класс для создания, валидации и парсинга JWT-токенов
public class JwtService {
    @Value("${token.signing.key}") //ключ, используемый для подписи JWT-токена
    private String jwtSigningKey;

//извлекает id пользователя из токена
    public String extractUserId(String token) {

        return extractClaim(token, Claims::getSubject);
    }
//создает JWT-токен для заданного userId
    public String generateToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userId);

        return generateToken(claims, userId.toString());
    }
//проверяет действителен ли токен, сравнивает id из токена с ожидаемым, проверяет не истек ли токен
    public boolean isTokenValid(String token, UUID checkingId) {
        final String userId = extractUserId(token);
        return (checkingId.toString().equals(userId)) && !isTokenExpired(token);
    }
//извлекает любое поле из токена
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }
//построение JWT-токена
    //добавляет claims, устанаввливает id пользователя
    //устанавливает дату выпуска и истечения
    //подписывает токен ключом и алгоритмом HS56
    private String generateToken(Map<String, Object> extraClaims, String userId) {
        return Jwts.builder().claims(extraClaims).subject(userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 6000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

//проверка истек ли токен
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
//извлекает дату истечения токена
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
//расшифровывает jwt и извлекает все claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }
//проберазует строку ключа в байты
    //возвращает ключ, пригодный для подписи токенов
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

package ToDoList.Domain.Entities.User;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
//класс является сущностью Java Persistence API, отображается на таблицу в бд
@Entity
@Table(name = "\"user\"")
//UserDetails исп-ся в Spring Security для хранения информ-ции о пользователе
public class User implements UserDetails {
    public User(){

    }
    //Конструктор с параметрами для создания нового пользователя вручную
    public User(UUID id, String email, String passwordHash) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    // Уникальный идентификатор пользователя, используется как первичный ключ в таблице
    @NotNull
    // Помечает поле как первичный ключ
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
//Эл. почта пользователя - уникальный ключ
    @NotNull
    @Column(name = "email", nullable = false, length = 200)
    private String email;
//Хэш пароля пользователя
    @NotNull
    @Column(name = "passwordHash", nullable = false, length = 200)
    private String passwordHash;
//геттер и сеттер для id
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    //геттер и сеттер для email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
//геттер и сеттер для passwordHash
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    // Метод из UserDetails, возвращает id как право доступа
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(id.toString()));
    }
    // Возвращает хэш пароля
    @Override
    public String getPassword() {
        return passwordHash;
    }
    // Возвращает имя пользователя
    @Override
    public String getUsername() {
        return email;
    }

    //аккаунт активен и не просрочен
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}

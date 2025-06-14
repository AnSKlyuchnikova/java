package ToDoList.Application.Services.Interfaces.User;

import ToDoList.Application.Exceptions.CustomExceptions.KeyNotFoundException;
import ToDoList.Application.Repositories.ModelsDTO.Token.TokenResponseModel;
import ToDoList.Application.Repositories.ModelsDTO.User.UserCreateModel;
import ToDoList.Application.Repositories.ModelsDTO.User.UserLoginDataModel;
import ToDoList.Domain.Entities.User.User;

import java.util.UUID;

public interface IUserService {
    //создание нового пользователя, на выходе токен для нового пользователя
    TokenResponseModel createUser(UserCreateModel userModel);
    //авторизация пользователя, на выходе jwt-токен, если авторизация успешна
    TokenResponseModel authorizeUser(UserLoginDataModel userLoginDataModel) throws KeyNotFoundException;
    //поиск пользователя по id
    User getUserById(UUID userId) throws KeyNotFoundException;
    //поиск пользователя по email
    User getUserByEmail(String email) throws KeyNotFoundException;
}

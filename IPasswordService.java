package ToDoList.Application.Services.Interfaces.User;
//хэширование пароля
//проверка пароля на соответсвие хэшу
public interface IPasswordService {
    String CreateHashFromPassword(String password);

    Boolean IsPasswordEqualToHash(String password, String passwordHash);
}

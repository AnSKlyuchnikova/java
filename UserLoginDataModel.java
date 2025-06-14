package ToDoList.Application.Repositories.ModelsDTO.User;

public class UserLoginDataModel {

    public UserLoginDataModel() {

    }

    public UserLoginDataModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private String email;
    private String password;

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

}

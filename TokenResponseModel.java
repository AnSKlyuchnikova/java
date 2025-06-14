package ToDoList.Application.Repositories.ModelsDTO.Token;
//отправка JWT-токена после регистрации или входа
public class TokenResponseModel {

    public TokenResponseModel() {

    }

    public TokenResponseModel(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    private String jwtToken;

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    public String getJwtToken() {
        return jwtToken;
    }
}

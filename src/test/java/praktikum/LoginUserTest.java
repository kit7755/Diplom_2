package praktikum;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserCreation.getRandomUser;

public class LoginUserTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    private ValidatableResponse responseRegister;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        responseRegister = userClient.register(user);
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка авторизации под существующим пользователем")
    public void loginUser() {
        bearerToken = responseRegister.extract().path("accessToken");

        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка авторизации с неверным паролем")
    public void loginUserWithWrongPass() {
        bearerToken = responseRegister.extract().path("accessToken");

        user.setPassword("");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false)).body("message", is("email or password are incorrect"));
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Логин с неверным email")
    @Description("Проверка авторизации с неверным email")
    public void loginUserWithWrongEmail() {
        bearerToken = responseRegister.extract().path("accessToken");

        user.setEmail("");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false)).body("message", is("email or password are incorrect"));
    }

    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);

    }
}

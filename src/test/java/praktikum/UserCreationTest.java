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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserCreation.getRandomUser;

public class UserCreationTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Регистрация нового пользователя")
    @Description("Проверка успешной регистрации нового пользователя")
    public void createUserTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Попытка создать пользователя, который уже существует")
    @Description("Проверка, что система не позволяет зарегистрировать пользователя с уже существующим email")
    public void createAlreadyExistsUserTest() {
        ValidatableResponse responseRegisterFirstUser = userClient.register(user);
        bearerToken = responseRegisterFirstUser.extract().path("accessToken");

        ValidatableResponse responseRegisterSecondUser = userClient.register(user);
        responseRegisterSecondUser.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("User already exists"));
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Попытка создать пользователя без имени")
    @Description("Проверка, что нельзя зарегистрировать пользователя без указания имени")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Попытка создать пользователя без email")
    @Description("Проверка, что нельзя зарегистрировать пользователя без указания email")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Epic(value = "Тесты пользователей")
    @DisplayName("Попытка создать пользователя без пароля")
    @Description("Проверка, что нельзя зарегистрировать пользователя без указания пароля")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);
    }
}



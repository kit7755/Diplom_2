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

public class UpdateUserDataTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }


    private String registerUserAndGetToken(User user) {
        ValidatableResponse responseRegister = userClient.register(user);
        return responseRegister.extract().path("accessToken");
    }

    @Test
    @Epic(value = "Тесты пользователя")
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка изменения данных пользователя с авторизацией")
    public void changeDataUserWithAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");

        User secondUser = getRandomUser();

        ValidatableResponse responsePatch = userClient.patch(secondUser, bearerToken);
        responsePatch.assertThat().statusCode(SC_OK).body("success", is(true));
    }


    @Test
    @Epic(value = "Тесты пользователя")
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка изменения данных пользователя без авторизации")
    public void changeDataUserWithoutAuthorization() {
        bearerToken = null;

        User secondUser = getRandomUser();

        ValidatableResponse responsePatch = userClient.patch(secondUser, bearerToken);
        responsePatch.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (bearerToken == null) return;

        try {
            userClient.delete(bearerToken);
        } catch (Exception e) {
        }
    }
}

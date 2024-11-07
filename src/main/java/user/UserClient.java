package user;

import utils.Interfaces;
import utils.Specifications;
import data.User;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.Matchers.is;

public class UserClient {
    private static final String AUTH_HEADER = "Authorization";

    @Step("Регистрация пользователя")
    public ValidatableResponse register(User user) {
        return given()
                .spec(Specifications.requestSpecification())
                .body(user)
                .when()
                .post(Interfaces.REGISTER_USER_API)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(User user) {
        return given()
                .spec(Specifications.requestSpecification())
                .body(user)
                .when()
                .post(Interfaces.LOGIN_API)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse delete(String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers(AUTH_HEADER, bearerToken)
                .delete(Interfaces.DELETE_USER_API)
                .then()
                .statusCode(SC_ACCEPTED)
                .body("message", is("User successfully removed"));
    }

    @Step("Обновление данных пользователя")
    public ValidatableResponse patch(User user, String bearerToken) {
        RequestSpecification request = given()
                .spec(Specifications.requestSpecification())
                .contentType(ContentType.JSON)
                .body(user);

        addAuthorizationHeader(request, bearerToken);

        return request
                .when()
                .patch(Interfaces.PATCH_USER_API)
                .then();
    }

    private void addAuthorizationHeader(RequestSpecification request, String bearerToken) {
        if (bearerToken != null && !bearerToken.trim().isEmpty()) {
            request.header(AUTH_HEADER, bearerToken);
        }
    }
}

package order;

import utils.Interfaces;
import utils.Specifications;
import data.Order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient {
    @Step("Создание заказа")
    public ValidatableResponse create(Order order, String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .and()
                .body(order)
                .when()
                .post(Interfaces.CREATE_ORDER_API)
                .then();
    }

    @Step("Получение ингредиентов")
    public static ValidatableResponse getAllIngredients() {
        return given()
                .spec(Specifications.requestSpecification())
                .get(Interfaces.INGREDIENT_API)
                .then();
    }

    @Step("Получение заказов клиента")
    public static ValidatableResponse getClientOrder(String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .get(Interfaces.USER_ORDERS_API)
                .then();
    }
}


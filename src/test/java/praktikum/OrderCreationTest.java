package praktikum;

import data.Order;
import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static order.OrderCreation.getListOrder;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserCreation.getRandomUser;

public class OrderCreationTest {

    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private Order order;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        order = getListOrder();
        orderClient = new OrderClient();
    }

    private String registerAndLoginUser(User user) {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        return responseRegister.extract().path("accessToken");
    }

    @Test
    @Epic(value = "Тесты заказов")
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией")
    public void createOrderWithAuthorizationTest() {
        bearerToken = registerAndLoginUser(user);
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Тесты заказов")
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Тесты заказов")
    @DisplayName("Создание заказа без ингридиентов")
    @Description("Проверка создания заказа без ингридиентов")
    public void createOrderWithoutIngridientTest() {
        bearerToken = registerAndLoginUser(user);
        order.setIngredients(Collections.emptyList());
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Epic(value = "Тесты заказов")
    @DisplayName("Создание заказа с неправильными ингридиентами")
    @Description("Проверка создания заказа с неправильными ингридиентами")
    public void createOrderWithWrongIngridientTest() {
        String[] invalidIngredients = {
                "60d3b41abdacab0026a733c6"
        };

        for (String ingredient : invalidIngredients) {
            List<String> wrongIngredients = new ArrayList<>();
            wrongIngredients.add(ingredient);
            order.setIngredients(wrongIngredients);
            bearerToken = registerAndLoginUser(user);

            ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);
            responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST)
                    .body("success", is(false))
                    .and()
                    .body("message", is("One or more ids provided are incorrect"));
        }
    }

    @After
    public void tearDown() {
        if (bearerToken == null || bearerToken.isEmpty()) return;
        userClient.delete(bearerToken);
    }
}

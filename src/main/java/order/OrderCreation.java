package order;

import data.Order;

public class OrderCreation {
    public static Order getListOrder() {

        return new Order()
                .setIngredients(OrderClient.getAllIngredients().extract().path("data._id"));
    }

}

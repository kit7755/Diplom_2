package user;

import data.User;

import static utils.Util.randomString;

public class UserCreation {

    /**
     * Создает и возвращает объект User с рандомными данными.
     *
     * @return User - пользователь со случайными email, паролем и именем
     */
    public static User getRandomUser() {
        return new User()
                .setEmail(randomString(9) + "@gmail.com")
                .setPassword(randomString(9))
                .setName(randomString(9));
    }
}


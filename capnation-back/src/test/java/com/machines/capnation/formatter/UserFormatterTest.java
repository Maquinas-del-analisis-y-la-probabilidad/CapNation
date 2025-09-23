package com.machines.capnation.formatter;

import com.machines.capnation.model.Role;
import com.machines.capnation.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserFormatterTest {
    private UserFormatter formatter = new UserFormatter();

    @Test
    void create_user_from_text() {
        User expected = new User.UserBuilder("Jhon Doe", "jhondoe@example.com", "password", Role.CUSTOMER)
                .setId(1L)
                .build();
        User actual = formatter.textToUser(
                "1,Jhon Doe,jhondoe@example.com,password,CUSTOMER"
        );
        assertEquals(expected, actual);
    }

    @Test
    void create_text_from_user() {
        User user = new User.UserBuilder("Jhon Doe", "jhondoe@example.com", "password", Role.CUSTOMER)
                .setId(1L)
                .build();
        var expected = "1,Jhon Doe,jhondoe@example.com,password,CUSTOMER";
        var actual = formatter.userToText(user);

        assertEquals(expected, actual);
    }
}

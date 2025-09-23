package com.machines.capnation.formatter;

import com.machines.capnation.model.Role;
import com.machines.capnation.model.User;

public class UserFormatter {
    public static final String separator = ",";

    /***
     * It takes an instance of a user class and returns his representation in database so:
     * id|name|email|password|role
     * @param user: Instance of a Cap
     * @return Text representation of a Cap in the text database
     */
    public String userToText(User user) {
        return new StringBuilder().append(user.getId()).append(separator)
                .append(user.getName()).append(separator)
                .append(user.getEmail()).append(separator)
                .append(user.getPassword()).append(separator)
                .append(user.getRole().toString())
                .toString();
    }

    /***
     * It takes a line in this format: id|name|email|password|role
     * and convert it to an instance of User class
     * @param line that represents the User in file database
     * @return an instance of the clas User with the values of the line
     */
    public User textToUser(String line) {
        String[] attr = line.split(separator);
        return new User.UserBuilder(attr[1], attr[2], attr[3], Role.valueOf(attr[4]))
                .setId(Long.parseLong(attr[0])).build();
    }
}

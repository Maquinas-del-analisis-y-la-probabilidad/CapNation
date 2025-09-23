package com.machines.capnation.model;

import java.util.Objects;

public class User {
    private long id;
    private String name;
    private String email;
    private String password;
    private Role role;

    private User(Long id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static class UserBuilder {
        private long id;
        private String name;
        private String email;
        private String password;
        private Role role;

        public UserBuilder(String name, String email, String password, Role role) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
        }

        public UserBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public User build() {
            return new User(id, name, email, password, role);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    // it compares two user to identify if these have the same attributes
    // without taking into account the id. It will be useful to cheks if there is
    // another similar user in database
    public boolean similar(User user) {
        if (user == null || getClass() != user.getClass()) return false;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, role);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

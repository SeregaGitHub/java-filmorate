package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    @NonNull
    @Email
    private String email;
    @NonNull
    @NotEmpty
    @Pattern(regexp = "\\S*")
    private String login;
    private String name;
    @NonNull
    @Past
    private LocalDate birthday;
    private Set<Integer> friends;

    public User(@NonNull String email, @NonNull String login, String name, @NonNull LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    {
        friends = new HashSet<>();
    }
}
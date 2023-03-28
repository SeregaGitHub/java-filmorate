package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
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
    private Set<Integer> friendshipRequests;
    private Set<Integer> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && email.equals(user.email) && login.equals(user.login)
                && Objects.equals(name, user.name) && birthday.equals(user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday);
    }
}
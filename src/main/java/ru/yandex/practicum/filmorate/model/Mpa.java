package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class Mpa {
    private Integer id;
    @Size(max = 30)
    private String name;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.TreeSet;

@Data
@Builder
public class Film {
    private Integer id;
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @Size(max = 200)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private int duration;
    @NonNull
    private Mpa mpa;
    private TreeSet<Genre> genres;
}
package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class Genre implements Comparable<Genre> {
    private Integer id;
    @Size(max = 30)
    private String name;

    @Override
    public int compareTo(Genre genre) {
        return this.id - genre.id;
    }
}

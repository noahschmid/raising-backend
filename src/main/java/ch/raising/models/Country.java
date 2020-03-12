package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Country {
    private long id;
    private String name;

    public Country(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
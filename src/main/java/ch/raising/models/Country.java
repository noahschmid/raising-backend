package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Country {
    private int id;
    private String name;

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
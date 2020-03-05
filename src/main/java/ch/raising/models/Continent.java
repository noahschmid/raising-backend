package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Continent {
    private int id;
    private String name;

    public Continent(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
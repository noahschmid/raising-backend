package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Industry {
    private int id;
    private String name;

    public Industry(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
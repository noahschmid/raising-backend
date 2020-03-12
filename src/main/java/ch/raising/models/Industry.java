package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Industry {
    private long id;
    private String name;

    public Industry(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
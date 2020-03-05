package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Support {
    private int id;
    private String name;

    public Support(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

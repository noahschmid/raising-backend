package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Support {
    private long id;
    private String name;

    public Support(long id, String name) {
        this.id = id;
        this.name = name;
    }
}

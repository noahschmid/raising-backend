package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Continent {
    private long id;
    private String name;

    public Continent(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof Continent))
            return false;

        return ((Continent)o).getId() == this.id;
    }
}
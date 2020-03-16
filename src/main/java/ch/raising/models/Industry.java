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

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof Industry))
            return false;

        return ((Industry)o).getId() == this.id;
    }
}
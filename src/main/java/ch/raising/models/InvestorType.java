package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestorType {
    private long id;
    private String name;
    private String description;

    public InvestorType(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

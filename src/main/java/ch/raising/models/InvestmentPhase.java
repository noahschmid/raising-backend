package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestmentPhase {
    private long id;
    private String name;

    public InvestmentPhase(long id, String name) {
        this.id = id;
        this.name = name;
    }
}

package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestmentPhase {
    private int id;
    private String name;

    public InvestmentPhase(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

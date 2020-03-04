package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestorType {
    private int id;
    private String name;
    private String description;

    public InvestorType() {
    }
}

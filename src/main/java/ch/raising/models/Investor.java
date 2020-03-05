package ch.raising.models;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Investor {
    private int id;
    private int accountId;
    private int investmentMin;
    private int investmentMax;
    private int investorTypeId;

    public Investor() {
    }
}
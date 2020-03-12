package ch.raising.models;

import lombok.Data;

@Data
public class Investor {
    private String name;
    private String description;
    private long accountId = -1;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private long investorTypeId = -1;
}
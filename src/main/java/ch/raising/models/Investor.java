package ch.raising.models;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Investor {
    private int id = -1;
    private String name;
    private String description;
    private int accountId = -1;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private int investorTypeId = -1;
}
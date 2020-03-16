package ch.raising.models;

import lombok.Data;

@Data
public class Relationship {
    private Long id;
    private Long investorId;
    private Long startupId;
    private String state;
    private int matchingScore;
}
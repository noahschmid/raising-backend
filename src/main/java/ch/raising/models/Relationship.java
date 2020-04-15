package ch.raising.models;

import lombok.Data;

@Data
public class Relationship {
    private Long id;
    private Long investorId;
    private Long startupId;
    private RelationshipState state;
    private int matchingScore;
}
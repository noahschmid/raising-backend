package ch.raising.models;

import ch.raising.models.enums.RelationshipState;
import lombok.Data;

@Data
public class Relationship {
    private Long id = -1l;
    private Long investorId = -1l;
    private Long startupId = -1l;
    private RelationshipState state = null;
    private int matchingScore = -1;
    
}
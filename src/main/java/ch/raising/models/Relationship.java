package ch.raising.models;

import java.sql.Timestamp;

import ch.raising.models.enums.RelationshipState;
import lombok.Data;

@Data
public class Relationship {
    private Long id = -1l;
    private Long investorId = -1l;
    private Long startupId = -1l;
    private RelationshipState state = null;
    private int matchingScore = -1;
    private Timestamp lastchanged;
    private Timestamp investorDecidedAt;
    private Timestamp startupDecidedAt;
}
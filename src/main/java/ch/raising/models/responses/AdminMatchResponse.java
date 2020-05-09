package ch.raising.models.responses;

import java.sql.Timestamp;
import java.util.List;

import ch.raising.models.Interaction;
import ch.raising.models.Investor;
import ch.raising.models.Startup;
import ch.raising.models.enums.RelationshipState;
import lombok.Data;

@Data
public class AdminMatchResponse {
    private long id;
    private RelationshipState state;
    private Startup startup;
    private Investor investor;
    private int matchingPercent;
    private List<Interaction> interactions;
    private Timestamp lastchanged;
}
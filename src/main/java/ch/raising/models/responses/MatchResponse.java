package ch.raising.models.responses;

import java.util.List;

import ch.raising.models.Interaction;
import ch.raising.models.RelationshipState;
import lombok.Data;

@Data
public class MatchResponse {
    private long id;
    private long accountId;
    private RelationshipState state;
    private String companyName;
    private String firstName;
    private String lastName;
    private long profilePictureId;
    private long investorTypeId;
    private long investmentPhaseId;
    private boolean isStartup;
    private int matchingPercent;
    private List<Interaction> interactions;
}
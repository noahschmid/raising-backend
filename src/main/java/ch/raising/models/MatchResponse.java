package ch.raising.models;

import lombok.Data;

@Data
public class MatchResponse {
    private long accountId;
    private long relationshipId;
    private String companyName;
    private String firstName;
    private String lastName;
    private String description;
    private long profilePictureId;
    private int matchingScore;
    private long investorTypeId;
    private long investmentPhaseId;
    private boolean isStartup;
}
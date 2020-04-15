package ch.raising.models;

import lombok.Data;

@Data
public class MatchResponse {
    private long id;
    private long accountId;
    private String companyName;
    private String firstName;
    private String lastName;
    private String description;
    private long profilePictureId;
    private int matchingPercent;
    private long investorTypeId;
    private long investmentPhaseId;
    private boolean isStartup;
}
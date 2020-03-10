package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestorUpdateRequest {
    private int id;
    private String name;
    private String description;
    private int accountId;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private int investorTypeId = -1;
    
    public InvestorUpdateRequest() {
        super();
    }
}

package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestorUpdateRequest {
    private String name;
    private String description;
    private long accountId;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private long investorTypeId = -1;
    
    public InvestorUpdateRequest() {
        super();
    }
}

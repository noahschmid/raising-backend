package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProfileService  {

    @Autowired
    private StartupService startupService;

    @Autowired
    private InvestorService investorService;

    @Autowired
    public ProfileService(StartupService startupService,
                            InvestorService investorService) {
        this.startupService = startupService;
        this.investorService = investorService;
    }

    /**
     * Get profile details
     * @param accountId
     * @return
     */
    public ResponseEntity<?> getProfile(int accountId) {
        ResponseEntity<?> response = startupService.getStartupProfile(accountId);
        if (response.getStatusCode() == HttpStatus.OK)
            return response;
        response = investorService.getInvestorProfile(accountId);
        if (response.getStatusCode() == HttpStatus.OK)
            return response;
        return ResponseEntity.status(500).body(new Error("User has not yet created a profile"));
    }
}
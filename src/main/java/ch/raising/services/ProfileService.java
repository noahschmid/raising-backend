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
}
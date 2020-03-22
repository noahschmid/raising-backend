package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Investor;
import ch.raising.services.InvestorService;

@Controller
@RequestMapping("/investor")
public class InvestorController {
    @Autowired
    InvestorService investorService;

    @Autowired
    public InvestorController() {
    }

    /**
     * Return profile of investor by given accountId
     * @param tableEntryId the tableEntryId of the account the investor belongs to
     * @return ResponseEntity instance with status code and investor or startup in body
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInvestorProfile(@PathVariable int id) {
        return investorService.getProfile(id);
    }

    /**
     * Update profile of investor by given accountId
     * @param request the tableEntryId of the account the investor belongs to
     * @return ResponseEntity with status code and error message (if exists)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateInvestorProfile(@PathVariable int id, @RequestBody Investor request) {
        return investorService.updateInvestor(id, request);
    }

    /**
     * Add new investor profile  
     * @param investor
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> addInvestor(@RequestBody Investor investor) {
        return investorService.registerProfile(investor);
    }
    /**
     * deletes the investmentphase of investor
     * @param tableEntryId
     * @return
     */
    @DeleteMapping("/investmentphase/{tableEntryId}")
    public ResponseEntity<?> deleteInvestmentphaseByInvestorId(@PathVariable long id){
    	return investorService.deleteInvestmentPhaseByIvestorId(id);
    }
    
    /**
     * adds investmentphase to investor
     * @param tableEntryId
     * @return
     */
    
    @PostMapping("/investmentphase/{tableEntryId}")
    public ResponseEntity<?> addInvestmentphaseByInvestorId(@PathVariable long id){
    		return investorService.addInvestmentPhaseByIvestorId(id);
    	}
}

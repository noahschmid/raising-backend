package ch.raising.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.ErrorResponse;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.PasswordResetRequest;
import ch.raising.services.AccountService;
import ch.raising.services.AssignmentTableService;
import ch.raising.utils.JwtUtil;
import ch.raising.controllers.AccountController;
import ch.raising.data.AccountRepository;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.FreeEmailRequest;
import ch.raising.models.Media;

@RequestMapping("/account")
@Controller
public class AccountController {
	
    private final AccountService accountService;
	private final AssignmentTableService assignmentTableService;
    
	
	@Autowired
    public AccountController(AccountService accountService, 
                            AuthenticationManager authenticationManager,
                            JwtUtil jwtUtil,
                            AssignmentTableService assignmentTableService) {
        this.accountService = accountService;
        this.assignmentTableService = assignmentTableService;
	}

	/**
	 * Check whether account exists with the given username and password
	 * @param account provided by the request 
	 * @return response instance with message and status code
	 */
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		return accountService.login(request);
	}
    
    /**
     * Forgot password endpoint. Returns reset code if request is valid 
     * @param request including email address of account
     * @return reset code
     */
    @PostMapping("/forgot")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return accountService.forgotPassword(request);
    }

    /**
     * Reset password endpoint. Sets new password if request is valid
     * @param request has to include reset code and new password
     * @return status code
     */
    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        return accountService.resetPassword(request);
    }
	
	/**
	 * Get all accounts (only for admins)
	 * @return list of all accounts
	 */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
	@ResponseBody
	public List<Account> getAccounts() {
		return accountService.getAccounts();
    }

    /**
     * Check whether email is already registered
     * @param request the request containing the email to test
     * @return response with status 400 if email is already registered, 200 else
     */
    @PostMapping("/valid")
    @ResponseBody
    public ResponseEntity<?> isEmailFree(@RequestBody FreeEmailRequest request) {
        return accountService.isEmailFree(request.getEmail());
    }
    /**
     * registers an account that is neither startup nor investor
     * @param account
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerAccount(@RequestBody Account account) {
        return accountService.registerProfile(account);
    }
    
    /**
	 * Searches for an account by id
	 * @param id the id of the desired account
     * @param request instance of the https request
	 * @return details of specific account
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getAccountById(@PathVariable long id, HttpServletRequest request) {
        if(!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied"));

        return ResponseEntity.ok().body(accountService.findById(id));
    }
    
    /**
	 * Delete account
	 * @param id
     * @param request instance of the http request 
	 * @return response object with status text
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteAccount(@PathVariable int id, HttpServletRequest request) {
        if(!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));

        return accountService.deleteProfile(id);
    }

    /**
     * Update account
     * @return Response entity with status code and message
     */
    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAccount(@PathVariable int id, 
                                            @RequestBody Account updateRequest,
                                            HttpServletRequest request) {
        if(!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));

        return accountService.updateProfile(id, updateRequest);
    }
    /**
     * add country to account
     * @param countryId
     * @return
     */
    @PostMapping("/country")
    @ResponseBody
    public ResponseEntity<?> addCountryToAccount(@RequestBody List<AssignmentTableModel> countries){
    	return assignmentTableService.addToAccountById("country", countries);
    }
    /**
     * delete country from account
     * @param countryId
     * @return
     */
    @PostMapping("/country/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCountryFromAccount(@RequestBody List<AssignmentTableModel> countries){
    	return assignmentTableService.deleteFromAccountById("country",countries);
    }
    /**
     * add continent to account
     * @param continentId
     * @return
     */
    @PostMapping("/continent")
    @ResponseBody
    public ResponseEntity<?> addContinentToAccount(@RequestBody List<AssignmentTableModel> continents){
    	return assignmentTableService.addToAccountById("continent", continents);
    }
    /**
     * delete continent from account
     * @param continentId
     * @return
     */
    @PostMapping("/continent/delete")
    @ResponseBody
    public ResponseEntity<?> deleteContinentFromAccount(@RequestBody List<AssignmentTableModel> continents){
    	return assignmentTableService.deleteFromAccountById("continent", continents);
    }
    /**
     * add supporttype to account
     * @param supportId
     * @return
     */
    @PostMapping("/support")
    @ResponseBody
    public ResponseEntity<?> addSupportToAccount(@RequestBody List<AssignmentTableModel> support){
    	return assignmentTableService.addToAccountById("support", support);
    }
    /**
     * delete supporttype from account
     * @param supportId
     * @return
     */
    @PostMapping("/support/delete")
    @ResponseBody
    public ResponseEntity<?> deleteSupportFromAccount(@RequestBody List<AssignmentTableModel> support){
    	return assignmentTableService.deleteFromAccountById("support", support);
    }
    /**
     * add industry to account    
     * @param industryId
     * @return
     */
    @PostMapping("/industry")
    @ResponseBody
    public ResponseEntity<?> addIndustryToAccount(@RequestBody List<AssignmentTableModel> industries){
    	return assignmentTableService.addToAccountById("industry", industries);
    }
    /**
     * delete industry form account
     * @param industryId
     * @return
     */
    @PostMapping("/industry/delete")
    @ResponseBody
    public ResponseEntity<?> deleteIndustryFromAccount(@RequestBody List<AssignmentTableModel> industries){
    	return assignmentTableService.deleteFromAccountById("industry", industries);
    }
    /**
     * add image to gallery of account    
     * @param industryId
     * @return
     */
    @PostMapping("/gallery")
    @ResponseBody
    public ResponseEntity<?> addImageToAccount(@RequestBody Media img){
    	return accountService.addGalleryImageToAccountById(img);
    }
    /**
     * delete image form gallery account
     * @param industryId
     * @return
     */
    @DeleteMapping("/gallery/{imageId}")
    @ResponseBody
    public ResponseEntity<?> deleteImageFromAccount(@PathVariable long imageId){
    	return accountService.deleteGalleryImageFromAccountById(imageId);
    }
    
    @GetMapping("/gallery")
    @ResponseBody
    public ResponseEntity<?> getGalleryOfAccount(){
    	return accountService.findGalleryImagesFromAccountById();
    }
    /**
     * add profile pic to account    
     * @param industryId
     * @return
     */
    @PostMapping("/profilepicture")
    @ResponseBody
    public ResponseEntity<?> addProfilePictureToAccount(@RequestBody Media img){
    	return accountService.addProfilePictureToAccountById(img);
 
    }
    /**
     * delete image form gallery account
     * @param industryId
     * @return
     */
    @DeleteMapping("/profilepicture/{imageId}")
    @ResponseBody
    public ResponseEntity<?> deleteProfilePictureFromAccount(@PathVariable long imageId){
    	return accountService.deleteProfilePictureFromAccountById(imageId);
    }
    
    @GetMapping("/profilepicture/")
    @ResponseBody
    public ResponseEntity<?> getProfilePictureOfAccount(){
    	return accountService.findProfilePictureFromAccountById();
    }
    
    
}
 
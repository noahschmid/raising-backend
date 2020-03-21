package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.crypto.RsaKeyConversionServicePostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.ErrorResponse;
import ch.raising.services.ProfileService;
import ch.raising.utils.NotImplementedException;


@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }  
    
    @GetMapping("/{tableEntryId}")
    public ResponseEntity<?> getProfile(@PathVariable int id) {
       	return ResponseEntity.status(500).body(new ErrorResponse("Not implemented"));
    }
}


package ch.raising.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.ProfileUpdateRequest;

@Controller
@RequestMapping("/profile/")
public class ProfileController {

    /**
     * Return profile of either startup or investor by given accountId
     * @param id the id of the account the startup or investor belongs to
     * @return ResponseEntity intance with status code and investor or startup in body
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable int id) {
        // TODO: return investor or startup
        return null;
    }

    /**
     * Update profile of either startup or investor by given accountId
     * @param request the id of the account the startup or investor belongs to
     * @return ResponseEntity with status code and error message (if exists)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        // TODO: update profile
        return null;
    }
}
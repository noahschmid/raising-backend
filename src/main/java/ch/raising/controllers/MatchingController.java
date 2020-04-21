package ch.raising.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.MatchingService;
import ch.raising.utils.JwtUtil;

@Controller
@RequestMapping("/match")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public MatchingController(MatchingService matchingService,
                            JwtUtil jwtUtil) {
        this.matchingService = matchingService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Get all matches of account
     */
    @GetMapping
    public ResponseEntity<?> getMatches(HttpServletRequest request) throws Exception {
        String token = jwtUtil.getToken(request);
        return ResponseEntity.ok().body(matchingService.getMatches(jwtUtil.extractId(token),
        jwtUtil.extractIsStartup(token)));
    }

    /**
     * Accept match
     * @return
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptMatch(@PathVariable long id, 
                                HttpServletRequest request) throws Exception {
        String token = jwtUtil.getToken(request);
        matchingService.accept(id, jwtUtil.extractIsStartup(token));
        return ResponseEntity.ok().build();
    }

    /**
     * Start matching algorithm
     */
    @PostMapping("/run")
    public ResponseEntity<?> match(HttpServletRequest request) throws Exception {
        String token = jwtUtil.getToken(request);
        matchingService.match(jwtUtil.extractId(token), jwtUtil.extractIsStartup(token));
        return ResponseEntity.ok().build();
    }

    /**
     * Decline match
     * @return
     */
    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineMatch(@PathVariable long id, 
                                    HttpServletRequest request) throws Exception {
        String token = jwtUtil.getToken(request);
        matchingService.decline(id, jwtUtil.extractIsStartup(token));
        return ResponseEntity.ok().build();
    }
}
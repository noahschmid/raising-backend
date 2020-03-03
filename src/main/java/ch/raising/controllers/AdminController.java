package ch.raising.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Response;

@RequestMapping("/admin")
@Controller
public class AdminController {
    @GetMapping("/")
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok().body(new Response("hello world"));
    }
}
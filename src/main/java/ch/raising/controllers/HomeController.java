package ch.raising.controllers;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.raising.models.ErrorResponse;


@Controller
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok().body(new ErrorResponse("hello world"));
    }
}
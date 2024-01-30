package pt.isec.pdrestapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isec.pdrestapi.models.HelloConfig;

import java.security.Principal;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(Principal principal,
                        @RequestParam(value = "language", required = true) String language)
    {
        if(language.equals("fr")){
            return "Bonjour, " + principal.getName() + "!";
        }
        if(language.equals("es")){
            return "Holla, " + principal.getName() + "!";
        }
        if(language.equals("en")){
            return "Hello, " + principal.getName() + "!";
        }
        return null;
    }

    @PostMapping("/hello")
    public ResponseEntity postHello(@RequestBody HelloConfig helloConfig, Principal principal){
        if(helloConfig.getLanguage() == null){
            return ResponseEntity.badRequest().body("Language is mandatory");
        }
        return generateHello(helloConfig.getLanguage(), principal);
    }

    private ResponseEntity generateHello(String language, Principal principal) {
        switch (language.toLowerCase()){
            case "es" ->{
                return ResponseEntity.ok("Holla, " + principal.getName() + "!");
            }
            case "en" -> {
                return ResponseEntity.ok("Hello, " + principal.getName() + "!");
            }
            case "fr" -> {
                return ResponseEntity.ok("Bonjour, " + principal.getName() + "!");
            }
            default ->
            {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Invalid language.");
            }
        }
    }
}

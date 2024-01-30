package pt.isec.pd.hello.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping()
    public String hello() {
        return "Hello!";
    }

    @GetMapping("{lang}")
    public ResponseEntity<String> helloLanguages(@PathVariable("lang") String language,
                                                 @RequestParam(name = "name", required = false, defaultValue = "") String name) {

        String responseBody;

        switch (language.toUpperCase()){
            case "UK" -> responseBody = "Hello " + name + "!";
            case "PT" -> responseBody ="Ola' " + name + "!";
            case "ES" -> responseBody = "Ola " + name + "!";
            case "FR" -> responseBody = "Salut " + name + "!";
            default -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not supported language:" + language + ".");
            }
        }

        //return ResponseEntity.ok(responseBody);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);

    }

    /*public String helloLanguages(@PathVariable("lang") String language,
                                 @RequestParam(name = "name", required = false, defaultValue = "") String name) {

        return switch (language.toUpperCase()){
            case "UK" -> "Hello " + name + "!";
            case "PT" -> "Ola' " + name + "!";
            case "ES" -> "Ola " + name + "!";
            case "FR" -> "Salut " + name + "!";
            default -> "Unsupported language!";
        };
    }*/
}

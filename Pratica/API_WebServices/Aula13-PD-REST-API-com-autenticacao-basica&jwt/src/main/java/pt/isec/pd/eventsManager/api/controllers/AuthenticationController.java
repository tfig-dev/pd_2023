package pt.isec.pd.eventsManager.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.isec.pd.eventsManager.api.models.User;
import pt.isec.pd.eventsManager.api.repository.Data;
import pt.isec.pd.eventsManager.api.security.TokenService;
import pt.isec.pd.eventsManager.api.models.UserConfig;

@RestController
public class AuthenticationController {
    private final TokenService tokenService;

    public AuthenticationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
//
    @PostMapping("/login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }
//
//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody(required = true) UserConfig userConfig) {
//
//        System.out.println("username: " + userConfig.email);
//        if (Data.getInstance().checkIfUserExists(userConfig.email)) {
//            User user = Data.getInstance().authenticate(userConfig.email, userConfig.password);
//            if (user != null) {
//                return ResponseEntity.ok("{"
//                        + "\"username\":" + "\"" + user.getName() + "\","
//                        + "\"nif\":" + user.getNif() + ","
//                        + "\"email\":" + "\"" + user.getEmail() + "\","
//                        + "\"isAdmin\":" + "\"" + user.isAdmin() + "\"" + "}");
//            }
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador não existe.");
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador ou palavra-pase inválidos.");
//    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody(required = true) UserConfig userConfig) {

        if (Data.getInstance().checkIfUserExists(userConfig.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Utilizador já existe.");
        }

        Data.getInstance().registerUser(new User(userConfig.username, userConfig.nif, userConfig.email, userConfig.password, false));
        return ResponseEntity.status(HttpStatus.CREATED).body("{"
                + "\"username\":" + "\"" + userConfig.username + "\","
                + "\"nif\":" + userConfig.nif + ","
                + "\"email\":" + "\"" + userConfig.email + "\","
                + "\"isAdmin\":" + "\"" + false + "\"" + "toucinho}");
    }
}

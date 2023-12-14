package pt.isec.pd.eventsManager.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping("/register")
    public ResponseEntity register(
            @RequestBody UserConfig userConfig) {

        if (userConfig.username == null || userConfig.nif == 0 || userConfig.email == null || userConfig.password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar utilizador - Parametros necessários em falta.");

        if (Data.getInstance().checkIfUserExists(userConfig.email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Utilizador já existe.");

        return Data.getInstance().registerUser(new User(userConfig.username, userConfig.nif, userConfig.email, userConfig.password, false))
                ? ResponseEntity.status(HttpStatus.CREATED).body(userConfig)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userConfig + "\nErro na base de dados ao criar utilizador.");
    }
}

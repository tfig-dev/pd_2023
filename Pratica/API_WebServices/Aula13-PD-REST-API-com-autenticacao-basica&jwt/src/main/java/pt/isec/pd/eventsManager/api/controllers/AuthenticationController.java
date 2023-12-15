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

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {
    private final TokenService tokenService;

    public AuthenticationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public Map<String, String> login(Authentication authentication) {
        String token = tokenService.generateToken(authentication);
        String lastGeneratedScope = tokenService.getLastGeneratedScope();

        Map<String, String> response = new HashMap<>();

        response.put("token", token);
        response.put("admin", lastGeneratedScope.equalsIgnoreCase("admin") ? "true" : "false");

        return response;
    }
//
//    @GetMapping("/login")
//    public String login(Authentication authentication) {
//        return tokenService.generateToken(authentication);
//    }

    @PostMapping("/register")
    public ResponseEntity register(
            @RequestBody UserConfig userConfig) {

        if (userConfig.username == null || userConfig.nif == 0 || userConfig.email == null || userConfig.password == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar utilizador - Parametros necessários em falta.");

        if (Data.getInstance().checkIfUserExists(userConfig.email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Utilizador já existe.");

        User user = new User(userConfig.username, userConfig.nif, userConfig.email, userConfig.password, false);

        return Data.getInstance().registerUser(user)
                ? ResponseEntity.status(HttpStatus.CREATED).body(user + "\nUtilizador criado com sucesso.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user + "\nErro na base de dados ao criar utilizador.");
    }
}

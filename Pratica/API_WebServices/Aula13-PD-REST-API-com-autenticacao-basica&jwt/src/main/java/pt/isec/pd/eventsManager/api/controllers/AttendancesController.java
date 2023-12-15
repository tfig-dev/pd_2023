package pt.isec.pd.eventsManager.api.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.eventsManager.api.models.Event;
import pt.isec.pd.eventsManager.api.models.User;
import pt.isec.pd.eventsManager.api.repository.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("attendances")
public class AttendancesController {

    @GetMapping("/{id}")
    public ResponseEntity getAttendanceById(
            Authentication authentication,
            @PathVariable("id") Integer id) {

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (!Data.getInstance().checkIfEventExists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        List<User> users = Data.getInstance().getRecords(id);
        if (users.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foram encontrados registos.");

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping
    public ResponseEntity getAttendances(
            Authentication authentication,
            @RequestParam(value = "name", required = false) String eventName,
            @RequestParam(value = "location", required = false) String eventLocation,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<Event> events;

        if (!authentication.getAuthorities().toString().contains("USER"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (startDate != null && endDate != null)
            events = Data.getInstance().getAttendanceRecords_v2(eventName, eventLocation, startDate.toString(), endDate.toString(), authentication.getName());
        else if (startDate != null)
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Uso errado de filtros.");
        else if (endDate != null)
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Uso errado de filtros.");
        else
            events = Data.getInstance().getAttendanceRecords_v2(eventName, eventLocation, null, null, authentication.getName());

        if (events.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foram encontrados eventos.");

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }


    @PostMapping
    public ResponseEntity addAttendance(
            Authentication authentication,
            @RequestBody Map<String, String> requestBody) {

        String code = requestBody.get("code");

        if (!authentication.getAuthorities().toString().contains("USER"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (code == null || code.isEmpty() || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos.");
        }

        int eventID = Data.getInstance().getEventIdByCode(code);

        if (eventID < 0)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Código inválido.");

        if (!Data.getInstance().isCodeValid(code))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Código expirado.");

        if (Data.getInstance().isParticipantRegistered(eventID, authentication.getName()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Presença já registada.");

        return Data.getInstance().addParticipant(eventID, authentication.getName())
                ? ResponseEntity.status(HttpStatus.OK).body("{\"EVENTID\":" + eventID + ",\"USER_EMAIL\":\"" + authentication.getName()+ "\"}" + "\nPresença adicionada.")
                : ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Erro de registo na base de dados.");

    }
}
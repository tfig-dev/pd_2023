package pt.isec.pd.eventsManager.api.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import pt.isec.pd.eventsManager.api.models.Event;
import pt.isec.pd.eventsManager.api.repository.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("events")
public class EventsController {

    @PostMapping
    public ResponseEntity createEvent(
            Authentication authentication,
            @RequestBody Event event) {

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (event.getDate() == null || event.getLocation() == null || event.getName() == null || event.getStartTime() == null || event.getEndTime() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Parametros necessários em falta.");

        if (Data.getInstance().checkIfEventExistsByAll(event))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Evento já existe.");

        return Data.getInstance().createEvent(event)
                ? ResponseEntity.status(HttpStatus.CREATED).body(event + "\nEvento criado com sucesso.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento.");
    }

    @GetMapping
    public ResponseEntity getEvents(
            Authentication authentication,
            @RequestParam(value = "name", required = false) String eventName,
            @RequestParam(value = "location", required = false) String eventLocation,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<Event> events;

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (startDate != null && endDate != null)
            events = Data.getInstance().getAllEvents(eventName, startDate.toString(), endDate.toString(), eventLocation);
        else if (startDate != null)
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Uso errado de filtros.");
        else if (endDate != null)
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Uso errado de filtros.");
        else
            events = Data.getInstance().getAllEvents(eventName, null, null, eventLocation);

        if (events.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foram encontrados eventos.");

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEventById(
            Authentication authentication,
            @PathVariable("id") Integer id) {

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (!Data.getInstance().checkIfEventExists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        Event events = Data.getInstance().getEventById(id);

        if (events == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foram encontrados eventos.");

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity generateCode(
            Authentication authentication,
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Integer> RequestBody) {

        Integer timeout = RequestBody.get("timeout");

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (!Data.getInstance().checkIfEventExists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        if (timeout <= 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao gerar código - Parametros necessários em falta.");

        String generatedCode = Data.getInstance().generateCode();
        if (Data.getInstance().updateCode(id, timeout, generatedCode))
            return ResponseEntity.status(HttpStatus.OK).body(Data.getInstance().getEventById(id) + "\nCódigo gerado com sucesso: " + generatedCode);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro a gerar código na base de dados.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(
            Authentication authentication,
            @PathVariable("id") Integer id) {

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (!Data.getInstance().checkIfEventExists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        if (Data.getInstance().checkIfEventCanBeEdited(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O evento não pode ser eliminado porque já tem presenças registadas.");

        Event event = Data.getInstance().deleteEvent(id);

        if (event == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro da base de dados ao eliminar evento.");

        return ResponseEntity.status(HttpStatus.OK).body(event + "\nEvento eliminado com sucesso.");
    }

}
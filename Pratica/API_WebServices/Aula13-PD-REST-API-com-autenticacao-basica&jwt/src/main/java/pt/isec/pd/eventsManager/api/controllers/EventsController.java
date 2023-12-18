package pt.isec.pd.eventsManager.api.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.isec.pd.eventsManager.api.models.Event;
import pt.isec.pd.eventsManager.api.repository.Data;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("events")
public class EventsController {
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM");

    @PostMapping
    public ResponseEntity createEvent(
            Authentication authentication,
            @RequestBody Event event) {

        System.out.println(event);

        if (!Data.getInstance().verifyToken(authentication))
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Token expirado.");

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        if (event.getName() == null || event.getName().length() < 3)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Nome inválido.");

        if (event.getLocation() == null || event.getLocation().length() < 3)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Localização inválida.");

        try {
            Data.validateDateFormat(event.getDate(), dateFormat);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Data inválida.");
        }

        if (event.getDate() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Data inválida.");

//        try {
//            Data.validateTimeFormat(event.getStartTime(), timeFormat);
//        }
//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Hora de início inválida s.");
//        }
//
//        try {
//            Data.validateTimeFormat(event.getEndTime(), timeFormat);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Hora de fim inválida.");
//        }

        if (event.getStartTime() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Hora de início inválida.");

        if (event.getEndTime() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar evento - Hora de fim inválida.");

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

        if (!Data.getInstance().verifyToken(authentication))
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Token expirado.");

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        List<Event> events;

        if (startDate != null && endDate != null) {
            try {
                Data.validateDateFormat(String.valueOf(startDate), dateFormat);
            } catch (ParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao obter evento - Data inválida.");
            }

            try {
                Data.validateDateFormat(String.valueOf(endDate), dateFormat);
            } catch (ParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao obter evento - Data inválida.");
            }
            events = Data.getInstance().getAllEvents(eventName, startDate.toString(), endDate.toString(), eventLocation);
        }
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
            @PathVariable("id") String id) {

        Integer idInt = null;

        if (!Data.getInstance().verifyToken(authentication))
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Token expirado.");

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao obter evento - ID inválido.");
        }

        if (!Data.getInstance().checkIfEventExists(idInt))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        Event events = Data.getInstance().getEventById(idInt);

        if (events == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foram encontrados eventos.");

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity generateCode(
            Authentication authentication,
            @PathVariable("id") String id,
            @RequestBody Map<String, Integer> RequestBody) {

        Integer idInt = null;

        if (!Data.getInstance().verifyToken(authentication))
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Token expirado.");

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao obter código - ID inválido.");
        }

        Integer timeout = null;

        try {
            timeout = RequestBody.get("timeout");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao obter código - Timeout inválido.");
        }

        if (!Data.getInstance().checkIfEventExists(idInt))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        if (timeout <= 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao gerar código - Parametros necessários em falta.");

        String generatedCode = Data.getInstance().generateCode();
        if (Data.getInstance().updateCode(idInt, timeout, generatedCode))
            return ResponseEntity.status(HttpStatus.OK).body(Data.getInstance().getEventById(idInt) + "\nCódigo gerado com sucesso: " + generatedCode);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro a gerar código na base de dados.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(
            Authentication authentication,
            @PathVariable("id") String id) {

        Integer idInt = null;

        if (!Data.getInstance().verifyToken(authentication))
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Token expirado.");

        if (!authentication.getAuthorities().toString().contains("ADMIN"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilizador sem permissões de Administrador.");

        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao eliminar evento - ID inválido.");
        }

        if (!Data.getInstance().checkIfEventExists(idInt))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O evento não existe.");

        if (Data.getInstance().checkIfEventCanBeEdited(idInt))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O evento não pode ser eliminado porque já tem presenças registadas.");

        Event event = Data.getInstance().deleteEvent(idInt);

        if (event == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro da base de dados ao eliminar evento.");

        return ResponseEntity.status(HttpStatus.OK).body(event + "\nEvento eliminado com sucesso.");
    }

}
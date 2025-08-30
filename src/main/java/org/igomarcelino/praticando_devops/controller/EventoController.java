package org.igomarcelino.praticando_devops.controller;

import org.igomarcelino.praticando_devops.dto.EventoRequestDTO;
import org.igomarcelino.praticando_devops.dto.EventoResponseDTO;
import org.igomarcelino.praticando_devops.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody EventoRequestDTO requestDTO){
         eventoService.criarEvento(requestDTO);

         return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> getAll(){
        var eventos = eventoService.eventos();
        return ResponseEntity.ok().body(eventos);
    }
}

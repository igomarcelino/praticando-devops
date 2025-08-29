package org.igomarcelino.praticando_devops.controller;

import org.igomarcelino.praticando_devops.dto.MensagemRequestDTO;
import org.igomarcelino.praticando_devops.dto.MensagemResponseDTO;
import org.igomarcelino.praticando_devops.service.MensagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensagens")
public class MensagemController {

    private final MensagemService mensagemService;

    public MensagemController(MensagemService mensagemService) {
        this.mensagemService = mensagemService;
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody MensagemRequestDTO requestDTO){
        mensagemService.salvarMensagem(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MensagemResponseDTO>> mensagens(){
        var mensagens = mensagemService.mensagens();
        return ResponseEntity.ok().body(mensagens);
    }

}

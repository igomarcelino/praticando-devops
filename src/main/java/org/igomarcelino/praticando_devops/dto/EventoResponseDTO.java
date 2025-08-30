package org.igomarcelino.praticando_devops.dto;

import org.igomarcelino.praticando_devops.entity.Evento;
import org.igomarcelino.praticando_devops.entity.Mensagem;

public record EventoResponseDTO(
        String nome,
        MensagemResponseDTO mensagemResponseDTO
) {
    public EventoResponseDTO(Evento evento){
        this(evento.getNome_evento(), new MensagemResponseDTO(evento.getMensagem()));
    }
}

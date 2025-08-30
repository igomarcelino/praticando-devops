package org.igomarcelino.praticando_devops.service;

import org.igomarcelino.praticando_devops.dto.EventoRequestDTO;
import org.igomarcelino.praticando_devops.dto.EventoResponseDTO;
import org.igomarcelino.praticando_devops.entity.Evento;
import org.igomarcelino.praticando_devops.entity.Mensagem;
import org.igomarcelino.praticando_devops.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public void criarEvento(EventoRequestDTO requestDTO){
        var evento = new Evento();
        evento.setNome_evento(requestDTO.nome());
        evento.setMensagem(new Mensagem(requestDTO.mensagem()));

        eventoRepository.save(evento);
    }

    public List<EventoResponseDTO> eventos(){
        var eventos = eventoRepository.findAll();
        return eventos.stream().map(EventoResponseDTO::new).toList();
    }
}

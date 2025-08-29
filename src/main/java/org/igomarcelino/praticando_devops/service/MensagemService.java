package org.igomarcelino.praticando_devops.service;

import org.igomarcelino.praticando_devops.dto.MensagemRequestDTO;
import org.igomarcelino.praticando_devops.dto.MensagemResponseDTO;
import org.igomarcelino.praticando_devops.entity.Mensagem;
import org.igomarcelino.praticando_devops.repository.MensagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MensagemService {

    private final MensagemRepository mensagemRepository;

    public MensagemService(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    @Transactional
    public void salvarMensagem(MensagemRequestDTO dto){
        var mensagem = new Mensagem();

        mensagem.setConteudo(dto.conteudo());

        mensagemRepository.save(mensagem);
    }

    public List<MensagemResponseDTO> mensagens(){
        return mensagemRepository.findAll().stream().map(MensagemResponseDTO::new).toList();
    }


}

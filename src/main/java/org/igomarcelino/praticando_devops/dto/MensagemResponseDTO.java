package org.igomarcelino.praticando_devops.dto;

import org.igomarcelino.praticando_devops.entity.Mensagem;

public record MensagemResponseDTO(
        String conteudo
) {
    public MensagemResponseDTO(Mensagem mensagem){
        this(mensagem.getConteudo());
    }
}

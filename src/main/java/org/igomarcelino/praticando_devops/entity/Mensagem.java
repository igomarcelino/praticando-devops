package org.igomarcelino.praticando_devops.entity;

import jakarta.persistence.*;
import org.igomarcelino.praticando_devops.repository.MensagemRepository;

@Entity
@Table(name = "tbl_mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_mensagem;

    private String conteudo;

    public Mensagem() {
    }

    public Mensagem(Long id_mensagem, String conteudo) {
        this.id_mensagem = id_mensagem;
        this.conteudo = conteudo;
    }

    public Mensagem(String mensagem){
        this.conteudo = mensagem;
    }


    public Long getId_mensagem() {
        return id_mensagem;
    }


    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mensagem mensagem)) return false;

        if (getId_mensagem() != null ? !getId_mensagem().equals(mensagem.getId_mensagem()) : mensagem.getId_mensagem() != null)
            return false;
        return getConteudo() != null ? getConteudo().equals(mensagem.getConteudo()) : mensagem.getConteudo() == null;
    }

    @Override
    public int hashCode() {
        int result = getId_mensagem() != null ? getId_mensagem().hashCode() : 0;
        result = 31 * result + (getConteudo() != null ? getConteudo().hashCode() : 0);
        return result;
    }
}

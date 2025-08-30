package org.igomarcelino.praticando_devops.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_evento;

    private String nome_evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mensagem")
    private Mensagem mensagem;


    public Evento() {
    }

    public Evento(Long id_evento, String nome_evento, Mensagem mensagem) {
        this.id_evento = id_evento;
        this.nome_evento = nome_evento;
        this.mensagem = mensagem;
    }

    public Long getId_evento() {
        return id_evento;
    }

    public void setId_evento(Long id_evento) {
        this.id_evento = id_evento;
    }

    public String getNome_evento() {
        return nome_evento;
    }

    public void setNome_evento(String nome_evento) {
        this.nome_evento = nome_evento;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }
}

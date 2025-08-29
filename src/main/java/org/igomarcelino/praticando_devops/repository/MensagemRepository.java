package org.igomarcelino.praticando_devops.repository;

import org.igomarcelino.praticando_devops.entity.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
}

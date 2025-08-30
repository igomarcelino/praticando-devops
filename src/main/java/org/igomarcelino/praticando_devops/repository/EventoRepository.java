package org.igomarcelino.praticando_devops.repository;

import org.igomarcelino.praticando_devops.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento,Long> {
}

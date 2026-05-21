package com.aduanas.comex.notificacion_ms.repository;

import com.aduanas.comex.notificacion_ms.entity.Notificacion;
import com.aduanas.comex.notificacion_ms.enums.EstadoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstado(EstadoNotificacion estado);

}

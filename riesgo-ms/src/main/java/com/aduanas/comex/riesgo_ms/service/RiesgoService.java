package com.aduanas.comex.riesgo_ms.service;



import com.aduanas.comex.riesgo_ms.client.CargaClient;
import com.aduanas.comex.riesgo_ms.dto.RiesgoRequestDTO;
import com.aduanas.comex.riesgo_ms.dto.RiesgoResponseDTO;
import com.aduanas.comex.riesgo_ms.entity.Riesgo;
import com.aduanas.comex.riesgo_ms.enums.CanalRiesgo;
import com.aduanas.comex.riesgo_ms.exception.RiesgoException;
import com.aduanas.comex.riesgo_ms.repository.RiesgoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RiesgoService {

    private final RiesgoRepository repository;

    private final CargaClient cargaClient;


    public RiesgoResponseDTO crear(RiesgoRequestDTO dto) {

        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion(dto.getDescripcion());
        riesgo.setTipoCarga(dto.getTipoCarga());
        riesgo.setOrigen(dto.getOrigen());
        riesgo.setCargaId(dto.getCargaId());
        riesgo.setPuntajeRiesgo(dto.getPuntajeRiesgo());
        riesgo.setMotivoAlerta(dto.getMotivoAlerta());
        riesgo.setCanalAsignado(CanalRiesgo.valueOf(dto.getCanalAsignado()));
        riesgo.setFechaRegistro(LocalDateTime.now());
        riesgo.setFechaEvaluacion(LocalDateTime.now());
        Riesgo guardado = repository.save(riesgo);
        return convertirResponseDTO(guardado);
    }

    public RiesgoResponseDTO evaluarCarga(Long cargaId) {

        int puntaje = calcularPuntaje(cargaId);

        Riesgo riesgo = new Riesgo();
        riesgo.setCargaId(cargaId);
        riesgo.setPuntajeRiesgo(puntaje);
        riesgo.setDescripcion("Evaluación automática de riesgo");
        riesgo.setTipoCarga("Carga general");
        riesgo.setOrigen("Origen desconocido");
        riesgo.setFechaRegistro(LocalDateTime.now());
        riesgo.setFechaEvaluacion(LocalDateTime.now());
        if (puntaje < 40) {
            riesgo.setCanalAsignado(CanalRiesgo.VERDE);
            riesgo.setMotivoAlerta("Carga confiable. Canal Verde automático.");
        } else if (puntaje < 70) {
            riesgo.setCanalAsignado(CanalRiesgo.NARANJA);
            riesgo.setMotivoAlerta("Riesgo moderado. Revisión documental.");
        } else {
            riesgo.setCanalAsignado(CanalRiesgo.ROJO);
            riesgo.setMotivoAlerta("Alto riesgo. Requiere inspección física.");
        }
        Riesgo guardado = repository.save(riesgo);
        return convertirResponseDTO(guardado);
    }

    public List<RiesgoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirResponseDTO)
                .collect(Collectors.toList());
    }

    public RiesgoResponseDTO buscarPorId(Long id) {
        Riesgo riesgo = repository.findById(id).orElseThrow(() -> new RiesgoException("Riesgo no encontrado"));

        return convertirResponseDTO(riesgo);
    }


    public List<RiesgoResponseDTO> buscarPorCanal(String canal) {
        CanalRiesgo canalEnum = CanalRiesgo.valueOf(canal);

        return repository.findByCanalAsignado(canalEnum)
                .stream()
                .map(this::convertirResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RiesgoResponseDTO> buscarPorCarga(Long cargaId) {

        return repository.findByCargaId(cargaId)
                .stream()
                .map(this::convertirResponseDTO)
                .collect(Collectors.toList());
    }


    public RiesgoResponseDTO actualizar(Long id, RiesgoRequestDTO dto) {
        Riesgo riesgo = repository.findById(id)
                .orElseThrow(() -> new RiesgoException("Riesgo no encontrado"));

        riesgo.setDescripcion(dto.getDescripcion());
        riesgo.setTipoCarga(dto.getTipoCarga());
        riesgo.setOrigen(dto.getOrigen());
        riesgo.setCargaId(dto.getCargaId());
        riesgo.setPuntajeRiesgo(dto.getPuntajeRiesgo());
        riesgo.setMotivoAlerta(dto.getMotivoAlerta());
        riesgo.setCanalAsignado(CanalRiesgo.valueOf(dto.getCanalAsignado()));
        riesgo.setFechaEvaluacion(LocalDateTime.now());
        Riesgo actualizado = repository.save(riesgo);
        return convertirResponseDTO(actualizado);
    }
    public void eliminar(Long id) {
        Riesgo riesgo = repository.findById(id).orElseThrow(() -> new RiesgoException("Riesgo no encontrado"));
        repository.delete(riesgo);
    }


    private int calcularPuntaje(Long cargaId) {
        return (int) (Math.random() * 100);
    }

    private RiesgoResponseDTO convertirResponseDTO(Riesgo riesgo) {

        return RiesgoResponseDTO.builder()
                .id(riesgo.getId())
                .descripcion(riesgo.getDescripcion())
                .tipoCarga(riesgo.getTipoCarga())
                .origen(riesgo.getOrigen())
                .fechaRegistro(riesgo.getFechaRegistro())
                .cargaId(riesgo.getCargaId())
                .puntajeRiesgo(riesgo.getPuntajeRiesgo())
                .canalAsignado(riesgo.getCanalAsignado().name())
                .motivoAlerta(riesgo.getMotivoAlerta())
                .fechaEvaluacion(riesgo.getFechaEvaluacion()
                )
                .build();
    }
}
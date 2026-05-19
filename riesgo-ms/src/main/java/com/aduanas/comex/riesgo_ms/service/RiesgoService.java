package com.aduanas.comex.riesgo_ms.service;

// ======================================================
// ===================== IMPORTS =========================
// ======================================================

// DTOs
import com.aduanas.comex.riesgo_ms.dto.RiesgoRequestDTO;
import com.aduanas.comex.riesgo_ms.dto.RiesgoResponseDTO;

// Entity
import com.aduanas.comex.riesgo_ms.entity.Riesgo;

// Enums
import com.aduanas.comex.riesgo_ms.enums.EstadoRiesgo;
import com.aduanas.comex.riesgo_ms.enums.NivelRiesgo;

// Exception personalizada
import com.aduanas.comex.riesgo_ms.exception.RiesgoException;

// Repository
import com.aduanas.comex.riesgo_ms.repository.RiesgoRepository;

// Service
import org.springframework.stereotype.Service;

// Fecha
import java.time.LocalDateTime;

// Listas
import java.util.List;

@Service
public class RiesgoService {

    // ======================================================
    // ===================== REPOSITORY =====================
    // ======================================================
    // Esta variable permite acceder a MySQL.
    //
    // Gracias a JpaRepository podemos usar:
    //
    // save()
    // findAll()
    // findById()
    // deleteById()
    //
    private final RiesgoRepository repository;

    // ======================================================
    // ===================== CONSTRUCTOR ====================
    // ======================================================
    // Spring inyecta automáticamente el repository.
    //
    // Gracias a esto NO hacemos:
    //
    // new RiesgoRepository()
    //
    public RiesgoService(
            RiesgoRepository repository
    ) {

        this.repository = repository;
    }

    // ======================================================
    // ===================== CREAR ==========================
    // ======================================================
    //
    // Este método:
    //
    // 1. recibe datos desde Postman
    // 2. convierte DTO → Entity
    // 3. guarda en MySQL
    // 4. devuelve ResponseDTO
    //
    public RiesgoResponseDTO crear(

            RiesgoRequestDTO dto
    ) {

        // ======================================================
        // ================= VALIDACIÓN EXTRA ===================
        // ======================================================
        //
        // Aunque ya existe @NotBlank,
        // aquí puedes agregar lógica negocio.
        //
        // Ejemplo:
        //
        // NO permitir origen desconocido
        //
        if(dto.getOrigen()
                .equalsIgnoreCase("desconocido")) {

            throw new RiesgoException(
                    "El origen no puede ser desconocido"
            );
        }

        // ======================================================
        // ================= CREAR ENTITY =======================
        // ======================================================
        //
        // La Entity representa
        // la tabla SQL.
        //
        Riesgo riesgo =
                new Riesgo();

        // ======================================================
        // =============== DTO → ENTITY =========================
        // ======================================================
        //
        // Aquí movemos datos
        // desde el DTO hacia la Entity.
        //

        riesgo.setDescripcion(
                dto.getDescripcion()
        );

        // ======================================================
        // STRING → ENUM
        // ======================================================
        //
        // El DTO recibe:
        //
        // "ALTO"
        //
        // pero la Entity usa enum:
        //
        // NivelRiesgo.ALTO
        //
        riesgo.setNivel(
                NivelRiesgo.valueOf(
                        dto.getNivel()
                )
        );

        riesgo.setEstado(
                EstadoRiesgo.valueOf(
                        dto.getEstado()
                )
        );

        riesgo.setTipoCarga(
                dto.getTipoCarga()
        );

        riesgo.setOrigen(
                dto.getOrigen()
        );

        // ======================================================
        // FECHA AUTOMÁTICA
        // ======================================================
        //
        // Guarda fecha y hora actual.
        //
        riesgo.setFechaRegistro(
                LocalDateTime.now()
        );

        // ======================================================
        // ================= GUARDAR MYSQL ======================
        // ======================================================
        //
        // save() viene desde JpaRepository.
        //
        Riesgo guardado =
                repository.save(riesgo);

        // ======================================================
        // ================= RETORNAR DTO =======================
        // ======================================================
        //
        // Devuelve datos limpios
        // al cliente.
        //
        return convertirResponseDTO(
                guardado
        );
    }

    // ======================================================
    // ===================== LISTAR =========================
    // ======================================================
    //
    // Obtiene TODOS los riesgos.
    //
    public List<RiesgoResponseDTO>
    listar() {

        // ======================================================
        // findAll()
        // ======================================================
        //
        // Obtiene todos los registros SQL.
        //
        return repository.findAll()

                // stream()
                // permite transformar listas
                .stream()

                // convierte Entity → DTO
                .map(
                        this::
                                convertirResponseDTO
                )

                // convierte stream → lista
                .toList();
    }

    // ======================================================
    // ===================== BUSCAR ID ======================
    // ======================================================
    //
    // Busca un riesgo por ID.
    //
    public RiesgoResponseDTO
    buscarPorId(Long id) {

        // ======================================================
        // findById()
        // ======================================================
        //
        // Busca por primary key.
        //
        Riesgo riesgo =
                repository.findById(id)

                        // Si NO existe:
                        // lanzar excepción
                        .orElseThrow(() ->

                                new RiesgoException(
                                        "Riesgo no encontrado"
                                )
                        );

        // Entity → DTO
        return convertirResponseDTO(
                riesgo
        );
    }

    // ======================================================
    // ===================== QUERY METHOD ===================
    // ======================================================
    //
    // Busca riesgos por nivel.
    //
    // Ejemplo:
    //
    // ALTO
    // BAJO
    //
    public List<RiesgoResponseDTO>
    buscarPorNivel(
            String nivel
    ) {

        // ======================================================
        // QUERY METHOD
        // ======================================================
        //
        // Spring genera SQL automáticamente:
        //
        // SELECT * FROM riesgos
        // WHERE nivel = ?
        //
        List<Riesgo> lista =
                repository.findByNivel(

                        NivelRiesgo.valueOf(
                                nivel
                        )
                );

        // ======================================================
        // ENTITY → DTO
        // ======================================================
        return lista.stream()

                .map(
                        this::
                                convertirResponseDTO
                )

                .toList();
    }

    // ======================================================
    // ===================== ELIMINAR =======================
    // ======================================================
    //
    // Elimina un riesgo por ID.
    //
    public void eliminar(Long id) {

        // ======================================================
        // VALIDAR EXISTENCIA
        // ======================================================
        //
        // existsById()
        // verifica si existe.
        //
        if(!repository.existsById(id)) {

            throw new RiesgoException(
                    "No se puede eliminar. Riesgo inexistente"
            );
        }

        // ======================================================
        // DELETE SQL
        // ======================================================
        repository.deleteById(id);
    }

    // ======================================================
    // ===================== UPDATE =========================
    // ======================================================
    //
    // Actualiza un riesgo existente.
    //
    public RiesgoResponseDTO actualizar(

            Long id,

            RiesgoRequestDTO dto
    ) {

        // ======================================================
        // BUSCAR EXISTENTE
        // ======================================================
        Riesgo riesgo =
                repository.findById(id)

                        .orElseThrow(() ->

                                new RiesgoException(
                                        "Riesgo no encontrado"
                                )
                        );

        // ======================================================
        // ACTUALIZAR DATOS
        // ======================================================
        riesgo.setDescripcion(
                dto.getDescripcion()
        );

        riesgo.setNivel(
                NivelRiesgo.valueOf(
                        dto.getNivel()
                )
        );

        riesgo.setEstado(
                EstadoRiesgo.valueOf(
                        dto.getEstado()
                )
        );

        riesgo.setTipoCarga(
                dto.getTipoCarga()
        );

        riesgo.setOrigen(
                dto.getOrigen()
        );

        // ======================================================
        // GUARDAR UPDATE
        // ======================================================
        Riesgo actualizado =
                repository.save(riesgo);

        // ======================================================
        // RETORNAR DTO
        // ======================================================
        return convertirResponseDTO(
                actualizado
        );
    }

    // ======================================================
    // ============== ENTITY → RESPONSE DTO =================
    // ======================================================
    //
    // Método reutilizable.
    //
    // Convierte Entity en ResponseDTO.
    //
    private RiesgoResponseDTO
    convertirResponseDTO(

            Riesgo entity
    ) {

        return RiesgoResponseDTO

                // Builder ayuda a construir objetos
                .builder()

                .id(entity.getId())

                .descripcion(
                        entity.getDescripcion()
                )

                .nivel(
                        entity.getNivel().name()
                )

                .estado(
                        entity.getEstado().name()
                )

                .tipoCarga(
                        entity.getTipoCarga()
                )

                .origen(
                        entity.getOrigen()
                )

                .fechaRegistro(
                        entity.getFechaRegistro()
                )

                .build();
    }
}
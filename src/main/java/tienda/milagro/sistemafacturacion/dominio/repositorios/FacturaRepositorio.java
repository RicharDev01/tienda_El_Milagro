package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Factura;

import java.time.LocalDate;
import java.util.List;
/**
 * Repositorio de acceso a datos para la entidad Factura.
 * Se declara aqui el metodo requerido por ClienteServicioImpl
 * para la logica de eliminacion mixta del CU03.
 *
 * NOTA: Este archivo es un fragmento de referencia. Integrar con
 * el repositorio completo de Factura ya existente en el proyecto.
 */
@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, String> {

    /**
     * Verifica si existe al menos una factura asociada al DUI del cliente.
     * El campo de relacion en la entidad Factura es CLI_DUI.
     *
     * @param dui documento unico de identidad del cliente
     * @return true si el cliente tiene facturas registradas
     */
    boolean existsByClienteDui(String dui);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "cliente", "usuario"})
    List<Factura> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "cliente", "usuario"})
    List<Factura> findByClienteDui(String dui);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "cliente", "usuario"})
    List<Factura> findByFechaBetweenAndClienteDui(LocalDate fechaInicio, LocalDate fechaFin, String dui);

    @Override
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "cliente", "usuario"})
    java.util.Optional<Factura> findById(String id);

    @Query("""
            SELECT f.id
            FROM Factura f
            WHERE f.id LIKE 'F%'
            ORDER BY f.id DESC
            """)
    List<String> buscarIdsFacturaDesc();

    @Query("""
            SELECT COALESCE(SUM(f.total), 0)
            FROM Factura f
            WHERE f.fecha BETWEEN :fechaInicio AND :fechaFin
            """)
    java.math.BigDecimal sumarTotalPorRango(@Param("fechaInicio") LocalDate fechaInicio,
                                            @Param("fechaFin") LocalDate fechaFin);
}
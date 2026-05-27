package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Factura;
/**
 * Repositorio de acceso a datos para la entidad Factura.
 * Se declara aqui el metodo requerido por ClienteServicioImpl
 * para la logica de eliminacion mixta del CU03.
 *
 * NOTA: Este archivo es un fragmento de referencia. Integrar con
 * el repositorio completo de Factura ya existente en el proyecto.
 */
@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, Long> {

    /**
     * Verifica si existe al menos una factura asociada al DUI del cliente.
     * El campo de relacion en la entidad Factura es CLI_DUI.
     *
     * @param dui documento unico de identidad del cliente
     * @return true si el cliente tiene facturas registradas
     */
    boolean existsByClienteDui(String dui);
}
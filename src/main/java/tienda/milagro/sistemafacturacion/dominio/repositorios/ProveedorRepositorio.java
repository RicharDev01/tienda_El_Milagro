package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Proveedor;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad Proveedor.
 * Clave primaria de tipo Long (campo id autogenerado).
 *
 * Perfil activo: dev-david
 */
@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Long> {

    /**
     * Busca un proveedor por su nombre exacto.
     * Se usa para la validacion de duplicados en el servicio.
     *
     * @param nombreProveedor nombre del proveedor a buscar
     * @return Optional con el proveedor si existe
     */
    Optional<Proveedor> findByNombreProveedor(String nombreProveedor);

    /**
     * Verifica si ya existe un proveedor con el nombre dado.
     * Alternativa mas eficiente a findByNombreProveedor para validaciones.
     *
     * @param nombreProveedor nombre del proveedor a verificar
     * @return true si ya existe un registro con ese nombre
     */
    boolean existsByNombreProveedor(String nombreProveedor);

    /**
     * Recupera todos los proveedores cuyo estado activo sea verdadero.
     *
     * @return lista de proveedores activos
     */
    List<Proveedor> findByEsActivoTrue();

    /**
     * Recupera todos los proveedores cuyo estado activo sea falso.
     * Util para reportes de auditoria o rehabilitacion de proveedores.
     *
     * @return lista de proveedores inactivos
     */
    List<Proveedor> findByEsActivoFalse();

    /**
     * Busca proveedores cuyo nombre contenga el fragmento indicado,
     * ignorando mayusculas y minusculas.
     *
     * @param fragmentoNombre parte del nombre a buscar
     * @return lista de proveedores que coincidan con el criterio
     */
    List<Proveedor> findByNombreProveedorContainingIgnoreCase(String fragmentoNombre);
}
package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.modelos.Proveedor;
import java.util.List;

/**
 * Contrato del servicio de negocio para la gestion de proveedores.
 * Define las operaciones del caso de uso CU02 del sistema El Milagro.
 *
 * Perfil activo: dev-david
 */
public interface ProveedorServicio {

    /**
     * Registra un nuevo proveedor en el sistema.
     * Asigna automaticamente la fechaRegistro y establece esActivo en true.
     * Lanza excepcion si el nombre del proveedor ya existe.
     *
     * @param proveedor objeto con los datos del nuevo proveedor
     * @return proveedor persistido con su id y fechaRegistro asignados
     */
    Proveedor registrar(Proveedor proveedor);

    /**
     * Modifica los datos de un proveedor existente identificado por su id.
     * Actualiza automaticamente la fechaModificacion.
     * Lanza excepcion si el id no corresponde a ningun proveedor.
     *
     * @param id               identificador del proveedor a modificar
     * @param datosActualizados objeto con los nuevos datos
     * @return proveedor actualizado
     */
    Proveedor modificar(Long id, Proveedor datosActualizados);

    /**
     * Lista todos los proveedores registrados, activos e inactivos.
     *
     * @return lista completa de proveedores
     */
    List<Proveedor> listarTodos();

    /**
     * Lista unicamente los proveedores cuyo campo esActivo sea verdadero.
     *
     * @return lista de proveedores activos
     */
    List<Proveedor> listarActivos();

    /**
     * Busca un proveedor por su id unico.
     * Lanza excepcion si no se encuentra.
     *
     * @param id identificador del proveedor
     * @return proveedor encontrado
     */
    Proveedor buscarPorId(Long id);

    /**
     * Busca proveedores cuyo nombre contenga el fragmento indicado.
     *
     * @param fragmentoNombre parte del nombre a buscar
     * @return lista de proveedores que coincidan con el criterio
     */
    List<Proveedor> buscarPorNombre(String fragmentoNombre);

    /**
     * Desactiva un proveedor mediante eliminacion logica (esActivo = false).
     * Si el proveedor tiene productos asociados, se desactiva igualmente
     * pero se registra la condicion en los logs para auditoria.
     * Lanza excepcion si el id no existe.
     *
     * @param id identificador del proveedor a desactivar
     */
    void desactivar(Long id);

    /**
     * Reactiva un proveedor previamente desactivado.
     * Actualiza fechaModificacion al momento de la reactivacion.
     *
     * @param id identificador del proveedor a reactivar
     * @return proveedor con esActivo en true
     */
    Proveedor reactivar(Long id);
}
package tienda.milagro.sistemafacturacion.dominio.servicios;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Proveedor;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ProveedorRepositorio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementacion de la logica de negocio para la gestion de proveedores.
 * Cubre el caso de uso CU02 del sistema de facturacion El Milagro.
 *
 * Perfil activo: dev-david
 *
 * Responsabilidades:
 *   - Validacion de nombres duplicados.
 *   - Gestion de auditoria (fechaRegistro / fechaModificacion).
 *   - Eliminacion logica con validacion opcional de productos asociados.
 */
@Service
@Transactional
public class ProveedorServicioImpl implements ProveedorServicio {

    // ------------------------------------------------------------------
    // Logger
    // ------------------------------------------------------------------

    private static final Logger registro = LoggerFactory.getLogger(ProveedorServicioImpl.class);

    // ------------------------------------------------------------------
    // Dependencias
    // ------------------------------------------------------------------

    private final ProveedorRepositorio proveedorRepositorio;

    /**
     * Inyeccion por constructor para facilitar pruebas unitarias con mocks.
     *
     * @param proveedorRepositorio repositorio de proveedores
     */
    public ProveedorServicioImpl(ProveedorRepositorio proveedorRepositorio) {
        this.proveedorRepositorio = proveedorRepositorio;
    }

    // ------------------------------------------------------------------
    // Implementacion del contrato
    // ------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Validacion: si el nombre ya existe se lanza IllegalArgumentException.
     * Auditoria: fechaRegistro se asigna con LocalDateTime.now().
     *            esActivo se inicializa en true.
     *            fechaModificacion se deja en null al momento del registro.
     */
    @Override
    public Proveedor registrar(Proveedor proveedor) {
        validarNombreNoDuplicado(proveedor.getNombreProveedor(), null);

        proveedor.setEsActivo(true);
        proveedor.setFechaRegistro(LocalDateTime.now());
        proveedor.setFechaModificacion(null);

        Proveedor proveedorGuardado = proveedorRepositorio.save(proveedor);

        registro.info("[CU02] Proveedor registrado: id={}, nombre={}",
                proveedorGuardado.getId(), proveedorGuardado.getNombreProveedor());

        return proveedorGuardado;
    }

    /**
     * {@inheritDoc}
     *
     * Auditoria: fechaModificacion se actualiza automaticamente con LocalDateTime.now().
     * Validacion: el nombre nuevo no puede coincidir con otro proveedor distinto.
     */
    @Override
    public Proveedor modificar(Long id, Proveedor datosActualizados) {
        Proveedor proveedorExistente = obtenerOLanzarExcepcion(id);

        // Validar nombre solo si realmente cambia
        String nombreNuevo = datosActualizados.getNombreProveedor();
        if (!proveedorExistente.getNombreProveedor().equalsIgnoreCase(nombreNuevo)) {
            validarNombreNoDuplicado(nombreNuevo, id);
        }

        proveedorExistente.setNombreProveedor(nombreNuevo);

        if (datosActualizados.getContacto() != null) {
            proveedorExistente.setContacto(datosActualizados.getContacto());
        }

        // Auditoria de modificacion
        proveedorExistente.setFechaModificacion(LocalDateTime.now());

        Proveedor proveedorActualizado = proveedorRepositorio.save(proveedorExistente);

        registro.info("[CU02] Proveedor actualizado: id={}, nombre={}",
                proveedorActualizado.getId(), proveedorActualizado.getNombreProveedor());

        return proveedorActualizado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listarTodos() {
        return proveedorRepositorio.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listarActivos() {
        return proveedorRepositorio.findByEsActivoTrue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Proveedor buscarPorId(Long id) {
        return obtenerOLanzarExcepcion(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> buscarPorNombre(String fragmentoNombre) {
        return proveedorRepositorio.findByNombreProveedorContainingIgnoreCase(fragmentoNombre);
    }

    /**
     * {@inheritDoc}
     *
     * Logica de eliminacion logica:
     *   1. Se verifica si el proveedor tiene productos asociados.
     *   2. Si tiene productos, se registra una advertencia en el log
     *      pero la desactivacion procede igualmente para no bloquear
     *      operaciones administrativas. El inventario existente no se altera.
     *   3. Se actualiza esActivo = false y fechaModificacion = now().
     */
    @Override
    public void desactivar(Long id) {
        Proveedor proveedor = obtenerOLanzarExcepcion(id);

        if (proveedor.getEsActivo() != null && !proveedor.getEsActivo()) {
            throw new IllegalStateException(
                    "El proveedor con id " + id + " ya se encuentra inactivo.");
        }

        // Validacion opcional: advertir si tiene productos asociados
        boolean tieneProductos = proveedor.getProductos() != null
                && !proveedor.getProductos().isEmpty();

        if (tieneProductos) {
            registro.warn("[CU02] El proveedor id={} tiene {} producto(s) asociado(s). " +
                    "Se procedera con la desactivacion logica.", id,
                    proveedor.getProductos().size());
        }

        proveedor.setEsActivo(false);
        proveedor.setFechaModificacion(LocalDateTime.now());
        proveedorRepositorio.save(proveedor);

        registro.info("[CU02] Proveedor desactivado (eliminacion logica): id={}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Proveedor reactivar(Long id) {
        Proveedor proveedor = obtenerOLanzarExcepcion(id);

        if (proveedor.getEsActivo() != null && proveedor.getEsActivo()) {
            throw new IllegalStateException(
                    "El proveedor con id " + id + " ya se encuentra activo.");
        }

        proveedor.setEsActivo(true);
        proveedor.setFechaModificacion(LocalDateTime.now());

        Proveedor proveedorReactivado = proveedorRepositorio.save(proveedor);

        registro.info("[CU02] Proveedor reactivado: id={}", id);

        return proveedorReactivado;
    }

    // ------------------------------------------------------------------
    // Metodos privados de apoyo
    // ------------------------------------------------------------------

    /**
     * Recupera un proveedor por id o lanza EntityNotFoundException si no existe.
     *
     * @param id identificador del proveedor
     * @return entidad Proveedor encontrada
     */
    private Proveedor obtenerOLanzarExcepcion(Long id) {
        return proveedorRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontro ningun proveedor con el id: " + id));
    }

    /**
     * Valida que el nombre del proveedor no este registrado por otro proveedor distinto.
     *
     * @param nombre      nombre a validar
     * @param idExcluido  id del proveedor que se esta editando (null en registros nuevos)
     */
    private void validarNombreNoDuplicado(String nombre, Long idExcluido) {
        proveedorRepositorio.findByNombreProveedor(nombre).ifPresent(proveedorExistente -> {
            // Si el duplicado es el mismo proveedor que se edita, no es conflicto
            if (!proveedorExistente.getId().equals(idExcluido)) {
                throw new IllegalArgumentException(
                        "Ya existe un proveedor registrado con el nombre: " + nombre);
            }
        });
    }
}
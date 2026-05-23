package tienda.milagro.sistemafacturacion.dominio.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Cliente;
import tienda.milagro.sistemafacturacion.dominio.repositorios.ClienteRepositorio;
import tienda.milagro.sistemafacturacion.dominio.repositorios.FacturaRepositorio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementacion de la logica de negocio para la gestion de clientes.
 * Cubre el caso de uso CU03 del sistema de facturacion El Milagro.
 */
@Service
@Transactional
public class ClienteServicioImpl implements ClienteServicio {

    // ------------------------------------------------------------------
    // Dependencias
    // ------------------------------------------------------------------

    private final ClienteRepositorio clienteRepositorio;
    private final FacturaRepositorio facturaRepositorio;

    /**
     * Constructor con inyeccion de dependencias explicita (sin @Autowired en campo).
     *
     * @param clienteRepositorio repositorio de clientes
     * @param facturaRepositorio repositorio de facturas, usado para validar eliminacion
     */
    public ClienteServicioImpl(ClienteRepositorio clienteRepositorio,
                                FacturaRepositorio facturaRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.facturaRepositorio = facturaRepositorio;
    }

    // ------------------------------------------------------------------
    // Operaciones del contrato
    // ------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Validacion: si el DUI ya existe en la base de datos se lanza
     * una excepcion de argumento ilegal para evitar duplicados.
     */
    @Override
    public Cliente registrar(Cliente cliente) {
        if (clienteRepositorio.existsById(cliente.getDui())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con el DUI: " + cliente.getDui());
        }
        // El campo esActivo se inicializa en true al momento del registro
        cliente.setEsActivo(true);
        cliente.setFechaRegistro(LocalDateTime.now());
        return clienteRepositorio.save(cliente);
    }

    /**
     * {@inheritDoc}
     *
     * Solo se actualizan los campos permitidos; el DUI (PK) permanece invariable.
     */
    @Override
    public Cliente modificar(String dui, Cliente datosActualizados) {
        Cliente clienteExistente = obtenerOLanzarExcepcion(dui);

        clienteExistente.setPrimerNombre(datosActualizados.getPrimerNombre());
        clienteExistente.setPrimerApellido(datosActualizados.getPrimerApellido());

        // Actualizamos los demas campos que el modelo exponga (se listan los tipicos;
        // ajustar segun atributos reales de la entidad Cliente)
        if (datosActualizados.getSegundoNombre() != null) {
            clienteExistente.setSegundoNombre(datosActualizados.getSegundoNombre());
        }
        if (datosActualizados.getSegundoApellido() != null) {
            clienteExistente.setSegundoApellido(datosActualizados.getSegundoApellido());
        }

        clienteExistente.setFechaModificacion(LocalDateTime.now());

        return clienteRepositorio.save(clienteExistente);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepositorio.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        return clienteRepositorio.findByEsActivoTrue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorDui(String dui) {
        return obtenerOLanzarExcepcion(dui);
    }

    /**
     * {@inheritDoc}
     *
     * Logica de eliminacion mixta:
     * 1. Si el cliente tiene facturas en la tabla FACTURA -> eliminacion logica
     *    (esActivo = false). No se borra el registro fisicamente para preservar
     *    la integridad referencial.
     * 2. Si no tiene ninguna factura asociada -> eliminacion fisica del registro.
     */
    @Override
    public void eliminar(String dui) {
        Cliente cliente = obtenerOLanzarExcepcion(dui);

        boolean tieneFacturas = facturaRepositorio.existsByClienteDui(dui);

        if (tieneFacturas) {
            // Eliminacion logica: el historial de facturas queda intacto
            cliente.setEsActivo(false);
            clienteRepositorio.save(cliente);
        } else {
            // Eliminacion fisica: no hay dependencias que protejer
            clienteRepositorio.delete(cliente);
        }
    }

    // ------------------------------------------------------------------
    // Metodos privados de apoyo
    // ------------------------------------------------------------------

    /**
     * Recupera un cliente por DUI o lanza una excepcion si no existe.
     *
     * @param dui documento unico de identidad del cliente
     * @return entidad Cliente encontrada
     * @throws jakarta.persistence.EntityNotFoundException si el DUI no existe
     */
    private Cliente obtenerOLanzarExcepcion(String dui) {
        return clienteRepositorio.findByDui(dui)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "No se encontro ningun cliente con el DUI: " + dui));
    }
}
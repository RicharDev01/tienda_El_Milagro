package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.modelos.Cliente;
import java.util.List;

/**
 * Contrato del servicio de negocio para la gestion de clientes.
 * Define las operaciones del caso de uso CU03.
 */
public interface ClienteServicio {

    /**
     * Registra un nuevo cliente en el sistema.
     * Lanza excepcion si el DUI ya existe.
     *
     * @param cliente objeto con los datos del nuevo cliente
     * @return cliente persistido con su idInterno asignado
     */
    Cliente registrar(Cliente cliente);

    /**
     * Modifica los datos de un cliente existente identificado por su DUI.
     * Lanza excepcion si el DUI no corresponde a ningun cliente.
     *
     * @param dui     identificador del cliente a modificar
     * @param cliente objeto con los nuevos datos
     * @return cliente actualizado
     */
    Cliente modificar(String dui, Cliente cliente);

    /**
     * Lista todos los clientes registrados en el sistema, activos e inactivos.
     *
     * @return lista completa de clientes
     */
    List<Cliente> listarTodos();

    /**
     * Lista unicamente los clientes cuyo campo esActivo sea verdadero.
     *
     * @return lista de clientes activos
     */
    List<Cliente> listarActivos();

    /**
     * Busca un cliente por su DUI.
     * Lanza excepcion si no se encuentra.
     *
     * @param dui documento unico de identidad
     * @return cliente encontrado
     */
    Cliente buscarPorDui(String dui);

    /**
     * Elimina un cliente aplicando la siguiente logica:
     * - Si el cliente tiene facturas asociadas: eliminacion logica (esActivo = false).
     * - Si el cliente no tiene facturas: eliminacion fisica del registro.
     *
     * @param dui identificador del cliente a eliminar
     */
    void eliminar(String dui);
}
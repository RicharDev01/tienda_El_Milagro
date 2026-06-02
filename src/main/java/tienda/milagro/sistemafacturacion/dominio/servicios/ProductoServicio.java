package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;

import java.util.List;

public interface ProductoServicio {

    /**
     * Retorna todos los productos registrados en el sistema,
     * independientemente de su estado.
     */
    List<Producto> listarTodos();

    /**
     * Retorna únicamente los productos cuyo estado sea activo.
     */
    List<Producto> listarActivos();

    /**
     * Busca productos activos cuyo nombre contenga el texto indicado,
     * sin distinción entre mayúsculas y minúsculas.
     *
     * @param nombre fragmento de texto a buscar
     * @return lista de productos activos que coincidan
     */
    List<Producto> buscarPorNombre(String nombre);

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     * @throws RuntimeException si no existe un producto con el id proporcionado
     */
    Producto buscarPorId(Long id);

    /**
     * Registra un nuevo producto en el sistema.
     * Establece esActivo = true y fechaRegistro = ahora por defecto.
     *
     * @param producto entidad con los datos del nuevo producto
     * @return el producto persistido con su id generado
     * @throws IllegalArgumentException si el precio es negativo
     */
    Producto registrarProducto(Producto producto);

    /**
     * Actualiza los campos modificables de un producto existente:
     * nombre, precio y proveedor. Preserva la fecha de registro original
     * y asigna fechaModificacion = ahora.
     *
     * @param id       identificador del producto a actualizar
     * @param producto entidad con los nuevos valores
     * @return el producto actualizado
     * @throws RuntimeException        si no existe un producto con el id proporcionado
     * @throws IllegalArgumentException si el precio es negativo
     */
    Producto actualizar(Long id, Producto producto);

    /**
     * Realiza una eliminación lógica del producto: establece esActivo = false
     * sin eliminar el registro de la base de datos.
     *
     * @param id identificador del producto a desactivar
     * @throws RuntimeException si no existe un producto con el id proporcionado
     */
    void desactivar(Long id);
}
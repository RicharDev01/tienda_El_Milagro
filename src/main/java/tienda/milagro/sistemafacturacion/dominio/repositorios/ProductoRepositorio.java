package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Producto;

import java.util.List;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    /**
     * Retorna todos los productos cuyo estado sea activo (esActivo = true).
     */
    List<Producto> findByEsActivoTrue();

    /**
     * Busca productos activos cuyo nombre contenga la cadena indicada,
     * ignorando diferencias entre mayúsculas y minúsculas.
     *
     * @param nombre fragmento de texto a buscar dentro del nombre del producto
     * @return lista de productos activos que coincidan con el criterio
     */
    List<Producto> findByNombreProductoContainingIgnoreCaseAndEsActivoTrue(String nombre);
}
package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad Cliente.
 * La clave primaria es el DUI (String).
 */
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {

    /**
     * Verifica si existe un cliente con el identificador interno dado.
     * Usado para validaciones de unicidad sobre el campo autogenerado.
     *
     * @param idInterno identificador interno autogenerado
     * @return true si ya existe un registro con ese idInterno
     */
    boolean existsByIdInterno(Long idInterno);

    /**
     * Recupera todos los clientes cuyo estado activo sea verdadero.
     *
     * @return lista de clientes activos
     */
    List<Cliente> findByEsActivoTrue();

    /**
     * Busca un cliente por su DUI, independientemente de si esta activo.
     * Se hereda de JpaRepository como findById, pero se declara explicitamente
     * con nombre semantico en espanol para mayor legibilidad del servicio.
     *
     * @param dui documento unico de identidad del cliente
     * @return Optional con el cliente encontrado o vacio
     */
    Optional<Cliente> findByDui(String dui);
}
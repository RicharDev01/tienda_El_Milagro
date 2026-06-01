package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Rol;

import java.util.Optional;

@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombreRol(String nombreRol);

}


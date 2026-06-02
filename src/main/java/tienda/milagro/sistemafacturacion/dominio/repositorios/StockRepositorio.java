package tienda.milagro.sistemafacturacion.dominio.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda.milagro.sistemafacturacion.persistencia.modelos.Stock;

import java.util.Optional;

@Repository
public interface StockRepositorio extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductoId(Long productoId);

}


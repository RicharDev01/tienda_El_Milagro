package tienda.milagro.sistemafacturacion.dominio.servicios;

import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioActualizarDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioCrearDto;
import tienda.milagro.sistemafacturacion.persistencia.gestiones.UsuarioRespuestaDto;

import java.util.List;

public interface UsuarioServicio {

    UsuarioRespuestaDto registrar(UsuarioCrearDto dto);

    UsuarioRespuestaDto modificar(Long id, UsuarioActualizarDto dto);

    List<UsuarioRespuestaDto> listarTodos();

    List<UsuarioRespuestaDto> listarActivos();

    UsuarioRespuestaDto buscarPorId(Long id);

    void eliminar(Long id);

    UsuarioRespuestaDto cambiarEstado(Long id);

}
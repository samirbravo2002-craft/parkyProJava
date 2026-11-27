package com.example.ejercicio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejercicio.dao.ClienteDao;
import com.example.ejercicio.dao.ReservaDao;
import com.example.ejercicio.dao.RolDao;
import com.example.ejercicio.dao.UsuarioDao;
import com.example.ejercicio.dao.VehiculoDao;
import com.example.ejercicio.domain.Cliente;
import com.example.ejercicio.domain.Rol;
import com.example.ejercicio.domain.Usuario;
import com.example.ejercicio.domain.Vehiculo;
import com.example.ejercicio.dto.ClienteForm;
import com.example.ejercicio.util.Texto;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private RolDao rolDao;

    @Autowired
    private VehiculoDao vehiculoDao;

    @Autowired
    private ReservaDao reservaDao;

    @Override
    public List<Cliente> listarClientes() {
        return clienteDao.findAllByOrderByUsuarioNombreAscUsuarioApellidoAsc();
    }

    @Override
    public Cliente buscarClientePorId(Integer noDocumento) {
        return noDocumento == null ? null : clienteDao.findById(noDocumento).orElse(null);
    }

    @Override
    @Transactional
    public void guardarCliente(ClienteForm f) {
        Cliente cliente;
        Usuario usuario;

        if (f.isNuevo()) {
            if (clienteDao.existsById(f.getNoDocumento())) {
                throw new IllegalArgumentException("Ya existe un cliente con el documento " + f.getNoDocumento() + ".");
            }
            cliente = new Cliente();
            cliente.setNoDocumento(f.getNoDocumento());
            usuario = new Usuario();
            usuario.setRol(buscarRolCliente());
            usuario.setEstado(true);
            // Sin contraseña: el cliente no entra a este módulo
        } else {
            cliente = clienteDao.findById(f.getNoDocumento())
                    .orElseThrow(() -> new IllegalArgumentException("El cliente ya no existe."));
            usuario = cliente.getUsuario();
        }

        // El correo es único en la tabla usuario
        String correo = f.getCorreo().trim().toLowerCase();
        final Integer idActual = usuario.getIdUsuario();
        usuarioDao.findFirstByCorreoIgnoreCase(correo).ifPresent(otro -> {
            if (!Objects.equals(otro.getIdUsuario(), idActual)) {
                throw new IllegalArgumentException("Ya hay un usuario registrado con el correo " + correo + ".");
            }
        });

        usuario.setTipoDocumento(f.getTipoDocumento().trim());
        usuario.setNombre(f.getNombre().trim());
        usuario.setApellido(f.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setCelular(f.getCelular().trim());

        cliente.setUsuario(usuarioDao.save(usuario));
        clienteDao.save(cliente);
    }

    @Override
    @Transactional
    public void eliminarCliente(Integer noDocumento) {
        Cliente cliente = clienteDao.findById(noDocumento).orElse(null);
        if (cliente == null) {
            return;
        }
        if (vehiculoDao.existsByClienteNoDocumento(noDocumento) || reservaDao.existsByClienteNoDocumento(noDocumento)) {
            throw new IllegalStateException("No se puede eliminar: el cliente tiene vehículos o reservas registradas.");
        }
        Usuario usuario = cliente.getUsuario();
        clienteDao.delete(cliente);
        // Solo se borra el usuario si es un usuario de tipo CLIENTE (nunca un administrador o controlador)
        if (usuario != null && usuario.getRol() != null
                && "CLIENTE".equals(Texto.normalizarRol(usuario.getRol().getNombre()))) {
            usuarioDao.delete(usuario);
        }
    }

    @Override
    public List<Vehiculo> vehiculosDelCliente(Integer noDocumento) {
        return vehiculoDao.findByClienteNoDocumentoOrderByPlacaAsc(noDocumento);
    }

    // Busca el rol CLIENTE que creaste en la base de datos (sin importar mayúsculas o tildes)
    private Rol buscarRolCliente() {
        for (Rol r : rolDao.findAll()) {
            if ("CLIENTE".equals(Texto.normalizarRol(r.getNombre()))) {
                return r;
            }
        }
        return null; // id_rol acepta NULL en tu script
    }
}

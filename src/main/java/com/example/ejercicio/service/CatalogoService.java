package com.example.ejercicio.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejercicio.dao.MetodoPagoDao;
import com.example.ejercicio.dao.ReservaDao;
import com.example.ejercicio.dao.ServicioLavadoDao;
import com.example.ejercicio.dao.ServicioParqueaderoDao;
import com.example.ejercicio.dao.TipoVehiculoDao;
import com.example.ejercicio.dao.VehiculoDao;
import com.example.ejercicio.domain.MetodoPago;
import com.example.ejercicio.domain.ServicioLavado;
import com.example.ejercicio.domain.ServicioParqueadero;
import com.example.ejercicio.domain.TipoVehiculo;
import com.example.ejercicio.util.Texto;

@Service
public class CatalogoService implements ICatalogoService {

    @Autowired
    private TipoVehiculoDao tipoDao;

    @Autowired
    private ServicioParqueaderoDao parqueaderoDao;

    @Autowired
    private ServicioLavadoDao lavadoDao;

    @Autowired
    private MetodoPagoDao metodoPagoDao;

    @Autowired
    private VehiculoDao vehiculoDao;

    @Autowired
    private ReservaDao reservaDao;

    @Autowired
    private Environment env;

    // Si un tipo no tiene su línea en application.properties, usa este número
    @Value("${parqueadero.capacidad.default:20}")
    private int capacidadPorDefecto;

    // ---------- Tipos de vehículo ----------

    @Override
    public List<TipoVehiculo> listarTipos() {
        return tipoDao.findAllByOrderByIdTipoVehiculoAsc();
    }

    @Override
    public List<TipoVehiculo> listarTiposActivos() {
        return listarTipos().stream().filter(TipoVehiculo::isActivo).toList();
    }

    @Override
    public TipoVehiculo buscarTipo(Integer id) {
        return id == null ? null : tipoDao.findById(id).orElse(null);
    }

    @Override
    public TipoVehiculo buscarTipoPorNombre(String nombre) {
        return nombre == null ? null : tipoDao.findFirstByNombreIgnoreCase(nombre.trim()).orElse(null);
    }

    @Override
    public void guardarTipo(TipoVehiculo tipo) {
        tipoDao.save(tipo);
    }

    @Override
    public void eliminarTipo(Integer id) {
        if (parqueaderoDao.existsByTipoVehiculoIdTipoVehiculo(id)
                || lavadoDao.existsByTipoVehiculoIdTipoVehiculo(id)
                || vehiculoDao.existsByTipoVehiculoIdTipoVehiculo(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar: tiene servicios o vehículos asociados. Puedes desactivarlo con Editar.");
        }
        tipoDao.deleteById(id);
    }

    @Override
    public int capacidad(TipoVehiculo tipo) {
        if (tipo == null) return 0;
        String clave = "parqueadero.capacidad." + Texto.slug(tipo.getNombre());
        Integer valor = env.getProperty(clave, Integer.class);
        return valor != null ? Math.max(0, valor) : capacidadPorDefecto;
    }

    // ---------- Servicios de parqueadero ----------

    @Override
    public List<ServicioParqueadero> listarServiciosParqueadero() {
        return parqueaderoDao.findAllByOrderByTipoVehiculoIdTipoVehiculoAscCostoAsc();
    }

    @Override
    public List<ServicioParqueadero> listarServiciosParqueaderoActivos() {
        return listarServiciosParqueadero().stream()
                .filter(s -> s.isActivo() && s.getTipoVehiculo() != null)
                .toList();
    }

    @Override
    public ServicioParqueadero buscarServicioParqueadero(Integer id) {
        return id == null ? null : parqueaderoDao.findById(id).orElse(null);
    }

    @Override
    public void guardarServicioParqueadero(ServicioParqueadero servicio) {
        parqueaderoDao.save(servicio);
    }

    @Override
    @Transactional
    public boolean eliminarServicioParqueadero(Integer id) {
        ServicioParqueadero s = buscarServicioParqueadero(id);
        if (s == null) return true;
        if (reservaDao.existsByServicioParqueaderoIdServicioParqueadero(id)) {
            // Tiene reservas: borrarlo rompería el historial, así que solo se desactiva
            s.setEstado(false);
            parqueaderoDao.save(s);
            return false;
        }
        parqueaderoDao.delete(s);
        return true;
    }

    // ---------- Servicios de lavado ----------

    @Override
    public List<ServicioLavado> listarServiciosLavado() {
        return lavadoDao.findAllByOrderByTipoVehiculoIdTipoVehiculoAscCostoAsc();
    }

    @Override
    public List<ServicioLavado> listarServiciosLavadoActivos() {
        return listarServiciosLavado().stream()
                .filter(s -> s.isActivo() && s.getTipoVehiculo() != null)
                .toList();
    }

    @Override
    public ServicioLavado buscarServicioLavado(Integer id) {
        return id == null ? null : lavadoDao.findById(id).orElse(null);
    }

    @Override
    public void guardarServicioLavado(ServicioLavado servicio) {
        lavadoDao.save(servicio);
    }

    @Override
    @Transactional
    public boolean eliminarServicioLavado(Integer id) {
        ServicioLavado s = buscarServicioLavado(id);
        if (s == null) return true;
        if (reservaDao.existsByServicioLavadoIdServicioLavado(id)) {
            s.setEstado(false);
            lavadoDao.save(s);
            return false;
        }
        lavadoDao.delete(s);
        return true;
    }

    // ---------- Métodos de pago ----------

    @Override
    public List<MetodoPago> listarMetodosPagoActivos() {
        return metodoPagoDao.findAllByOrderByIdMetodoPagoAsc().stream().filter(MetodoPago::isActivo).toList();
    }
}

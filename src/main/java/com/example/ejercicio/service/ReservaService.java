package com.example.ejercicio.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejercicio.dao.ClienteDao;
import com.example.ejercicio.dao.ComprobantePagoDao;
import com.example.ejercicio.dao.MetodoPagoDao;
import com.example.ejercicio.dao.ReservaDao;
import com.example.ejercicio.dao.UsuarioDao;
import com.example.ejercicio.dao.VehiculoDao;
import com.example.ejercicio.domain.Cliente;
import com.example.ejercicio.domain.ComprobantePago;
import com.example.ejercicio.domain.MetodoPago;
import com.example.ejercicio.domain.Reserva;
import com.example.ejercicio.domain.ServicioLavado;
import com.example.ejercicio.domain.ServicioParqueadero;
import com.example.ejercicio.domain.TipoVehiculo;
import com.example.ejercicio.domain.Usuario;
import com.example.ejercicio.domain.Vehiculo;
import com.example.ejercicio.dto.ReservaForm;
import com.example.ejercicio.util.Tiempo;

/**
 * Reglas del negocio para crear una reserva con su pago.
 * Todo se valida AQUÍ (no solo en el navegador) y los precios se toman de la base de datos,
 * nunca del formulario: así nadie puede manipular el total.
 * Guarda en: vehiculo (si la placa es nueva), reserva y comprobante_pago.
 */
@Service
public class ReservaService implements IReservaService {

    private static final Pattern VOUCHER = Pattern.compile("^(?:VCH-?\\d{0,8}-?)?0*(\\d+)$");

    @Autowired
    private ReservaDao reservaDao;

    @Autowired
    private VehiculoDao vehiculoDao;

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private MetodoPagoDao metodoPagoDao;

    @Autowired
    private ComprobantePagoDao comprobanteDao;

    @Autowired
    private ICatalogoService catalogoService;

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listar(String q) {
        if (q == null || q.isBlank()) {
            return conMetodoPago(reservaDao.findAllByOrderByIdReservaDesc());
        }
        String texto = q.trim().toUpperCase();
        List<Reserva> lista = new ArrayList<>(
                reservaDao.findByVehiculoPlacaContainingIgnoreCaseOrderByIdReservaDesc(texto.replaceAll("[^A-Z0-9]", "")));

        // También se puede buscar por voucher: "VCH-20261127-00012" o solo el número "12"
        if (texto.startsWith("VCH") || texto.matches("^\\d+$")) {
            Matcher m = VOUCHER.matcher(texto);
            if (m.matches()) {
                try {
                    Integer id = Integer.valueOf(m.group(1));
                    reservaDao.findById(id).ifPresent(r -> {
                        if (lista.stream().noneMatch(x -> x.getIdReserva().equals(r.getIdReserva()))) {
                            lista.add(0, r);
                        }
                    });
                } catch (NumberFormatException ignorado) {
                    // número demasiado grande: no es un voucher
                }
            }
        }
        return conMetodoPago(lista);
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva buscarPorId(Integer id) {
        Reserva r = id == null ? null : reservaDao.findById(id).orElse(null);
        if (r != null) {
            r.setMetodoPago(comprobanteDao.findFirstByReservaIdReservaOrderByIdComprobantePagoAsc(id)
                    .map(c -> c.getMetodoPago().getNombre()).orElse("—"));
        }
        return r;
    }

    @Override
    public List<Vehiculo> listarVehiculos() {
        return vehiculoDao.findAllByOrderByPlacaAsc();
    }

    @Override
    @Transactional
    public Reserva crear(ReservaForm f, String correoUsuario) {

        // 1) Placa
        String placa = f.getPlaca() == null ? "" : f.getPlaca().trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (!placa.matches("^[A-Z0-9]{5,7}$")) {
            throw error("La placa debe tener entre 5 y 7 letras o números (ej. ABC123).");
        }

        // 2) Tipo de vehículo
        if (f.getIdTipoVehiculo() == null) {
            throw error("Selecciona el tipo de vehículo.");
        }
        TipoVehiculo tipo = catalogoService.buscarTipo(f.getIdTipoVehiculo());
        if (tipo == null || !tipo.isActivo()) {
            throw error("El tipo de vehículo no existe o está inactivo.");
        }

        // 3) Cliente
        if (f.getNoDocumentoCliente() == null) {
            throw error("Selecciona el cliente.");
        }
        Cliente cliente = clienteDao.findById(f.getNoDocumentoCliente())
                .orElseThrow(() -> error("El cliente no existe."));

        // 4) Servicio de parqueadero
        if (f.getIdServicioParqueadero() == null) {
            throw error("Selecciona el servicio de parqueadero.");
        }
        ServicioParqueadero parq = catalogoService.buscarServicioParqueadero(f.getIdServicioParqueadero());
        if (parq == null || !parq.isActivo() || !mismoTipo(parq.getTipoVehiculo(), tipo)) {
            throw error("Ese servicio de parqueadero no corresponde al tipo de vehículo.");
        }

        // 5) Lavado (opcional)
        ServicioLavado lavado = null;
        if (f.getIdServicioLavado() != null) {
            lavado = catalogoService.buscarServicioLavado(f.getIdServicioLavado());
            if (lavado == null || !lavado.isActivo() || !mismoTipo(lavado.getTipoVehiculo(), tipo)) {
                throw error("Ese servicio de lavado no corresponde al tipo de vehículo.");
            }
        }

        // 6) Método de pago
        if (f.getIdMetodoPago() == null) {
            throw error("Selecciona un método de pago.");
        }
        MetodoPago metodo = metodoPagoDao.findById(f.getIdMetodoPago())
                .filter(MetodoPago::isActivo)
                .orElseThrow(() -> error("El método de pago no existe o está inactivo."));

        // 7) Placa ya adentro y cupos
        if (reservaDao.existsByVehiculoPlacaAndEstado(placa, Reserva.ACTIVA)) {
            throw error("La placa " + placa + " ya está dentro del parqueadero.");
        }
        long ocupados = reservaDao.contarPorTipoYEstado(tipo.getIdTipoVehiculo(), Reserva.ACTIVA);
        if (ocupados >= catalogoService.capacidad(tipo)) {
            throw error("No hay cupos disponibles para " + tipo.getNombre() + ".");
        }

        // 8) Total y pago
        BigDecimal total = BigDecimal.valueOf(parq.getCosto())
                .add(lavado != null ? BigDecimal.valueOf(lavado.getCosto()) : BigDecimal.ZERO);
        if (metodo.isEfectivo()
                && (f.getMontoRecibido() == null || f.getMontoRecibido().compareTo(total) < 0)) {
            throw error("El monto recibido es menor al total a pagar.");
        }

        // 9) Usuario que registra (el que inició sesión)
        Usuario registra = usuarioDao.findFirstByCorreoIgnoreCase(correoUsuario)
                .orElseThrow(() -> error("No se encontró el usuario que registra la reserva."));

        // 10) Vehículo: si la placa no existe se crea con el cliente y el tipo elegidos
        Vehiculo vehiculo = vehiculoDao.findById(placa).orElse(null);
        if (vehiculo == null) {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca(placa);
            vehiculo.setCliente(cliente);
            vehiculo.setTipoVehiculo(tipo);
            vehiculo.setEstado(true);
            vehiculo = vehiculoDao.save(vehiculo);
        } else {
            if (!vehiculo.isActivo()) {
                throw error("El vehículo " + placa + " está inactivo.");
            }
            if (vehiculo.getTipoVehiculo() == null) {
                vehiculo.setTipoVehiculo(tipo);
                vehiculo = vehiculoDao.save(vehiculo);
            } else if (!mismoTipo(vehiculo.getTipoVehiculo(), tipo)) {
                throw error("La placa " + placa + " está registrada como " + vehiculo.getTipoVehiculo().getNombre() + ".");
            }
        }

        // 11) Guardar la reserva
        LocalDateTime ahora = Tiempo.ahora().withNano(0);
        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setVehiculo(vehiculo);
        r.setUsuarioRegistra(registra);
        r.setFecha(ahora.toLocalDate());
        r.setHoraEntrada(ahora.toLocalTime());
        r.setHoraSalida(null);
        r.setServicioParqueadero(parq);
        r.setServicioLavado(lavado);
        r.setEstado(Reserva.ACTIVA);
        r = reservaDao.save(r);

        // 12) Comprobante de pago
        ComprobantePago c = new ComprobantePago();
        c.setReserva(r);
        c.setMetodoPago(metodo);
        comprobanteDao.save(c);

        r.setMetodoPago(metodo.getNombre());
        return r;
    }

    @Override
    @Transactional
    public void registrarSalida(Integer id) {
        Reserva r = reservaDao.findById(id).orElseThrow(() -> error("La reserva no existe."));
        if (!r.isActiva()) {
            throw error("La reserva " + r.getCodigoVoucher() + " ya estaba finalizada.");
        }
        LocalDateTime ahora = Tiempo.ahora().withNano(0);
        r.setEstado(Reserva.FINALIZADA);
        // La tabla exige hora_salida > hora_entrada y solo guarda la hora (TIME).
        // Si el vehículo sale otro día, la hora de salida se deja vacía.
        if (ahora.toLocalDate().equals(r.getFecha()) && ahora.toLocalTime().isAfter(r.getHoraEntrada())) {
            r.setHoraSalida(ahora.toLocalTime());
        }
        reservaDao.save(r);
    }

    // Llena el nombre del método de pago leyendo comprobante_pago (una sola consulta)
    private List<Reserva> conMetodoPago(List<Reserva> lista) {
        if (lista.isEmpty()) return lista;
        Map<Integer, String> metodos = new HashMap<>();
        for (ComprobantePago c : comprobanteDao.findByReservaIn(lista)) {
            metodos.putIfAbsent(c.getReserva().getIdReserva(), c.getMetodoPago().getNombre());
        }
        lista.forEach(r -> r.setMetodoPago(metodos.getOrDefault(r.getIdReserva(), "—")));
        return lista;
    }

    private static boolean mismoTipo(TipoVehiculo a, TipoVehiculo b) {
        return a != null && b != null && Objects.equals(a.getIdTipoVehiculo(), b.getIdTipoVehiculo());
    }

    private static IllegalArgumentException error(String msg) {
        return new IllegalArgumentException(msg);
    }
}

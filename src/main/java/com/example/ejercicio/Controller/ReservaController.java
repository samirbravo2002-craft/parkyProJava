package com.example.ejercicio.Controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ejercicio.domain.Reserva;
import com.example.ejercicio.dto.ReservaForm;
import com.example.ejercicio.service.ICatalogoService;
import com.example.ejercicio.service.IClienteService;
import com.example.ejercicio.service.IReservaService;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @Autowired
    private ICatalogoService catalogoService;

    @Autowired
    private IClienteService clienteService;

    // ---------- Historial ----------

    @GetMapping
    public String historial(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("reservas", reservaService.listar(q));
        model.addAttribute("q", q == null ? "" : q);
        return "reservas";
    }

    // El vehículo salió: se finaliza la reserva y se libera el cupo
    @PostMapping("/salida")
    public String salida(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            reservaService.registrarSalida(id);
            ra.addFlashAttribute("ok", "Salida registrada. El cupo quedó libre.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas";
    }

    // ---------- Nueva reserva ----------

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("form", new ReservaForm());
        cargarCatalogos(model);
        return "reserva-nueva";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("form") ReservaForm form, Authentication authentication,
                          Model model, RedirectAttributes ra) {
        try {
            Reserva reserva = reservaService.crear(form, authentication.getName());

            // Datos del pago que solo se muestran en el voucher recién generado
            if (form.getMontoRecibido() != null) {
                BigDecimal cambio = form.getMontoRecibido().subtract(reserva.getTotal());
                ra.addFlashAttribute("montoRecibido", form.getMontoRecibido());
                ra.addFlashAttribute("cambio", cambio.signum() < 0 ? BigDecimal.ZERO : cambio);
            }
            if (form.getReferenciaPago() != null && !form.getReferenciaPago().isBlank()) {
                String ref = form.getReferenciaPago().trim();
                ra.addFlashAttribute("referenciaPago", ref.length() > 120 ? ref.substring(0, 120) : ref);
            }
            ra.addFlashAttribute("ok", "Reserva " + reserva.getCodigoVoucher() + " generada.");
            return "redirect:/reservas/voucher/" + reserva.getIdReserva();
        } catch (IllegalArgumentException e) {
            // Se vuelve a mostrar el formulario con lo que el usuario ya había llenado
            model.addAttribute("error", e.getMessage());
            cargarCatalogos(model);
            return "reserva-nueva";
        }
    }

    // ---------- Voucher ----------

    @GetMapping("/voucher/{id}")
    public String voucher(@PathVariable("id") Integer id, Model model) {
        Reserva reserva = reservaService.buscarPorId(id);
        if (reserva == null) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reserva);
        return "reserva-voucher";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("tipos", catalogoService.listarTiposActivos());
        model.addAttribute("serviciosParqueadero", catalogoService.listarServiciosParqueaderoActivos());
        model.addAttribute("serviciosLavado", catalogoService.listarServiciosLavadoActivos());
        model.addAttribute("metodosPago", catalogoService.listarMetodosPagoActivos());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("vehiculos", reservaService.listarVehiculos());
    }
}

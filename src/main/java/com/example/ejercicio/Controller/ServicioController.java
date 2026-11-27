package com.example.ejercicio.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ejercicio.domain.ServicioLavado;
import com.example.ejercicio.domain.ServicioParqueadero;
import com.example.ejercicio.domain.TipoVehiculo;
import com.example.ejercicio.service.ICatalogoService;

/**
 * Sección "Servicios": tipos de vehículo, servicios de parqueadero y servicios de lavado.
 */
@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ICatalogoService catalogoService;

    @GetMapping
    public String index(Model model) {
        List<TipoVehiculo> tipos = catalogoService.listarTipos();
        Map<Integer, Integer> capacidades = new LinkedHashMap<>();
        for (TipoVehiculo t : tipos) {
            capacidades.put(t.getIdTipoVehiculo(), catalogoService.capacidad(t));
        }
        model.addAttribute("tipos", tipos);
        model.addAttribute("capacidades", capacidades);
        model.addAttribute("serviciosParqueadero", catalogoService.listarServiciosParqueadero());
        model.addAttribute("serviciosLavado", catalogoService.listarServiciosLavado());
        return "servicios";
    }

    // ===================== TIPOS DE VEHÍCULO =====================

    @PostMapping("/tipo/guardar")
    public String guardarTipo(@RequestParam(value = "id", required = false) Integer id,
                              @RequestParam(value = "nombre", required = false) String nombre,
                              @RequestParam(value = "activo", defaultValue = "false") boolean activo,
                              RedirectAttributes ra) {

        if (nombre == null || nombre.isBlank() || nombre.trim().length() > 20) {
            ra.addFlashAttribute("error", "El nombre del tipo de vehículo es obligatorio (máximo 20 caracteres).");
            return "redirect:/servicios";
        }

        TipoVehiculo tipo = (id != null) ? catalogoService.buscarTipo(id) : new TipoVehiculo();
        if (tipo == null) {
            ra.addFlashAttribute("error", "El tipo de vehículo ya no existe.");
            return "redirect:/servicios";
        }
        TipoVehiculo mismoNombre = catalogoService.buscarTipoPorNombre(nombre);
        if (mismoNombre != null && !mismoNombre.getIdTipoVehiculo().equals(tipo.getIdTipoVehiculo())) {
            ra.addFlashAttribute("error", "Ya existe un tipo de vehículo con ese nombre.");
            return "redirect:/servicios";
        }

        tipo.setNombre(nombre.trim());
        tipo.setEstado(activo);
        catalogoService.guardarTipo(tipo);
        ra.addFlashAttribute("ok", "Tipo de vehículo guardado.");
        return "redirect:/servicios";
    }

    @PostMapping("/tipo/eliminar")
    public String eliminarTipo(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            catalogoService.eliminarTipo(id);
            ra.addFlashAttribute("ok", "Tipo de vehículo eliminado.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar: tiene registros asociados.");
        }
        return "redirect:/servicios";
    }

    // ===================== SERVICIOS (parqueadero y lavado) =====================

    @PostMapping("/servicio/guardar")
    public String guardarServicio(@RequestParam(value = "id", required = false) Integer id,
                                  @RequestParam("categoria") String categoria,
                                  @RequestParam(value = "nombre", required = false) String nombre,
                                  @RequestParam(value = "idTipoVehiculo", required = false) Integer idTipoVehiculo,
                                  @RequestParam(value = "tipoCobro", required = false) String tipoCobro,
                                  @RequestParam(value = "descripcion", required = false) String descripcion,
                                  @RequestParam(value = "costo", required = false) Double costo,
                                  @RequestParam(value = "activo", defaultValue = "false") boolean activo,
                                  RedirectAttributes ra) {

        boolean esParqueadero = ICatalogoService.CAT_PARQUEADERO.equals(categoria);
        boolean esLavado = ICatalogoService.CAT_LAVADO.equals(categoria);

        String error = null;
        if (!esParqueadero && !esLavado) {
            error = "Categoría de servicio no válida.";
        } else if (nombre == null || nombre.isBlank() || nombre.trim().length() > 30) {
            error = "El nombre del servicio es obligatorio (máximo 30 caracteres).";
        } else if (descripcion == null || descripcion.isBlank() || descripcion.trim().length() > 200) {
            error = "La descripción es obligatoria (máximo 200 caracteres).";
        } else if (costo == null || costo < 0) {
            error = "El costo debe ser un número igual o mayor que 0.";
        } else if (esParqueadero && (tipoCobro == null || !ICatalogoService.TIPOS_COBRO.contains(tipoCobro))) {
            error = "Selecciona el tipo de cobro (hora, día o mes).";
        }
        TipoVehiculo tipo = catalogoService.buscarTipo(idTipoVehiculo);
        if (error == null && tipo == null) {
            error = "Selecciona un tipo de vehículo válido.";
        }
        if (error != null) {
            ra.addFlashAttribute("error", error);
            return "redirect:/servicios";
        }

        if (esParqueadero) {
            ServicioParqueadero s = (id != null) ? catalogoService.buscarServicioParqueadero(id) : new ServicioParqueadero();
            if (s == null) {
                ra.addFlashAttribute("error", "El servicio ya no existe.");
                return "redirect:/servicios";
            }
            s.setNombre(nombre.trim());
            s.setDescripcion(descripcion.trim());
            s.setTipoCobro(tipoCobro);
            s.setCosto(costo);
            s.setTipoVehiculo(tipo);
            s.setEstado(activo);
            catalogoService.guardarServicioParqueadero(s);
        } else {
            ServicioLavado s = (id != null) ? catalogoService.buscarServicioLavado(id) : new ServicioLavado();
            if (s == null) {
                ra.addFlashAttribute("error", "El servicio ya no existe.");
                return "redirect:/servicios";
            }
            s.setNombre(nombre.trim());
            s.setDescripcion(descripcion.trim());
            s.setCosto(costo);
            s.setTipoVehiculo(tipo);
            s.setEstado(activo);
            catalogoService.guardarServicioLavado(s);
        }
        ra.addFlashAttribute("ok", "Servicio guardado.");
        return "redirect:/servicios";
    }

    @PostMapping("/servicio/eliminar")
    public String eliminarServicio(@RequestParam("id") Integer id,
                                   @RequestParam("categoria") String categoria,
                                   RedirectAttributes ra) {
        try {
            boolean eliminado = ICatalogoService.CAT_PARQUEADERO.equals(categoria)
                    ? catalogoService.eliminarServicioParqueadero(id)
                    : catalogoService.eliminarServicioLavado(id);
            ra.addFlashAttribute("ok", eliminado
                    ? "Servicio eliminado."
                    : "El servicio tiene reservas registradas: se desactivó en lugar de eliminarse.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar: el servicio tiene registros asociados.");
        }
        return "redirect:/servicios";
    }
}

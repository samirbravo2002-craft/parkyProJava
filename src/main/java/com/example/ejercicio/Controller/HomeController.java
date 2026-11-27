package com.example.ejercicio.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ejercicio.domain.Cliente;
import com.example.ejercicio.domain.Limpiador;
import com.example.ejercicio.dto.ClienteForm;
import com.example.ejercicio.service.IClienteService;
import com.example.ejercicio.service.ILimpiadorService;

import jakarta.validation.Valid;

/**
 * Sección "Agregar": clientes y limpiadores (ver, crear, editar, eliminar).
 * El usuarioActual y el dashboard los agrega ModeloGlobal a todas las pantallas.
 */
@Controller
public class HomeController {

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private ILimpiadorService limpiadorService;

    // Página de inicio después del login
    @GetMapping("/")
    public String index() {
        return "redirect:/reservas/nueva";
    }

    @GetMapping("/agregar")
    public String agregar(Model model) {
        model.addAttribute("listaClientes", clienteService.listarClientes());
        model.addAttribute("listaLimpiadores", limpiadorService.listarLimpiadores());
        return "agregar";
    }

    // ===================== CLIENTE =====================

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Integer id, Model model) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        if (cliente == null) {
            return "redirect:/agregar";
        }
        model.addAttribute("cliente", cliente);
        model.addAttribute("vehiculos", clienteService.vehiculosDelCliente(id));
        return "detalle";
    }

    @GetMapping("/nuevoCliente")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new ClienteForm());
        return "form";
    }

    @GetMapping("/editarCliente")
    public String editarCliente(@RequestParam("id") Integer id, Model model) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        if (cliente == null) {
            return "redirect:/agregar";
        }
        model.addAttribute("cliente", ClienteForm.desde(cliente));
        return "form";
    }

    // BindingResult debe ir justo después del objeto validado.
    @PostMapping("/guardarCliente")
    public String guardarCliente(@Valid @ModelAttribute("cliente") ClienteForm cliente,
                                 BindingResult resultado, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "form";
        }
        try {
            clienteService.guardarCliente(cliente);
        } catch (IllegalArgumentException e) {
            resultado.reject("cliente", e.getMessage());
            return "form";
        } catch (DataIntegrityViolationException e) {
            resultado.reject("cliente", "No se pudo guardar: revisa que el documento y el correo no estén repetidos.");
            return "form";
        }
        ra.addFlashAttribute("ok", "Cliente guardado.");
        return "redirect:/agregar";
    }

    @PostMapping("/eliminarCliente")
    public String eliminarCliente(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            clienteService.eliminarCliente(id);
            ra.addFlashAttribute("ok", "Cliente eliminado.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar: el cliente tiene registros asociados.");
        }
        return "redirect:/agregar";
    }

    // ===================== LIMPIADOR =====================

    @GetMapping("/detalleLimp")
    public String detalleLimp(@RequestParam("id") Integer id, Model model) {
        Limpiador limpiador = limpiadorService.buscarLimpiadorPorId(id);
        if (limpiador == null) {
            return "redirect:/agregar";
        }
        model.addAttribute("limpiador", limpiador);
        return "detalleLimp";
    }

    @GetMapping("/nuevoLimpiador")
    public String nuevoLimpiador(Model model) {
        model.addAttribute("limpiador", new Limpiador());
        return "formLimp";
    }

    @GetMapping("/editarLimpiador")
    public String editarLimpiador(@RequestParam("id") Integer id, Model model) {
        Limpiador limpiador = limpiadorService.buscarLimpiadorPorId(id);
        if (limpiador == null) {
            return "redirect:/agregar";
        }
        model.addAttribute("limpiador", limpiador);
        return "formLimp";
    }

    @PostMapping("/guardarLimpiador")
    public String guardarLimpiador(@Valid @ModelAttribute("limpiador") Limpiador limpiador,
                                   BindingResult resultado, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "formLimp";
        }
        try {
            limpiadorService.guardarLimpiador(limpiador);
        } catch (IllegalArgumentException e) {
            resultado.rejectValue("correo", "correo", e.getMessage());
            return "formLimp";
        } catch (DataIntegrityViolationException e) {
            resultado.reject("limpiador", "No se pudo guardar: revisa que el correo no esté repetido.");
            return "formLimp";
        }
        ra.addFlashAttribute("ok", "Limpiador guardado.");
        return "redirect:/agregar";
    }

    @PostMapping("/eliminarLimpiador")
    public String eliminarLimpiador(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            limpiadorService.eliminarLimpiador(id);
            ra.addFlashAttribute("ok", "Limpiador eliminado.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar: el limpiador tiene registros asociados.");
        }
        return "redirect:/agregar";
    }

    // Muestra templates/login.html (el archivo debe llamarse en minúscula)
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

package com.garantia_facil.app.controllers;

import com.garantia_facil.app.models.Garantia;
import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.services.GarantiaService;
import com.garantia_facil.app.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class GarantiaController {
    private final GarantiaService garantiaService;
    private final UsuarioService usuarioService;

    public GarantiaController(GarantiaService garantiaService, UsuarioService usuarioService){
        this.garantiaService = garantiaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String inicioPag(Model model, Authentication authentication){
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

        model.addAttribute("usuario", usuario);
        return "home";
    }

    @PreAuthorize("@usuarioService.possuiAssinatura(authentication.name)")
    @GetMapping("/nova-garantia")
    public String form(Model model){
        model.addAttribute("garantia", new Garantia());

        return "nova-garantia";
    }


    @GetMapping("/g/{codigo}")
    public String exibirDetalhes(@PathVariable String codigo, Model model, Authentication authentication){
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        Garantia garantia = garantiaService.buscarPorCodigo(codigo, usuario.getId());

        boolean valida = garantia.getDataValidade().isAfter(LocalDate.now()) || garantia.getDataValidade().isEqual(LocalDate.now());
        boolean possuiAssinatura = (authentication != null && usuarioService.possuiAssinatura(authentication.getName()));

        model.addAttribute("garantia", garantia);
        model.addAttribute("valida", valida);
        model.addAttribute("possuiAssinatura", possuiAssinatura);

        return "detalhe-garantia";
    }

    @PreAuthorize("@usuarioService.possuiAssinatura(authentication.name)")
    @GetMapping("/g/{codigo}/mal-uso")
    public String formMalUso(@PathVariable String codigo, Model model){
        model.addAttribute("codigo", codigo);
        return "form-mal-uso";
    }

    @PreAuthorize("@usuarioService.possuiAssinatura(authentication.name)")
    @PostMapping("/g/{codigo}/mal-uso")
    public String processarMalUso(@PathVariable String codigo, @RequestParam String observacao, Authentication authentication){
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        garantiaService.registrarMalUso(codigo, observacao, usuario.getId());
        return "redirect:/g/" + codigo;
    }

    @PreAuthorize("@usuarioService.possuiAssinatura(authentication.name)")
    @GetMapping("/garantias")
    public String listar(@RequestParam(required = false) String termo, Model model, Authentication authentication){
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        List<Garantia> garantias = garantiaService.listarOuFiltrar(termo, usuario.getId());
        model.addAttribute("garantias", garantias);
        model.addAttribute("termo", termo);
        return "lista-garantias";
    }

    @PreAuthorize("@usuarioService.possuiAssinatura(authentication.name)")
    @PostMapping("/garantias")
    public String salvar(@ModelAttribute("garantia") @Valid Garantia garantia, BindingResult result, Authentication authentication){
        if (result.hasErrors()) {
            return "nova-garantia";
        }

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        Garantia novaGarantia = garantiaService.criarGarantia(garantia, usuario);

        return "redirect:/g/" + novaGarantia.getCodigoGarantia();
    }
}
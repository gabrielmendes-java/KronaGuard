package com.garantia_facil.app.controllers;

import com.garantia_facil.app.models.Garantia;
import com.garantia_facil.app.services.GarantiaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class GarantiaController {
    private final GarantiaService garantiaService;

    public GarantiaController(GarantiaService garantiaService){
        this.garantiaService = garantiaService;
    }

    @GetMapping("/")
    public String inicio(){
        return "redirect:/nova-garantia";
    }

    @GetMapping("/nova-garantia")
    public String form(Model model){
        model.addAttribute("garantia", new Garantia());
        return "nova-garantia";
    }

    @GetMapping("/g/{codigo}")
    public String exibirDetalhes(@PathVariable String codigo, Model model){
        Garantia garantia = garantiaService.buscarPorCodigo(codigo);

        boolean valida = garantia.getDataValidade().isAfter(LocalDate.now()) || garantia.getDataValidade().isEqual(LocalDate.now());
        model.addAttribute("garantia", garantia);
        model.addAttribute("valida", valida);

        return "detalhe-garantia";
    }

    @GetMapping("/g/{codigo}/mal-uso")
    public String formMalUso(@PathVariable String codigo, Model model){
        model.addAttribute("codigo", codigo);
        return "form-mal-uso";
    }

    @PostMapping("/g/{codigo}/mal-uso")
    public String processarMalUso(@PathVariable String codigo, @RequestParam String observacao){
        garantiaService.registrarMalUso(codigo, observacao);
        return "redirect:/g/" + codigo;
    }

    @GetMapping("/garantias")
    public String listar(@RequestParam(required = false) String termo, Model model){
        List<Garantia> garantias = garantiaService.listarOuFiltrar(termo);
        model.addAttribute("garantias", garantias);
        model.addAttribute("termo", termo);
        return "lista-garantias";
    }

    @PostMapping("/garantias")
    public String salvar(@ModelAttribute("garantia") @Valid Garantia garantia, BindingResult result){
        if (result.hasErrors()) {
            return "nova-garantia";
        }
        Garantia novaGarantia = garantiaService.criarGarantia(garantia);
        return "redirect:/g/" + novaGarantia.getCodigoGarantia();
    }
}
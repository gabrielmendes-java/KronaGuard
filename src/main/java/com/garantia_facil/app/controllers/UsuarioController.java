package com.garantia_facil.app.controllers;

import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UsuarioController {
    UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model){
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid Usuario usuario, BindingResult result, Model model){
        if(result.hasErrors()){
            return "cadastro";
        }

        try {
            usuarioService.salvar(usuario);
        }catch (IllegalArgumentException e){
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }

        return "redirect:/login";
    }
}

package com.garantia_facil.app.controllers;

import com.garantia_facil.app.models.Usuario;
import com.garantia_facil.app.services.PasswordResetService;
import com.garantia_facil.app.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final PasswordResetService passwordResetService;

    public UsuarioController(UsuarioService usuarioService, PasswordResetService passwordResetService){
        this.usuarioService = usuarioService;
        this.passwordResetService = passwordResetService;
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

    @GetMapping("/esqueci-senha")
    public String esqueciSenhaGet(){
        return "esqueci-senha";
    }
    @PostMapping("/esqueci-senha")
    public String esqueciSenha(@RequestParam String email){
        System.out.println("Solicitação de recuperação para: " + email);

        passwordResetService.criarToken(email);

        return "redirect:/esqueci-senha?enviado";
    }

    @GetMapping("/redefinir-senha")
    public String redefinirSenhaForm(@RequestParam String token, Model model){
        try{
            passwordResetService.buscarToken(token);

            model.addAttribute("token", token);

            return "redefinir-senha";
        }catch (IllegalArgumentException e){
            return "redirect:/login?tokenInvalido";
        }
    }

    @PostMapping("/redefinir-senha")
    public String redefinirSenha(@RequestParam String token, @RequestParam String senha, @RequestParam String confirmarSenha){
        try{
            if(!senha.equals(confirmarSenha)){
                return "redirect:/redefinir-senha?token=" + token + "&erro=senhas";
            }

            passwordResetService.redefinirSenha(token, senha);

            return "redirect:/login?senhaAlterada";
        }catch (IllegalArgumentException e){
            return "redirect:/login?tokenInvalido";
        }
    }
}

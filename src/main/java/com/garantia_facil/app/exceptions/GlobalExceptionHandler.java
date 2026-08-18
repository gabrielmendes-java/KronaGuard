package com.garantia_facil.app.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null && statusCode == 404){
            model.addAttribute("titulo", "Página não encontrada");
            model.addAttribute("mensagemErro", "O endereço que você tentou acessar não existe ou foi removido.");
        }else {
            model.addAttribute("titulo", "Ops! Algo deu errado");
            model.addAttribute("mensagemErro", "Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.");
        }
        return "erro-garantia";
    }
}

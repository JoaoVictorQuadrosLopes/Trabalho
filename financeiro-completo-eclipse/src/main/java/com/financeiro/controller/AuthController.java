package com.financeiro.controller;

import com.financeiro.model.Usuario;
import com.financeiro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

  
    @GetMapping("/cadastro")
    public String telaCadastro() {
        return "cadastro";
    }

    
    @PostMapping("/cadastro")
    public String salvarCadastro(@ModelAttribute Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return "redirect:/login?sucesso";
    }
}

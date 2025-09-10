package com.financeiro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.financeiro.model.Categoria;
import com.financeiro.model.Transacao;
import com.financeiro.model.Usuario;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.repository.UsuarioRepository;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoRepository transacaoRepo;
    private final CategoriaRepository categoriaRepo;
    private final UsuarioRepository usuarioRepo;

    public TransacaoController(TransacaoRepository transacaoRepo, CategoriaRepository categoriaRepo, UsuarioRepository usuarioRepo) {
        this.transacaoRepo = transacaoRepo;
        this.categoriaRepo = categoriaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/nova")
    public String novaTransacao(Model model) {
        model.addAttribute("transacao", new Transacao());
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "transacao-form";
    }

    @PostMapping("/salvar")
    public String salvarTransacao(@ModelAttribute Transacao transacao,
                                  @RequestParam("categoriaId") Long categoriaId,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepo.findByEmail(userDetails.getUsername()).orElseThrow();
        Categoria categoria = categoriaRepo.findById(categoriaId).orElse(null);
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);
        if (transacao.getData() == null) {
            transacao.setData(LocalDate.now());
        }
        transacaoRepo.save(transacao);
        return "redirect:/";
    }

    @GetMapping("/editar/{id}")
    public String editarTransacao(@PathVariable Long id, Model model) {
        Transacao transacao = transacaoRepo.findById(id).orElseThrow();
        model.addAttribute("transacao", transacao);
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "transacao-form";
    }

    @GetMapping("/excluir/{id}")
    public String excluirTransacao(@PathVariable Long id) {
        transacaoRepo.deleteById(id);
        return "redirect:/";
    }
}

package com.financeiro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.financeiro.model.Transacao;
import com.financeiro.model.Usuario;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.repository.UsuarioRepository;

@Controller
public class HomeController {

    private final TransacaoRepository transacaoRepo;
    private final UsuarioRepository usuarioRepo;

    public HomeController(TransacaoRepository transacaoRepo, UsuarioRepository usuarioRepo) {
        this.transacaoRepo = transacaoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        Usuario usuario = usuarioRepo.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Transacao> transacoes;

        if (inicio != null && fim != null) {
            transacoes = transacaoRepo.findByUsuarioAndDataBetween(usuario, inicio, fim);
        } else {
            transacoes = transacaoRepo.findByUsuario(usuario);
        }

        double receitas = transacoes.stream()
                .filter(t -> t.getTipo() != null && t.getTipo().equals("RECEITA"))
                .mapToDouble(Transacao::getValor)
                .sum();

        double despesas = transacoes.stream()
                .filter(t -> t.getTipo() != null && t.getTipo().equals("DESPESA"))
                .mapToDouble(Transacao::getValor)
                .sum();

        double saldo = receitas - despesas;

        model.addAttribute("transacoes", transacoes);
        model.addAttribute("receitas", receitas);
        model.addAttribute("despesas", despesas);
        model.addAttribute("saldo", saldo);

        return "index";
    }
}

package com.financeiro.service;

import com.financeiro.model.Transacao;
import com.financeiro.model.Usuario;
import com.financeiro.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    /**
     * Lista todas as transações de um usuário
     */
    public List<Transacao> buscarPorUsuario(Usuario usuario) {
        return transacaoRepository.findByUsuario(usuario);
    }

    /**
     * Lista as transações de um usuário em um período específico
     */
    public List<Transacao> buscarPorUsuarioEPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            return transacaoRepository.findByUsuarioAndDataBetween(usuario, inicio, fim);
        }
        return transacaoRepository.findByUsuario(usuario);
    }

    /**
     * Salva ou atualiza uma transação
     */
    public Transacao salvar(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

    /**
     * Exclui uma transação pelo ID
     */
    public void excluir(Long id) {
        transacaoRepository.deleteById(id);
    }

    /**
     * Busca transação pelo ID
     */
    public Transacao buscarPorId(Long id) {
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    /**
     * Lista todas as transações no sistema (admin)
     */
    public List<Transacao> listarTodas() {
        return transacaoRepository.findAll();
    }
}

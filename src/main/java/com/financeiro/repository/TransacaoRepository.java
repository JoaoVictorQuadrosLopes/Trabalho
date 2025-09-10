package com.financeiro.repository;

import com.financeiro.model.Transacao;
import com.financeiro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuario(Usuario usuario);

    List<Transacao> findByUsuarioAndDataBetween(Usuario usuario, LocalDate inicio, LocalDate fim);

	List<Transacao> findByDataBetween(LocalDate inicio, LocalDate fim);
}

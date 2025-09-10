package com.financeiro.controller;

import com.financeiro.model.Transacao;
import com.financeiro.repository.TransacaoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
public class RelatorioController {

    @Autowired
    private TransacaoRepository transacaoRepository;

    // Exportar relatório por data 
    @GetMapping("/relatorio/exportar")
    public void exportarRelatorio(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            HttpServletResponse response) throws IOException {

        List<Transacao> transacoes = transacaoRepository.findByDataBetween(inicio, fim);

        gerarExcel(transacoes, response, "relatorio_" + inicio + "_a_" + fim + ".xlsx");
    }

    
    @GetMapping("/relatorio/periodo")
    public void relatorioPorPeriodo(
            @RequestParam("tipo") String tipo,
            HttpServletResponse response) throws IOException {

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = null;
        LocalDate fim = null;

        switch (tipo.toLowerCase()) {
            case "semana":
                inicio = hoje.with(java.time.DayOfWeek.MONDAY);
                fim = hoje.with(java.time.DayOfWeek.SUNDAY);
                break;
            case "mes":
                inicio = hoje.with(TemporalAdjusters.firstDayOfMonth());
                fim = hoje.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "ano":
                inicio = hoje.with(TemporalAdjusters.firstDayOfYear());
                fim = hoje.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                inicio = hoje.minusMonths(1);
                fim = hoje;
        }

        List<Transacao> transacoes = transacaoRepository.findByDataBetween(inicio, fim);

        gerarExcel(transacoes, response, "relatorio_" + tipo + ".xlsx");
    }

   // aqui vai gerar excel, pdf é dificil demais
    private void gerarExcel(List<Transacao> transacoes, HttpServletResponse response, String nomeArquivo) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transações");

     
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Descrição");
        header.createCell(1).setCellValue("Valor");
        header.createCell(2).setCellValue("Tipo");
        header.createCell(3).setCellValue("Categoria");
        header.createCell(4).setCellValue("Data");

      
        int rowNum = 1;
        for (Transacao t : transacoes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getDescricao());
            row.createCell(1).setCellValue(t.getValor().doubleValue());
            row.createCell(2).setCellValue(t.getTipo().toString());
            row.createCell(3).setCellValue(t.getCategoria() != null ? t.getCategoria().getNome() : "Sem categoria");
            row.createCell(4).setCellValue(t.getData().toString());
        }

        // Configuração do response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + nomeArquivo);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

package com.example.demo.controllers;

import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.PdfChartExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/reportes")
public class ChartReportController {

    private final ProductRepository productRepository;
    private final PdfChartExportService pdfChartExportService;

    public ChartReportController(ProductRepository productRepository,
                                 PdfChartExportService pdfChartExportService) {
        this.productRepository = productRepository;
        this.pdfChartExportService = pdfChartExportService;
    }

    @GetMapping("/graficos")
    public void exportarConGraficos(HttpServletResponse response) throws IOException {
        var productos = productRepository.findAll();
        pdfChartExportService.exportarGraficos(response, productos);
    }
}

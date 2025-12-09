package com.example.demo.service;

import com.example.demo.models.Product;

// iText imports correctos
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

@Service
public class PdfChartExportService {

    public void exportarGraficos(HttpServletResponse response, List<Product> productos) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_graficos.pdf");

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Logo centrado 🔥
            try {
                ClassPathResource logoFile = new ClassPathResource("static/img/logo.png");
                Image logo = Image.getInstance(logoFile.getURL());
                logo.scaleToFit(110, 110);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.setSpacingAfter(18);
                document.add(logo);
            } catch (Exception ignored) {}

            // Título elegante
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.RED);
            Paragraph titulo = new Paragraph("📊 Reporte Gráfico de Productos", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            if (productos.isEmpty()) {
                document.add(new Paragraph("No hay datos disponibles."));
                document.close();
                return;
            }

            // === 📍 GRÁFICO BARRAS ===
            DefaultCategoryDataset datasetBarras = new DefaultCategoryDataset();
            productos.forEach(p -> datasetBarras.addValue(p.getStock(), "Stock", p.getName()));

            JFreeChart chartBarras = ChartFactory.createBarChart(
                    "Stock por Producto", "Producto", "Cantidad en Stock", datasetBarras);

            chartBarras.setBackgroundPaint(Color.WHITE);

            BarRenderer renderer = (BarRenderer) chartBarras.getCategoryPlot().getRenderer();
            renderer.setSeriesPaint(0, Color.BLACK);
            chartBarras.getCategoryPlot().getRangeAxis().setLabelPaint(Color.RED);
            chartBarras.getTitle().setPaint(Color.RED);

            BufferedImage barrasImage = chartBarras.createBufferedImage(700, 380);
            Image barrasPDF = Image.getInstance(writer, barrasImage, 1f);
            barrasPDF.setAlignment(Element.ALIGN_CENTER);
            barrasPDF.setSpacingAfter(35);
            document.add(barrasPDF);

            // === 📍 GRÁFICO PASTEL — Estilo Moderno  ===
            DefaultPieDataset<String> datasetPastel = new DefaultPieDataset<>();
            productos.forEach(p -> datasetPastel.setValue(p.getName(), p.getCantidadVendida()));

            JFreeChart chartPastel = ChartFactory.createPieChart(
                    "Ventas por Producto", datasetPastel, true, true, false);

            PiePlot piePlot = (PiePlot) chartPastel.getPlot();
            piePlot.setBackgroundPaint(Color.WHITE);
            piePlot.setCircular(true);
            piePlot.setLabelBackgroundPaint(Color.BLACK);
            piePlot.setLabelPaint(Color.WHITE);
            piePlot.setOutlinePaint(Color.RED);
            chartPastel.getTitle().setPaint(Color.RED);

            BufferedImage pastelImage = chartPastel.createBufferedImage(700, 380);
            Image pastelPDF = Image.getInstance(writer, pastelImage, 1f);
            pastelPDF.setAlignment(Element.ALIGN_CENTER);
            pastelPDF.setSpacingAfter(20);
            document.add(pastelPDF);

            document.close();

        } catch (Exception e) {
            System.out.println("❌ Error al generar PDF gráfico: " + e.getMessage());
        }
    }
}

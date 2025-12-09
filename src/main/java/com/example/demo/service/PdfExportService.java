package com.example.demo.service;

import com.example.demo.models.Product;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportService {

    public void exportarProductosA(HttpServletResponse response, List<Product> productos) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_productos_filtrados.pdf");

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Logo centrado
            try {
                Image logo = Image.getInstance(new ClassPathResource("static/img/logo.png").getURL());
                logo.scaleToFit(90, 90);
                logo.setAlignment(Image.ALIGN_CENTER);
                logo.setSpacingAfter(12);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("⚠ No se pudo cargar el logo: " + e.getMessage());
            }

            // Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.RED);
            Paragraph titulo = new Paragraph("📌 Reporte de Productos Filtrados", fontTitulo);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            document.add(titulo);

            // Si no hay productos
            if (productos.isEmpty()) {
                Paragraph sinDatos = new Paragraph("No hay productos que coincidan con el filtro.");
                sinDatos.setAlignment(Element.ALIGN_CENTER);
                document.add(sinDatos);
                document.close();
                return;
            }

            // Tabla
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10);
            tabla.setWidths(new float[]{4f, 2f, 2f});

            // Encabezados
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            String[] headers = {"Nombre", "Stock", "Cantidad Vendida"};
            PdfPCell celda;

            for (String h : headers) {
                celda = new PdfPCell(new Phrase(h, fontHeader));
                celda.setBackgroundColor(Color.BLACK);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                tabla.addCell(celda);
            }

            // Filas
            Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            for (Product p : productos) {
                tabla.addCell(new Phrase(p.getName(), fontBody));
                tabla.addCell(new Phrase(String.valueOf(p.getStock()), fontBody));
                tabla.addCell(new Phrase(String.valueOf(p.getCantidadVendida()), fontBody));
            }

            document.add(tabla);

            // Pie de página
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);
            Paragraph footer = new Paragraph("\nReporte generado automáticamente © 2024", fontFooter);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            throw new IOException("Error al generar PDF: " + e.getMessage());
        }
    }
}

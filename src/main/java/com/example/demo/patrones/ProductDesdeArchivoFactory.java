package com.example.demo.patrones;

import com.example.demo.models.Product;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDesdeArchivoFactory {

    public List<Product> crearDesdeCsv(InputStream inputStream) throws IOException {
        List<Product> productos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String first = br.readLine();
            if (first == null) return productos;

            char delimiter = detectarDelimitador(first);
            boolean encabezado = esEncabezado(first);

            // Si la primera línea NO es encabezado, procesarla también
            if (!encabezado) {
                Product p = parseLinea(first, delimiter);
                if (p != null) productos.add(p);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                Product p = parseLinea(line, delimiter);
                if (p != null) productos.add(p);
            }
        }

        return productos;
    }

    private boolean esEncabezado(String line) {
        String lower = line.toLowerCase();
        return lower.contains("name") || lower.contains("precio") || lower.contains("price");
    }

    private char detectarDelimitador(String line) {
        if (line.contains(";")) return ';';
        if (line.contains("\t")) return '\t';
        return ','; // por defecto
    }

    private Product parseLinea(String line, char delimiter) {
        // Split simple respetando comillas dobles (soporte básico)
        List<String> cols = splitRespetandoComillas(line, delimiter);

        // Esperamos: name, price, description, image, stock, cantidadVendida
        if (cols.size() < 5) return null; // muy pocos datos

        Product p = new Product();

        // name
        p.setName(safeStr(cols, 0));

        // price
        p.setPrice(safeDouble(cols, 1, 0.0));

        // description
        p.setDescription(safeStr(cols, 2));

        // image (opcional)
        p.setImage(safeStr(cols, 3));

        // stock
        p.setStock(safeInt(cols, 4, 0));

        // cantidadVendida (opcional → por defecto 0)
        p.setCantidadVendida(safeInt(cols, 5, 0));

        return p;
    }

    private List<String> splitRespetandoComillas(String line, char delimiter) {
        List<String> out = new ArrayList<>();
        StringBuilder curr = new StringBuilder();
        boolean enComillas = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                enComillas = !enComillas;
                continue;
            }
            if (c == delimiter && !enComillas) {
                out.add(curr.toString().trim());
                curr.setLength(0);
            } else {
                curr.append(c);
            }
        }
        out.add(curr.toString().trim());
        return out;
    }

    private String safeStr(List<String> cols, int idx) {
        return idx < cols.size() ? cols.get(idx).trim() : null;
    }

    private Integer safeInt(List<String> cols, int idx, int def) {
        try {
            return idx < cols.size() && !cols.get(idx).isBlank() ? Integer.parseInt(cols.get(idx).trim()) : def;
        } catch (Exception e) {
            return def;
        }
    }

    private Double safeDouble(List<String> cols, int idx, double def) {
        try {
            String v = idx < cols.size() ? cols.get(idx).trim() : "";
            if (v.isBlank()) return def;
            v = v.replace(",", "."); // por si usan coma decimal
            return Double.parseDouble(v);
        } catch (Exception e) {
            return def;
        }
    }
}

package com.example.demo.controllers;

import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.PdfExportService;
import com.example.demo.service.ProductService;
import com.example.demo.service.PdfChartExportService;
import com.example.demo.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private PdfChartExportService pdfChartExportService;

    @Autowired
    private EmailService emailService;

    // Mostrar panel admin con filtros aplicados
    @GetMapping("/admin-panel")
    public String mostrarPanel(HttpSession session,
                               Model model,
                               @RequestParam(required = false) String ordenarPor,
                               @RequestParam(required = false) String orden,
                               @RequestParam(required = false) String nombre,
                               @RequestParam(required = false) Integer cantidad) {

        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        List<Product> productos = productService.obtenerProductosFiltrados(ordenarPor, orden, nombre, cantidad);
        model.addAttribute("productos", productos);
        model.addAttribute("usuarios", userRepository.findAll());
        model.addAttribute("ordenarPor", ordenarPor);
        model.addAttribute("orden", orden);
        model.addAttribute("nombre", nombre);
        model.addAttribute("cantidad", cantidad);

        return "admin-panel";
    }

    // Exportar productos filtrados a PDF (listado)
    @GetMapping("/admin-panel/exportar-pdf")
    public void exportarReporteProductos(HttpServletResponse response,
                                         HttpSession session,
                                         @RequestParam(required = false) String ordenarPor,
                                         @RequestParam(required = false) String orden,
                                         @RequestParam(required = false) String nombre,
                                         @RequestParam(required = false) Integer cantidad) throws IOException {

        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            response.sendRedirect("/");
            return;
        }

        // Obtener los productos con los mismos filtros del panel
        List<Product> productosFiltrados = productService.obtenerProductosFiltrados(ordenarPor, orden, nombre, cantidad);

        // Exportar solo los productos filtrados
        pdfExportService.exportarProductosA(response, productosFiltrados);
    }

    // Exportar gráficos filtrados a PDF
    @GetMapping("/admin-panel/exportar-graficos")
    public void exportarGraficosProductos(HttpServletResponse response,
                                          HttpSession session,
                                          @RequestParam(required = false) String ordenarPor,
                                          @RequestParam(required = false) String orden,
                                          @RequestParam(required = false) String nombre,
                                          @RequestParam(required = false) Integer cantidad) throws IOException {

        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            response.sendRedirect("/");
            return;
        }

        // Aplicar los mismos filtros
        List<Product> productosFiltrados = productService.obtenerProductosFiltrados(ordenarPor, orden, nombre, cantidad);

        // Exportar solo los productos filtrados
        pdfChartExportService.exportarGraficos(response, productosFiltrados);
    }

    // Importar CSV con reporte
    @PostMapping("/products/upload")
    public String uploadProductosCsv(@RequestParam("file") MultipartFile file,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        try {
            int duplicados = productService.importarProductosCsvConReporte(file);
            if (duplicados > 0) {
                ra.addFlashAttribute("mensaje",
                        "Productos importados correctamente. " + duplicados + " ya existían y no fueron cargados.");
            } else {
                ra.addFlashAttribute("mensaje", "Productos importados correctamente. No hubo duplicados.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al importar: " + e.getMessage());
        }

        return "redirect:/admin-panel";
    }

/*     // Notificación de stock bajo
    @PostMapping("/admin-panel/notificar-stock-bajo")
    public String notificarStockBajo(HttpSession session, RedirectAttributes ra) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null || !"admin".equals(usuario.getRole())) {
            return "redirect:/";
        }

        try {
            emailService.enviarAlertaStockBajoAdmins();
            ra.addFlashAttribute("mensaje", "Correos enviados a los administradores.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al enviar correos: " + e.getMessage());
        }

        return "redirect:/admin-panel";
    } */
}

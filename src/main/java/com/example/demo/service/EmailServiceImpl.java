package com.example.demo.service;

import com.example.demo.models.Pedido;
import com.example.demo.models.PedidoItem;
import com.example.demo.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String fromName;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${app.mail.from-email}") String fromEmail,
                            @Value("${app.mail.from-name:SegurityConnect}") String fromName) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    @Override
    public void sendOrderConfirmation(User user, Pedido pedido) throws Exception {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("El usuario o su correo son nulos");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail, fromName);
            helper.setSubject("Confirmación de pedido #" + pedido.getId());

            String html = buildOrderHtml(user, pedido);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            // Re-lanzar excepción para que el caller decida (o hacer log)
            throw new Exception("Error enviando correo de confirmación: " + ex.getMessage(), ex);
        }
    }

    private String buildOrderHtml(User user, Pedido pedido) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withLocale(Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Gracias por tu compra, ").append(escape(user.getName())).append("!</h2>");
        sb.append("<p>Pedido <strong>#").append(pedido.getId()).append("</strong> - Fecha: ")
          .append(pedido.getFecha() != null ? pedido.getFecha().format(fmt) : "").append("</p>");

        sb.append("<h3>Resumen del pedido</h3>");
        sb.append("<table style='width:100%; border-collapse:collapse;'>");
        sb.append("<thead><tr>")
          .append("<th style='text-align:left; border-bottom:1px solid #ddd;'>Producto</th>")
          .append("<th style='text-align:right; border-bottom:1px solid #ddd;'>Precio unit.</th>")
          .append("<th style='text-align:right; border-bottom:1px solid #ddd;'>Cantidad</th>")
          .append("<th style='text-align:right; border-bottom:1px solid #ddd;'>Subtotal</th>")
          .append("</tr></thead><tbody>");

        if (pedido.getItems() != null) {
            for (PedidoItem it : pedido.getItems()) {
                String productName = it.getProduct() != null ? escape(it.getProduct().getName()) : "Producto";
                double subtotal = (it.getPrecioUnitario() != null ? it.getPrecioUnitario() : 0) * (it.getCantidad() != null ? it.getCantidad() : 0);
                sb.append("<tr>")
                  .append("<td style='padding:6px 0;'>").append(productName).append("</td>")
                  .append("<td style='text-align:right;'>$").append(String.format("%.2f", it.getPrecioUnitario())).append("</td>")
                  .append("<td style='text-align:right;'>").append(it.getCantidad()).append("</td>")
                  .append("<td style='text-align:right;'>$").append(String.format("%.2f", subtotal)).append("</td>")
                  .append("</tr>");
            }
        }

        sb.append("</tbody></table>");

        sb.append("<p><strong>Total: $").append(String.format("%.2f", pedido.getTotal() != null ? pedido.getTotal() : 0.0)).append("</strong></p>");

        sb.append("<h4>Datos de envío</h4>");
        sb.append("<p>Contacto: ").append(escape(pedido.getContacto())).append("<br>");
        sb.append("Dirección: ").append(escape(pedido.getDireccion())).append("</p>");

        sb.append("<hr>");
        sb.append("<p>Si tienes preguntas, responde este correo o visita nuestra web.</p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
    
    @Override
    public void sendCustomEmail(String[] recipients, String subject, String htmlMessage) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(new InternetAddress(fromEmail, fromName));
        helper.setSubject(subject);

        // Añadir múltiples BCC (ocultos entre sí)
        for (String r : recipients) {
            if (r != null && !r.trim().isEmpty()) {
                helper.addBcc(r.trim());
            }
        }

        helper.setText(htmlMessage, true);

        mailSender.send(message);
    }
    
}

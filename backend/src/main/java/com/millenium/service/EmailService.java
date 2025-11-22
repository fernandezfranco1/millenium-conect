package com.millenium.service;

import com.millenium.model.Producto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${mail.to.admin:admin@millenium-conect.com}")
    private String adminEmail;
    
    @Value("${spring.mail.username:#{null}}")
    private String fromEmail;
    
    public void sendLowStockAlert(Producto producto) {
        // Verificar si las credenciales de email están configuradas
        if (fromEmail == null || fromEmail.isEmpty()) {
            System.out.println("⚠️ Email no configurado. Alerta de stock no enviada para: " + producto.getNombre());
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("⚠️ Alerta de Stock Bajo - " + producto.getNombre());
            
            String htmlContent = loadEmailTemplate(producto);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            System.out.println("✅ Email de alerta de stock enviado para: " + producto.getNombre());
            
        } catch (MessagingException | IOException e) {
            System.err.println("❌ Error al enviar email de alerta de stock: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String loadEmailTemplate(Producto producto) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/low-stock-alert.html");
        String template = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        
        // Reemplazar variables en la plantilla
        template = template.replace("${productName}", producto.getNombre());
        template = template.replace("${productBrand}", producto.getMarca() != null ? producto.getMarca() : "N/A");
        template = template.replace("${productColor}", producto.getColor() != null ? producto.getColor() : "N/A");
        template = template.replace("${currentStock}", String.valueOf(producto.getStock()));
        template = template.replace("${productPrice}", String.format("%.2f", producto.getPrecioVenta()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        template = template.replace("${timestamp}", LocalDateTime.now().format(formatter));
        
        return template;
    }
}

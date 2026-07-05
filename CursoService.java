package cl.duoc.ejemplo.microservicio.controller;

import cl.duoc.ejemplo.microservicio.service.ProductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductorController {

    @Autowired
    private ProductorService productorService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        productorService.sendMessage(message);
        return "Mensaje enviado: " + message;
    }
}
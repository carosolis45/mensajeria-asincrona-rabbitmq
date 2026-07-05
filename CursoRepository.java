package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.InscripcionRequest;
import cl.duoc.ejemplo.microservicio.dto.InscripcionResponse;
import cl.duoc.ejemplo.microservicio.entity.Curso;
import cl.duoc.ejemplo.microservicio.service.CursoService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {
    
    private final CursoService cursoService;
    
    public InscripcionController(CursoService cursoService) {
        this.cursoService = cursoService;
    }
    
    @PostMapping
    public InscripcionResponse inscribir(@RequestBody InscripcionRequest request) {
        InscripcionResponse response = new InscripcionResponse();
        response.setEstudiante(request.getEstudiante());
        
        List<InscripcionResponse.DetalleCurso> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (Long id : request.getCursosIds()) {
            Curso curso = cursoService.buscarPorId(id);
            if (curso != null) {
                InscripcionResponse.DetalleCurso detalle = new InscripcionResponse.DetalleCurso();
                detalle.setNombre(curso.getNombre());
                detalle.setInstructor(curso.getInstructor());
                detalle.setDuracion(curso.getDuracion());
                detalle.setCosto(curso.getCosto());
                detalles.add(detalle);
                total = total.add(curso.getCosto());
            }
        }
        
        response.setCursos(detalles);
        response.setTotal(total);
        return response;
    }
}

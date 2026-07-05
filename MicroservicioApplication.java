package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.entity.Curso;
import cl.duoc.ejemplo.microservicio.service.CursoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {
    
    private final CursoService cursoService;
    
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }
    
    @GetMapping
    public List<Curso> listarCursos() {
        return cursoService.listarTodos();
    }
    
    @PostMapping
    public Curso agregarCurso(@RequestBody Curso curso) {
        return cursoService.guardar(curso);
    }
}

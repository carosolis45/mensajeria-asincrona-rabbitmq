package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.InscripcionRequest;
import cl.duoc.ejemplo.microservicio.dto.InscripcionResponse;
import cl.duoc.ejemplo.microservicio.dto.ResumenInscripcionDTO;
import cl.duoc.ejemplo.microservicio.service.CursoService;
import cl.duoc.ejemplo.microservicio.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/generar-resumen")
    public ResponseEntity<String> generarResumen(@RequestBody InscripcionRequest request) {
        InscripcionResponse inscripcion = inscribir(request);
        String contenido = generarTextoResumen(inscripcion);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resumen_" + request.getEstudiante() + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(contenido);
    }

    @PostMapping("/subir")
    public ResponseEntity<String> subirResumenS3(@RequestBody ResumenInscripcionDTO resumen) {
        String contenido = generarTextoResumenDesdeDTO(resumen);
        String carpeta = String.valueOf(System.currentTimeMillis());
        String nombreArchivo = "resumen_" + resumen.getEstudiante() + ".txt";
        
        s3Service.subirArchivo(carpeta, nombreArchivo, contenido.getBytes());
        
        return ResponseEntity.ok("Archivo subido exitosamente a carpeta: " + carpeta);
    }

    @PutMapping("/modificar")
    public ResponseEntity<String> modificarArchivoS3(@RequestParam String carpeta,
                                                      @RequestParam String nombreArchivo,
                                                      @RequestBody String nuevoContenido) {
        if (s3Service.archivoExiste(carpeta, nombreArchivo)) {
            s3Service.modificarArchivo(carpeta, nombreArchivo, nuevoContenido.getBytes());
            return ResponseEntity.ok("Archivo modificado exitosamente");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/descargar")
    public ResponseEntity<byte[]> descargarArchivoS3(@RequestParam String carpeta,
                                                      @RequestParam String nombreArchivo) {
        if (s3Service.archivoExiste(carpeta, nombreArchivo)) {
            byte[] contenido = s3Service.descargarArchivo(carpeta, nombreArchivo);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(contenido);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarArchivoS3(@RequestParam String carpeta,
                                                     @RequestParam String nombreArchivo) {
        if (s3Service.archivoExiste(carpeta, nombreArchivo)) {
            s3Service.eliminarArchivo(carpeta, nombreArchivo);
            return ResponseEntity.ok("Archivo eliminado exitosamente");
        }
        return ResponseEntity.notFound().build();
    }

    private String generarTextoResumen(InscripcionResponse inscripcion) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE INSCRIPCIÓN ===\n");
        sb.append("Estudiante: ").append(inscripcion.getEstudiante()).append("\n");
        sb.append("Fecha: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        sb.append("\n--- Cursos Inscritos ---\n");
        
        for (InscripcionResponse.DetalleCurso curso : inscripcion.getCursos()) {
            sb.append("Curso: ").append(curso.getNombre()).append("\n");
            sb.append("Instructor: ").append(curso.getInstructor()).append("\n");
            sb.append("Duración: ").append(curso.getDuracion()).append("\n");
            sb.append("Costo: $").append(curso.getCosto()).append("\n");
            sb.append("------------------------\n");
        }
        
        sb.append("\nTOTAL A PAGAR: $").append(inscripcion.getTotal()).append("\n");
        sb.append("=============================\n");
        return sb.toString();
    }

    private String generarTextoResumenDesdeDTO(ResumenInscripcionDTO resumen) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE INSCRIPCIÓN ===\n");
        sb.append("Estudiante: ").append(resumen.getEstudiante()).append("\n");
        sb.append("Fecha: ").append(resumen.getFecha()).append("\n");
        sb.append("\n--- Cursos Inscritos ---\n");
        
        for (ResumenInscripcionDTO.CursoDTO curso : resumen.getCursos()) {
            sb.append("Curso: ").append(curso.getNombre()).append("\n");
            sb.append("Instructor: ").append(curso.getInstructor()).append("\n");
            sb.append("Duración: ").append(curso.getDuracion()).append("\n");
            sb.append("Costo: $").append(curso.getCosto()).append("\n");
            sb.append("------------------------\n");
        }
        
        sb.append("\nTOTAL A PAGAR: $").append(resumen.getTotal()).append("\n");
        sb.append("=============================\n");
        return sb.toString();
    }

    private InscripcionResponse inscribir(InscripcionRequest request) {
        InscripcionResponse response = new InscripcionResponse();
        response.setEstudiante(request.getEstudiante());
        
        List<InscripcionResponse.DetalleCurso> detalles = new ArrayList<>();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        
        for (Long id : request.getCursosIds()) {
            var curso = cursoService.buscarPorId(id);
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
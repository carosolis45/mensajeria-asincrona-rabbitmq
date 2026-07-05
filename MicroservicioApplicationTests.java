package cl.duoc.ejemplo.microservicio.dto;

import java.math.BigDecimal;
import java.util.List;

public class InscripcionResponse {
    private String estudiante;
    private List<DetalleCurso> cursos;
    private BigDecimal total;
    
    public String getEstudiante() { return estudiante; }
    public void setEstudiante(String estudiante) { this.estudiante = estudiante; }
    public List<DetalleCurso> getCursos() { return cursos; }
    public void setCursos(List<DetalleCurso> cursos) { this.cursos = cursos; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public static class DetalleCurso {
        private String nombre;
        private String instructor;
        private String duracion;
        private BigDecimal costo;
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getInstructor() { return instructor; }
        public void setInstructor(String instructor) { this.instructor = instructor; }
        public String getDuracion() { return duracion; }
        public void setDuracion(String duracion) { this.duracion = duracion; }
        public BigDecimal getCosto() { return costo; }
        public void setCosto(BigDecimal costo) { this.costo = costo; }
    }
}

package tpe.model;

import java.util.Objects;

public class Tarea {
    private String nombre;
    private String ID;
    private Integer tiempoDeEjecucion;
    private boolean critica;
    private Integer prioridad;

    public Tarea(String nombre, String ID, Integer tiempoDeEjecucion, boolean critica, Integer prioridad) {
        this.nombre = nombre;
        this.ID = ID;
        this.tiempoDeEjecucion = tiempoDeEjecucion;
        this.critica = critica;
        this.prioridad = prioridad;
    }

    public String getID() {
        return ID;
    }

    public Integer getTiempoDeEjecucion() {
        return tiempoDeEjecucion;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    

    public void setTiempoDeEjecucion(Integer tiempoDeEjecucion) {
        this.tiempoDeEjecucion = tiempoDeEjecucion;
    }

    public boolean isCritica() {
        return critica;
    }

    public void setCritica(boolean critica) {
        this.critica = critica;
    }

    public void setPrioridad(Integer prioridad) {
        if((prioridad >= 1) && (prioridad <= 100))
            this.prioridad = prioridad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarea tarea)) return false;
        return Objects.equals(getID(), tarea.getID());
    }

/*    @Override
    public String toString() {
        return "Tarea{" +
                "nombre='" + nombre + '\'' +
                ", ID='" + ID + '\'' +
                ", tiempoDeEjecucion=" + tiempoDeEjecucion +
                ", critica=" + critica +
                ", prioridad=" + prioridad +
                '}';
    }*/
    @Override
    public String toString() {
        return nombre;
    }
}

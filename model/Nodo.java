package tpe.model;

public class Nodo {
    private Tarea tarea;
    private Nodo menor;
    private Nodo mayor;

    public Nodo(Tarea tarea) {
        this.tarea = tarea;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public Nodo getMenor() {
        return menor;
    }

    public void setMenor(Nodo menor) {
        this.menor = menor;
    }

    public Nodo getMayor() {
        return mayor;
    }

    public void setMayor(Nodo mayor) {
        this.mayor = mayor;
    }
}

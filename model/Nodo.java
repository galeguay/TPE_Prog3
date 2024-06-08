package tpe.model;

import java.util.ArrayList;
import java.util.Iterator;


public class Nodo {
    private Tarea tarea;
    private Nodo menor;
    private Nodo mayor;
    private ArrayList<Tarea> iguales = new ArrayList<>();

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

    public void agregarIgual(Tarea t){
        iguales.add(t);
    }

    public Iterator<Tarea> getIguales(){
        return this.iguales.iterator();
    }

    public boolean tieneIguales(){
        return !this.iguales.isEmpty();
    }

}

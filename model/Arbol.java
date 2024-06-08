package tpe.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Arbol {
    private Nodo root;
    private ArrayList<Nodo> nodos;

    public void add(Tarea tarea) {
        if (this.root == null)
            this.root = new Nodo(tarea);
        else
            this.add(this.root, tarea);
    }

    private void add(Nodo actual, Tarea tarea) {

        if (actual.getTarea().getPrioridad() > tarea.getPrioridad()) {
            if (actual.getMenor() == null) {
                Nodo temp = new Nodo(tarea);
                actual.setMenor(temp);
            } else {
                add(actual.getMenor(), tarea);
            }
        } else if (actual.getTarea().getPrioridad() < tarea.getPrioridad()) {
            if (actual.getMayor() == null) {
                Nodo temp = new Nodo(tarea);
                actual.setMayor(temp);
            } else {
                add(actual.getMayor(), tarea);
            }
        } else if (actual.getTarea().getPrioridad() == tarea.getPrioridad()) {
            actual.agregarIgual(tarea);
        }
    }

    public void printPreOrder() {
        printPreOrder(this.root);
    }

    private void printPreOrder(Nodo nodo) {
        if (nodo != null) {
            printPreOrder(nodo.getMenor());
            System.out.println(nodo.getTarea().toString());
            printPreOrder(nodo.getMayor());
        }
    }

    public List<Tarea> enlistarRangoPrioridad(int prioridadInferior, int prioridadSuperior) {
        ArrayList<Tarea> tareasFiltradas = new ArrayList<>();
        enlistarRangoRecu(root, tareasFiltradas, prioridadInferior, prioridadSuperior);
        return tareasFiltradas;
    }

    private void enlistarRangoRecu(Nodo nodo, List<Tarea> tareasFiltradas, int piso, int techo) {
        if (nodo != null) {
            int prioridadActual = nodo.getTarea().getPrioridad();
            if ((prioridadActual >= piso) && (prioridadActual <= techo)) {
                tareasFiltradas.add(nodo.getTarea());
                if (nodo.tieneIguales()) {
                    Iterator<Tarea> tareasIguales = nodo.getIguales();
                    while (tareasIguales.hasNext()) {
                        Tarea tarea = tareasIguales.next();
                        tareasFiltradas.add(tarea);
                    }
                }
            }
            enlistarRangoRecu(nodo.getMenor(), tareasFiltradas, piso, techo);
            enlistarRangoRecu(nodo.getMayor(), tareasFiltradas, piso, techo);
        }

    }
}

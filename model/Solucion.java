package tpe.model;

import java.util.ArrayList;
import java.util.HashMap;

import static tpe.utils.TextColor.GREEN;

public class Solucion {

    HashMap<String, ArrayList<Tarea>> solucion = new HashMap<>();
    int metrica;
    int tiempoSolucion;

    public HashMap<String, ArrayList<Tarea>> getHashSolucion() {
        return solucion;
    }

    public void setSolucion(HashMap<String, ArrayList<Tarea>> solucion) {
        this.solucion = solucion;
        setTiempoSolucion(calcularTiempo());
    }

    public int getMetrica() {
        return metrica;
    }

    public void setMetrica(int metrica) {
        this.metrica = metrica;
    }

    public void sumarMetrica(){
        this.metrica++;
    }

    public int getTiempoSolucion() {
        return tiempoSolucion;
    }

    public void setTiempoSolucion(int tiempoSolucion) {
        this.tiempoSolucion = tiempoSolucion;
    }

    private Integer calcularTiempo() {
        Integer maxCarga = 0;
        for (String procesador : solucion.keySet()) {
            Integer suma = 0;
            for (Tarea t : solucion.get(procesador)) {
                suma += t.getTiempoDeEjecucion();
            }
            if (suma > maxCarga) {
                maxCarga = suma;
            }
        }
        return maxCarga;
    }

    @Override
    public String toString() {
        return GREEN + " + \"FINAL DE BACKTRACKING\"" +
                "\nSolución: " + solucion +
                "\nMétrica=" + metrica +
                "\nTiempo de solución obtenida: " + tiempoSolucion;
    }


}